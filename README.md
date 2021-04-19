# wakatime-client
[![wakatime](https://wakatime.com/badge/github/hrafnthor/wakatime-client.svg)](https://wakatime.com/badge/github/hrafnthor/wakatime-client)

This project is a native Android wrapper ontop of the restful api supplied by the code activity tracker [Wakatime](https://www.wakatime.com).

## Setup

### Authentication

The client supports both an OAuth 2.0 flow (courtesy of [AppAuth](https://github.com/openid/AppAuth-Android)) as well as simply using an api key.

#### OAuth flow

When going for the OAuth 2.0 authentication flow, you first need to create a new application within Wakatime's [app dashboard](https://wakatime.com/apps).

Once created you'll need to supply the client instance with the `clientSecret`, `clientId` and `redirectUri` as shown below

```kotlin
WakatimeClient.Builder(
    clientId = "Your client's id",
    clientSecret = "Your client's secret",
    redirectUri = Uri.parse("Your client's redirect uri")
)....
```
Then register an `Activity` in the `AndroidManifest` which will receive the results from the OAuth flow and process them. If your redirectUri is 'myapplication://authentication-results' then configure the receiver as follows

```xml
 <activity android:name="net.openid.appauth.RedirectUriReceiverActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <data
            android:host="authentication-results"
            android:scheme="myapplication" />
    </intent-filter>
</activity>
```
Going for an OAuth flow like this will allow your implementation to be used by other users, logging in using their own credentials against Wakatime's services.

**Make sure that you do not bundle the id and secret within the application, as it is trivial to retrieve these values from an APK**
Rather have them be delivered to the client from a service you trust.

#### Api key

When going for an API key authentication, each user first needs to grab their api key from the [accounts settings](https://wakatime.com/settings/accounts) inside of Wakatime.

It can then be supplied to the client as shown below

```kotlin
WakatimeClient.Builder(
    base64EncodedApiKey = "Your personal API key"
)....
```
**Make sure that you do not bundle the API key within the application, as it is your own private one!** Each user will have to supply their own.

### Configuration

The client builder exposes the internal network client for further configuration such as adding network interceptors or configuring timeout limits

```kotlin
builder.network {
    val interceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    getOKHttpBuilder().apply {
        addInterceptor(interceptor)
        callTimeout(<Your value>, TimeUnit)
        // Here you have full access to the OkHttpClient.Builder
    }
    
    getRetrofitBuilder().apply {
        // Here you have full access to the Retrofit.Builder
    }
    
    // If network layer caching should be done by the client,
    // it can be configured by using the built in caching mechanism
    enableCache(context.cacheDir, cacheLifetimeInSeconds = 30)
}...
```

### Credential storage

Lastly the client requires an implementation of `AuthStorage` for storing credentials and related information.

```kotlin
builder.build(context, <Your implementation of AuthStorage>)
```
The reason this is left to the implementer is that there are multiple ways of securing authentication data on Android, and the best practices are constantly evolving and changing. By leaving this implementation detail out, this project allows for unforseen future changes without having to react to them it self.

## Usage

### Authentication

With the client configured, authentication becomes as easy as creating an authentication intent

```kotlin
val intent = client.createAuthenticationIntent(listOf(Scope.Email, Scope.ReadStats))
startActivityForResults(intent)
```

Then either in the calling context's onActivityResults or using the latest ActivityResultsLauncher
simply pass the received data on to the client

```kotlin
when(val authenticated = client.onAuthenticationResult(result)) {
    is Results.Failure -> displayError(results.error)
    is Results.Success -> {
        // Start fetching data
    }
}

```
### Retrieving data

With the authentication out of the way, sending a heartbeat for writing this markdown would not be much more complex than

```kotlin
val results: Results<Confirmation> = client.sendHeartbeat(Heartbeat.send(
    entity = "<ID>",
    time =0f, 
    type = Type.App, 
    category = Category.Documentation) {
        branch = "develop"
        project = "wakatime-client"
        language = "markdown"
})
```

Or fetching summaries for a specific project and its branch's over a defined period

```kotlin
val results: Results<Summaries> = client.getSummaries(Summaries.request(start, end) {
    timezone = end.timeZone.displayName
    project {
        projectName = "wakatime-client"
        branches("develop", "master")
    }
    meta { 
        writesOnly = true
    }
})
```
