package `is`.hth.wakatimeclient

import `is`.hth.wakatimeclient.core.data.net.NetworkClient
import `is`.hth.wakatimeclient.core.data.net.NetworkClientImpl
import `is`.hth.wakatimeclient.core.data.auth.AuthClient
import `is`.hth.wakatimeclient.core.data.auth.AuthClientImpl
import `is`.hth.wakatimeclient.core.data.auth.AuthConfig
import `is`.hth.wakatimeclient.core.data.auth.Method
import `is`.hth.wakatimeclient.core.data.db.DbErrorProcessor
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeErrorProcessor
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeNetworkClient
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeNetworkClientImpl
import `is`.hth.wakatimeclient.wakatime.data.auth.SessionManager
import `is`.hth.wakatimeclient.wakatime.data.auth.SessionManagerImpl
import `is`.hth.wakatimeclient.wakatime.data.db.MasterDao
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeDatabase
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeDbClient
import `is`.hth.wakatimeclient.wakatime.data.users.UserInjector
import `is`.hth.wakatimeclient.wakatime.data.users.UserRepository
import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import kotlin.math.abs

/**
 *
 */
@Suppress("unused")
class WakatimeClient private constructor(
    private val auth: AuthClientImpl,
    private val net: WakatimeNetworkClient,
    private val db: WakatimeDbClient,
    private val session: SessionManager,
    val users: UserRepository
) : AuthClient by auth,
    WakatimeNetworkClient by net,
    SessionManager by session,
    MasterDao by db {

    /**
     *
     */
    class Builder private constructor(
        secret: String = "",
        clientId: String = "",
        private val apiKey: String = "",
        redirectUri: Uri = Uri.parse(""),
        method: Method
    ) {

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
            clientId: String,
            redirectUri: Uri
        ) : this(
            secret = secret,
            clientId = clientId,
            redirectUri = redirectUri,
            method = Method.OAuth
        )

        private var cacheLifetimeMinutes: Int = 5
        private val config: AuthConfig = AuthConfig(
            clientSecret = secret,
            clientId = clientId,
            redirectUri = redirectUri,
            host = Uri.parse("https://wakatime.com"),
            method = method
        )
        private val netBuilder = NetworkClientImpl.Builder("https://wakatime.com")
        private val authBuilder = AuthClientImpl.Builder(apiKey, config)

        /**
         * Configure the [AuthClient] that will be used for authentication against Wakatime's API
         */
        fun authenticator(action: (AuthClient.Builder.() -> Unit)): Builder =
            apply { action(authBuilder) }

        /**
         * Configure the [NetworkClient] that will be used for interacting against Wakatime's API
         */
        fun network(action: (NetworkClient.Builder.() -> Unit)): Builder =
            apply { action(netBuilder) }

        /**
         * Assigns the global cache lifetime in minutes used to determine if new
         * values should be fetched over the network. The default value is 5 minutes.
         */
        fun cacheLifetimeInMinutes(minutes: Int): Builder =
            apply { cacheLifetimeMinutes = abs(minutes) }

        /**
         * Constructs a [WakatimeClient] based on the current configuration
         */
        fun build(context: Context): WakatimeClient {
            val db = WakatimeDatabase.getInstance(context.applicationContext)
            val dbClient = WakatimeDbClient(db, DbErrorProcessor())

            val authClient = authBuilder.build(context)

            val netClient = netBuilder.setAuthenticatorIfNeeded(authClient.authenticator()).build()
            val wakaNetClient = WakatimeNetworkClientImpl(netClient, WakatimeErrorProcessor(Gson()))

            val manager = SessionManagerImpl(
                config = config,
                dbClient = dbClient,
                storage = authClient.storage,
                session = authClient.session(),
                oauthApi = wakaNetClient.oauthApi(),
                netProcessor = wakaNetClient.processor()
            )

            val users = UserInjector.provideRepository(
                cacheLimit = cacheLifetimeMinutes,
                session = authClient.session(),
                dbClient = dbClient,
                netClient = wakaNetClient
            )

            return WakatimeClient(
                auth = authClient,
                net = wakaNetClient,
                db = dbClient,
                session = manager,
                users = users
            )
        }
    }
}