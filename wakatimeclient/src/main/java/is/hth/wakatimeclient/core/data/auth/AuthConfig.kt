package `is`.hth.wakatimeclient.core.data.auth

import android.net.Uri

data class AuthConfig(
    /**
     * The client id which will be used during an OAuth 2.0 flow,
     * as given by Wakatime's app dashboard
     */
    val clientId: String,
    /**
     * The client secret which will be used during an OAuth 2.0 flow,
     * as given by Wakatime's app dashboard
     */
    val clientSecret: String,
    /**
     * The redirect uri's which will be used during an OAuth 2.0 flow,
     * as configured on Wakatime's app dashboard
     */
    val redirectUri: Uri,
    /**
     * The host with which the client will communicate with.
     */
    val host: Uri,
    /**
     * The authentication method which is being used
     */
    val method: Method,
    /**
     * The remote endpoint which will be used during authentication
     */
    val authorizationEndpoint: Uri = host.buildUpon().appendPath("oauth/authorize").build(),
    /**
     * The remote endpoint which will be used during token operations
     */
    val tokenEndpoint: Uri = host.buildUpon().appendPath("oauth/token").build()
)