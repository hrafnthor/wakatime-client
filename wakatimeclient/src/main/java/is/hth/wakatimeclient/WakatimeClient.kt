package `is`.hth.wakatimeclient

import `is`.hth.wakatimeclient.core.data.DbErrorFactory
import `is`.hth.wakatimeclient.core.data.NetworkClient
import `is`.hth.wakatimeclient.core.data.NetworkClientImpl
import `is`.hth.wakatimeclient.core.data.NetworkErrorFactory
import `is`.hth.wakatimeclient.core.data.auth.AuthClient
import `is`.hth.wakatimeclient.core.data.auth.AuthClientImpl
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeDatabase
import `is`.hth.wakatimeclient.wakatime.data.users.UserInjector
import `is`.hth.wakatimeclient.wakatime.data.users.UserRepository
import android.content.Context
import kotlin.math.abs

/**
 *
 */
class WakatimeClient private constructor(
    private val authClient: AuthClient,
    private val netClient: NetworkClient,
    private val users: UserRepository
) : AuthClient by authClient, UserRepository by users {

    /**
     *
     */
    class Builder(
        secret: String,
        clientId: String,
        redirectUri: String
    ) {

        private var cacheLifetimeMinutes: Int = 5
        private val authBuilder = AuthClientImpl.Builder(secret, clientId, redirectUri)
        private val netBuilder = NetworkClientImpl.Builder("https://www.wakatime.com")

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
            val networkFactory = NetworkErrorFactory()
            val dbErrorFactory = DbErrorFactory()

            val authClient = authBuilder.build(context.applicationContext)
            val netClient = netBuilder.build(authClient.getAuthenticator())
            val service = netClient.getService()
            val db = WakatimeDatabase.getInstance(context.applicationContext)

            val users = UserInjector.provideRepository(
                cacheLifetimeMinutes,
                service,
                db,
                networkFactory,
                dbErrorFactory
            )

            return WakatimeClient(authClient, netClient, users)
        }
    }
}