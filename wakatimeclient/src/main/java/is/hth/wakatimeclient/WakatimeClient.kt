package `is`.hth.wakatimeclient

import `is`.hth.wakatimeclient.core.data.auth.*
import `is`.hth.wakatimeclient.core.data.auth.AuthClientImpl
import `is`.hth.wakatimeclient.core.data.auth.DefaultAuthenticator
import `is`.hth.wakatimeclient.core.data.net.NetworkClient
import `is`.hth.wakatimeclient.core.data.net.NetworkClientImpl
import `is`.hth.wakatimeclient.wakatime.SessionManager
import `is`.hth.wakatimeclient.wakatime.SessionManagerImpl
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeErrorProcessor
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeNetworkClient
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeRemoteDataSource
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeRemoteDataSourceImpl
import android.content.Context
import android.net.Uri
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber

/**
 *
 */
@Suppress("unused")
class WakatimeClient private constructor(
    private val auth: AuthClientImpl,
    private val net: WakatimeNetworkClient,
    private val session: SessionManager,
    private val remote: WakatimeRemoteDataSource
) : WakatimeRemoteDataSource by remote,
    SessionManager by session,
    AuthClient by auth {

    class Builder private constructor(
        secret: String = "",
        appId: String = "",
        apiKey: String = "",
        redirectUri: Uri = Uri.parse(""),
        method: Method
    ) {

        init {
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            } else {
                Timber.plant(Timber.asTree())
            }
        }

        /**
         * The client will be configured to use basic authentication with
         * the supplied base64 encoded api key.
         */
        constructor(base64EncodedApiKey: String) : this(
            apiKey = base64EncodedApiKey,
            method = Method.ApiKey
        )

        /**
         * The client will be configured to use OAuth 2.0 with the supplied
         * values.
         */
        constructor(
            secret: String,
            appId: String,
            redirectUri: Uri
        ) : this(
            secret = secret,
            appId = appId,
            redirectUri = redirectUri,
            method = Method.OAuth
        )

        private val config: AuthConfig = AuthConfig(
            clientSecret = secret,
            appId = appId,
            redirectUri = redirectUri,
            host = Uri.parse("https://wakatime.com"),
            method = method
        )
        private val netBuilder = NetworkClientImpl.Builder("https://wakatime.com/api/v1/")
        private val authBuilder = AuthClientImpl.Builder(apiKey, config)

        /**
         * Configure the [AuthClient] that will be used for authentication against Wakatime's API
         */
        fun authenticator(action: (AuthClient.Builder.() -> Unit)): Builder = apply { action(authBuilder) }

        /**
         * Configure the [NetworkClient] that will be used for interacting against Wakatime's API
         */
        fun network(action: (NetworkClient.Builder.() -> Unit)): Builder = apply { action(netBuilder) }

        /**
         * Constructs a [WakatimeClient] based on the current configuration
         */
        @ExperimentalSerializationApi
        fun build(context: Context, storage: AuthStorage): WakatimeClient {
            val authClient: AuthClientImpl = authBuilder.build(context, storage)
            val netClient: NetworkClient = netBuilder
                .setAuthenticatorIfNeeded(DefaultAuthenticator(authClient.session()))
                .build()

            val network = WakatimeNetworkClient(netClient, WakatimeErrorProcessor())

            val manager = SessionManagerImpl(
                config = config,
                storage = authClient.storage,
                session = authClient.session(),
                oauthApi = network.oauthApi(),
                netProcessor = network.processor(),
            )

            val remoteSource: WakatimeRemoteDataSource = WakatimeRemoteDataSourceImpl(
                session = authClient.session(),
                api = network.api(),
                processor = network.processor(),
            )

            return WakatimeClient(
                auth = authClient,
                net = network,
                remote = remoteSource,
                session = manager
            )
        }
    }
}