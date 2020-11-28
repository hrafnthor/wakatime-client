package `is`.hth.wakatimeclient

import `is`.hth.wakatimeclient.core.data.auth.AuthClient
import `is`.hth.wakatimeclient.core.data.auth.AuthClientImpl
import `is`.hth.wakatimeclient.core.data.auth.AuthConfig
import `is`.hth.wakatimeclient.core.data.auth.Method
import `is`.hth.wakatimeclient.core.data.db.DbErrorProcessor
import `is`.hth.wakatimeclient.core.data.net.NetworkClient
import `is`.hth.wakatimeclient.core.data.net.NetworkClientImpl
import `is`.hth.wakatimeclient.core.util.RateLimiter
import `is`.hth.wakatimeclient.wakatime.*
import `is`.hth.wakatimeclient.wakatime.data.api.*
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeDatabase
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeDbClient
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeLocalDataSource
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeLocalDataSourceImpl
import android.content.Context
import android.net.Uri
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber
import java.util.concurrent.TimeUnit
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
    val users: UserRepo,
    val rankings: RankingRepo,
    val activity: ActivityRepo
) : AuthClient by auth,
    WakatimeNetworkClient by net,
    SessionManager by session {

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
            clientId: String,
            redirectUri: Uri
        ) : this(
            secret = secret,
            clientId = clientId,
            redirectUri = redirectUri,
            method = Method.OAuth
        )

        private var cacheLifetimeInSeconds: Int = 60
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
        fun cacheLifetimeInSeconds(minutes: Int): Builder =
            apply { cacheLifetimeInSeconds = abs(minutes) }

        /**
         * Constructs a [WakatimeClient] based on the current configuration
         */
        @ExperimentalSerializationApi
        fun build(context: Context): WakatimeClient {
            val db: WakatimeDatabase = WakatimeDatabase.getInstance(context.applicationContext)
            val dbClient = WakatimeDbClient(db, DbErrorProcessor())

            val authClient: AuthClientImpl = authBuilder.build(context)
            val netClient: NetworkClient = netBuilder
                .setAuthenticatorIfNeeded(authClient.authenticator())
                .also {
                    if (cacheLifetimeInSeconds > 0) {
                        it.enableCache(context.cacheDir, cacheLifetimeInSeconds)
                    }
                }.build()
            val wakaNetClient = WakatimeNetworkClientImpl(netClient, WakatimeErrorProcessor())
            val limiter = RateLimiter<String>(cacheLifetimeInSeconds, TimeUnit.SECONDS)

            val manager = SessionManagerImpl(
                config = config,
                dbClient = dbClient,
                storage = authClient.storage,
                session = authClient.session(),
                oauthApi = wakaNetClient.oauthApi(),
                netProcessor = wakaNetClient.processor(),
            )

            val localSource: WakatimeLocalDataSource = WakatimeLocalDataSourceImpl(
                db = dbClient.wakatimeDatabase,
                processor = dbClient.processor
            )

            val remoteSource: WakatimeRemoteDataSource = WakatimeRemoteDataSourceImpl(
                session = authClient.session(),
                api = wakaNetClient.api(),
                processor = wakaNetClient.processor(),
            )

            val users: UserRepo = UserRepoImpl(
                limiter = limiter,
                remote = remoteSource,
                local = localSource
            )

            val rankings: RankingRepo = RankingRepoImpl(
                limiter = limiter,
                remote = remoteSource,
                local = localSource
            )

            val activity: ActivityRepo = ActivityRepoImpl(
                limiter = limiter,
                remote = remoteSource,
                local = localSource
            )

            return WakatimeClient(
                auth = authClient,
                net = wakaNetClient,
                db = dbClient,
                session = manager,
                users = users,
                rankings = rankings,
                activity = activity
            )
        }
    }
}