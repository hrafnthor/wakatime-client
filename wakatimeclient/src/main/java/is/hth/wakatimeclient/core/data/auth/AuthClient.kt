package `is`.hth.wakatimeclient.core.data.auth

import `is`.hth.wakatimeclient.core.data.Reset
import `is`.hth.wakatimeclient.core.data.Resettable
import android.content.Context
import android.content.Intent
import android.net.Uri
import net.openid.appauth.*
import net.openid.appauth.browser.AnyBrowserMatcher
import net.openid.appauth.browser.BrowserMatcher
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface AuthClient : Resettable {

    /**
     * Indicates if the client is currently authorized
     */
    fun isAuthorized(): Boolean

    /**
     * The list of scopes that the user has authorized the client to have access to, if any.
     */
    fun authorizedScopes(): List<Scope>

    /**
     *
     */
    fun createAuthenticationIntent(scopes: List<Scope>): Intent

    /**
     *
     */
    suspend fun onAuthenticationResult(result: Intent): Boolean

    /**
     *
     */
    fun getAuthenticator(): Authenticator

    /**
     *
     */
    interface Builder {

        /**
         *
         */
        fun setBrowserMatcher(matcher: BrowserMatcher): Builder

        /**
         *
         */
        fun setStorage(storage: AuthStateStorage): Builder
    }
}

/**
 *
 */
internal class AuthClientImpl internal constructor(
    private val config: AuthConfig,
    private val storage: AuthStateStorage,
    private val service: AuthorizationService
) : AuthClient, Authenticator {

    private val authReset: Reset by lazy { AuthReset(storage) }

    override fun isAuthorized(): Boolean = storage.getState().isAuthorized

    override fun authorizedScopes(): List<Scope> =
        storage.getState().scopeSet?.map { Scope.scopes.getValue(it) } ?: listOf()

    override fun createAuthenticationIntent(scopes: List<Scope>): Intent {
        val serviceConfig = AuthorizationServiceConfiguration(
            config.authorizationEndpoint,
            config.tokenEndpoint
        )
        val joined = scopes.joinToString { it.name }
        val request = AuthorizationRequest.Builder(
            serviceConfig,
            config.clientId,
            ResponseTypeValues.CODE,
            config.redirectUri
        ).setScopes(joined).build()

        val intent = service.createCustomTabsIntentBuilder(request.toUri()).build()
        return service.getAuthorizationRequestIntent(request, intent)
    }

    override suspend fun onAuthenticationResult(result: Intent): Boolean =
        suspendCoroutine { continuation ->
            val response = AuthorizationResponse.fromIntent(result)
            val exception = AuthorizationException.fromIntent(result)

            storage.update {
                it.update(response, exception)
            }

            if (response == null) {
                Timber.e(exception)
                continuation.resume(false)
            } else fetchAuthorizationToken(config, response) {
                continuation.resume(it.isSuccess)
            }
        }

    override fun getAuthenticator(): Authenticator = this

    override fun getReset(): Reset = authReset

    //
    //                      OKHttp.Authenticator implementation
    //////////////////////////////////////////////////////////////////////////

    override fun authenticate(route: Route?, response: Response): Request? {
        val authorizationHeader = "Authorization"
        return if (response.header(authorizationHeader) == null && isAuthorized()) {
            // Authorization exists and has not been attempted for this request yet
            val header = getAuthorizationHeader()
            response.request()
                .newBuilder()
                .header(authorizationHeader, header)
                .build()
        } else null
    }

    //
    //                      Private API implementation
    //////////////////////////////////////////////////////////////////////////

    private fun getAuthorizationHeader(): String = "Bearer ${storage.getState().accessToken}"

    private fun fetchAuthorizationToken(
        config: AuthConfig,
        response: AuthorizationResponse,
        receiver: (Result<Boolean>) -> Unit
    ) {
        val map = hashMapOf(Pair("client_secret", config.clientSecret))
        val request = response.createTokenExchangeRequest(map)
        service.performTokenRequest(request) { result, exception ->
            val state = storage.update {
                it.update(result, exception)
            }

            if (exception == null) {
                receiver(Result.success(state.isAuthorized))
            } else {
                receiver(Result.failure(exception))
            }
        }
    }

    //
    //                      Internal class definitions
    //////////////////////////////////////////////////////////////////////////

    private class AuthReset(private val storage: AuthStateStorage) : Reset {

        override suspend fun reset(): Unit = storage.clear()
    }

    /**
     *
     */
    internal class Builder(
        clientSecret: String,
        clientId: String,
        redirectUri: String
    ) : AuthClient.Builder {

        private val config: AuthConfig = AuthConfig(
            clientSecret = clientSecret,
            clientId = clientId,
            redirectUri = Uri.parse(redirectUri),
            authorizationEndpoint = Uri.parse("https://wakatime.com/oauth/authorize"),
            tokenEndpoint = Uri.parse("https://wakatime.com/oauth/token")
        )
        private var storage: AuthStateStorage? = null
        private var matcher: BrowserMatcher = AnyBrowserMatcher.INSTANCE

        override fun setBrowserMatcher(matcher: BrowserMatcher): AuthClient.Builder =
            apply { this.matcher = matcher }

        override fun setStorage(storage: AuthStateStorage): AuthClient.Builder =
            apply { this.storage = storage }

        internal fun build(context: Context): AuthClient {
            val appauthConfig = AppAuthConfiguration.Builder().setBrowserMatcher(matcher).build()
            return AuthClientImpl(
                config = config,
                storage = storage ?: DefaultAuthStateStorage.construct(context),
                service = AuthorizationService(context, appauthConfig)
            )
        }
    }
}

/**
 *
 */
internal data class AuthConfig(
    /**
     *
     */
    val clientSecret: String,
    /**
     *
     */
    val clientId: String,
    /**
     *
     */
    val redirectUri: Uri,
    /**
     *
     */
    val authorizationEndpoint: Uri,
    /**
     *
     */
    val tokenEndpoint: Uri
)