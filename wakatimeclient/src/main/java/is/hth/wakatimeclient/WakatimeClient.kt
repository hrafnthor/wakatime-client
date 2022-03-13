package `is`.hth.wakatimeclient

import `is`.hth.wakatimeclient.core.data.auth.*
import `is`.hth.wakatimeclient.core.data.auth.AuthClientImpl
import `is`.hth.wakatimeclient.core.data.auth.DefaultAuthenticator
import `is`.hth.wakatimeclient.core.data.net.CacheControl
import `is`.hth.wakatimeclient.core.data.net.NetworkClient
import `is`.hth.wakatimeclient.core.data.net.NetworkClientImpl
import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import `is`.hth.wakatimeclient.wakatime.SessionManager
import `is`.hth.wakatimeclient.wakatime.SessionManagerImpl
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeErrorProcessor
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeNetworkClient
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeRemoteDataSource
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeRemoteDataSourceImpl
import android.content.Context
import android.net.Uri

@Suppress("unused")
public class WakatimeClient private constructor(
    private val authentication: AuthClientImpl,
    private val network: WakatimeNetworkClient,
    private val session: SessionManager,
    private val remote: WakatimeRemoteDataSource
) : WakatimeRemoteDataSource by remote,
    SessionManager by session,
    CacheControl by network,
    AuthClient by authentication {

    public class Builder private constructor(
        clientSecret: String = "",
        clientId: String = "",
        apiKey: String = "",
        redirectUri: Uri = Uri.parse(""),
        method: Method
    ) {

        /**
         * The client will be configured to use basic authentication with
         * the supplied base64 encoded api key.
         */
        public constructor(base64EncodedApiKey: String) : this(
            apiKey = base64EncodedApiKey,
            method = Method.ApiKey
        )

        /**
         * The client will be configured to use OAuth 2.0 with the supplied
         * values.
         */
        public constructor(
            clientId: String,
            clientSecret: String,
            redirectUri: Uri
        ) : this(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = redirectUri,
            method = Method.OAuth
        )

        private val config: AuthConfig = AuthConfig(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = redirectUri,
            host = Uri.parse("https://wakatime.com"),
            method = method
        )
        private val netBuilder = NetworkClientImpl.Builder("https://wakatime.com/")
        private val authBuilder = AuthClientImpl.Builder(apiKey, config)

        /**
         * Configure the [AuthClient] that will be used for authentication against Wakatime's API
         */
        public fun authentication(action: (AuthClient.Builder.() -> Unit)): Builder = apply { action(authBuilder) }

        /**
         * Configure the [NetworkClient] that will be used for interacting against Wakatime's API
         */
        public fun network(action: (NetworkClient.Builder.() -> Unit)): Builder = apply { action(netBuilder) }

        /**
         * Constructs a [WakatimeClient] based on the current configuration
         */
        public fun build(context: Context, storage: AuthStorage): WakatimeClient {
            val json = WakatimeJsonFactory.json

            val authClient: AuthClientImpl = authBuilder.build(context, storage)
            val netClient: NetworkClient = netBuilder
                .setAuthenticatorIfNeeded(DefaultAuthenticator(authClient.session()))
                .build(json)

            val network = WakatimeNetworkClient(netClient, WakatimeErrorProcessor(json))

            val manager = SessionManagerImpl(
                config = config,
                storage = authClient.storage,
                session = authClient.session(),
                oauthApi = network.oauthApi(),
                processor = network.processor(),
            )

            val remoteSource: WakatimeRemoteDataSource = WakatimeRemoteDataSourceImpl(
                session = authClient.session(),
                api = network.api(),
                processor = network.processor(),
            )

            return WakatimeClient(
                authentication = authClient,
                network = network,
                remote = remoteSource,
                session = manager
            )
        }
    }
}