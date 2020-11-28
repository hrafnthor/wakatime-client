package `is`.hth.wakatimeclient.core.data.auth

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.Results
import android.content.Context
import android.content.Intent
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

interface AuthClient {

    /**
     * Creates a new [Intent] for starting a new OAuth flow against Wakatime.
     * The [scopes] determine what access level the resulting authentication will have.
     */
    fun createAuthenticationIntent(scopes: List<Scope>): Intent

    /**
     * Processes the received [Intent] results from the OAuth flow started
     * with calling [createAuthenticationIntent]
     */
    suspend fun onAuthenticationResult(result: Intent): Boolean

    /**
     *
     */
    fun authenticator(): Authenticator

    /**
     *
     */
    fun session(): Session

    /**
     * Exposes utility functionality for the ongoing session
     */
    interface Session {

        /**
         * Indicates if the client is currently authorized
         */
        fun isAuthorized(): Boolean

        /**
         * Indicates if the client is configured to use OAuth.
         */
        fun authenticationMethod(): Method

        /**
         * The list of scopes that the user has authorized the client to have access to, if any.
         */
        fun authorizedScopes(): Set<Scope>

        /**
         * The session's access token if any exists
         */
        fun accessToken(): String

        /**
         * The session's refresh token if any exists
         */
        fun refreshToken(): String

        /**
         * The API key being used if any exists
         */
        fun apiKey(): String

        /**
         * Updates the access token if needed for the current session
         */
        suspend fun update(force: Boolean): Results<Unit>
    }

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
        fun setStorage(storage: AuthStorage): Builder
    }
}

/**
 *
 */
internal class AuthClientImpl internal constructor(
    private val config: AuthConfig,
    internal val storage: AuthStorage,
    private val authService: AuthorizationService
) : AuthClient {

    private val session: AuthClient.Session = SessionImpl(storage, authService)
    private val authenticator: Authenticator = AuthenticatorImpl(session)

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

        val intent = authService.createCustomTabsIntentBuilder(request.toUri()).build()
        return authService.getAuthorizationRequestIntent(request, intent)
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

    override fun authenticator(): Authenticator = authenticator

    override fun session(): AuthClient.Session = session

    //
    //                      Private API implementation
    //////////////////////////////////////////////////////////////////////////

    private fun fetchAuthorizationToken(
        config: AuthConfig,
        response: AuthorizationResponse,
        receiver: (Result<Boolean>) -> Unit
    ) {
        val map = hashMapOf(Pair("client_secret", config.clientSecret))
        val request = response.createTokenExchangeRequest(map)
        authService.performTokenRequest(request) { result, exception ->
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

    private class AuthenticatorImpl(
        private val session: AuthClient.Session
    ) : Authenticator {

        override fun authenticate(route: Route?, response: Response): Request? {
            val authorizationHeader = "Authorization"
            return if (response.header(authorizationHeader) == null && session.isAuthorized()) {
                // Authorization exists and has not been attempted for this request yet
                val header = when (session.authenticationMethod()) {
                    is Method.OAuth -> "Bearer ${session.accessToken()}"
                    is Method.ApiKey -> "Basic ${session.apiKey()}"
                }
                response.request()
                    .newBuilder()
                    .header(authorizationHeader, header)
                    .build()
            } else null
        }
    }

    private class SessionImpl(
        private val storage: AuthStorage,
        private val service: AuthorizationService
    ) : AuthClient.Session {

        override fun isAuthorized(): Boolean {
            return when (authenticationMethod()) {
                Method.ApiKey -> apiKey().isNotEmpty()
                Method.OAuth -> getState().isAuthorized
            }
        }

        override fun authorizedScopes(): Set<Scope> {
            val scopes = getState().scopeSet?.joinToString(separator = ",") { it } ?: ""
            return Scope.extractScopes(scopes)
        }

        override fun accessToken(): String = getState().accessToken ?: ""

        override fun refreshToken(): String = getState().refreshToken ?: ""

        override fun apiKey(): String = storage.getKey() ?: ""

        override suspend fun update(force: Boolean): Results<Unit> = suspendCoroutine {
            if (storage.getMethod() is Method.OAuth) {
                with(storage.getState()) {
                    // Mark if token should be refreshed no matter its state
                    needsTokenRefresh = force
                    when {
                        needsTokenRefresh -> performActionWithFreshTokens(service) { _, _, exception ->
                            val results = if (exception == null) {
                                Results.Success.Empty
                            } else {
                                val message: String = exception.message ?: ""
                                val error = Error.Authentication.TokenRefresh(message)
                                Results.Failure(error)
                            }
                            it.resumeWith(Result.success(results))
                        }
                        isAuthorized -> {
                            // Authentication is valid and does not need refreshing
                            it.resumeWith(Result.success(Results.Success.Empty))
                        }
                        else -> {
                            // No authentication was found
                            val msg = "No authentication found! Halting token refresh operation"
                            val result = Results.Failure(Error.Authentication.Unauthorized(msg))
                            it.resumeWith(Result.success(result))
                        }
                    }
                }
            } else it.resumeWith(Result.success(Results.Success.Empty))
        }

        private fun getState(): AuthState = storage.getState()

        override fun authenticationMethod(): Method = storage.getMethod()
    }

    /**
     *
     */
    internal class Builder(
        private val apiKey: String,
        private val config: AuthConfig
    ) : AuthClient.Builder {

        private lateinit var storage: AuthStorage
        private var matcher: BrowserMatcher = AnyBrowserMatcher.INSTANCE

        override fun setBrowserMatcher(matcher: BrowserMatcher): AuthClient.Builder =
            apply { this.matcher = matcher }

        override fun setStorage(storage: AuthStorage): AuthClient.Builder =
            apply { this.storage = storage }

        internal fun build(context: Context): AuthClientImpl {
            if (this::storage.isInitialized.not()) {
                storage = DefaultAuthStorage.construct(context.applicationContext)
            }

            storage.setMethod(config.method)
            storage.setKey(apiKey)

            val appauthConfig = AppAuthConfiguration.Builder()
                .setBrowserMatcher(matcher)
                .build()

            return AuthClientImpl(
                config = config,
                storage = storage,
                authService = AuthorizationService(context.applicationContext, appauthConfig)
            )
        }
    }
}