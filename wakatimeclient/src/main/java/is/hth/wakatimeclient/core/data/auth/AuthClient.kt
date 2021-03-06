package `is`.hth.wakatimeclient.core.data.auth

import `is`.hth.wakatimeclient.core.data.*
import android.content.Context
import android.content.Intent
import net.openid.appauth.*
import net.openid.appauth.browser.AnyBrowserMatcher
import net.openid.appauth.browser.BrowserMatcher
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public interface AuthClient {

    /**
     * Creates a new [Intent] for starting a new OAuth flow against Wakatime.
     * The [scopes] determine what access level the resulting authentication will have.
     */
    public fun createAuthenticationIntent(scopes: List<Scope>): Intent

    /**
     * Processes the received [Intent] results from the OAuth flow started
     * with calling [createAuthenticationIntent]
     */
    public suspend fun onAuthenticationResult(result: Intent): Results<Boolean>

    /**
     * The current user session
     */
    public fun session(): Session

    /**
     * Exposes utility functionality for the ongoing session
     */
    public interface Session {

        /**
         * Indicates if the client is currently authorized
         */
        public fun isAuthorized(): Boolean

        /**
         * Indicates if the client is configured to use OAuth.
         */
        public fun authenticationMethod(): Method?

        /**
         * The list of scopes that the user has authorized the client to have access to, if any.
         */
        public fun authorizedScopes(): Set<Scope>

        /**
         * The session's access token if any exists
         */
        public fun accessToken(): String

        /**
         * The session's refresh token if any exists
         */
        public fun refreshToken(): String

        /**
         * The API key being used if any exists
         */
        public fun apiKey(): String

        /**
         * Updates the access token if needed for the current session
         */
        public suspend fun update(force: Boolean): Results<Unit>
    }

    public interface Builder {

        /**
         * For setting custom filtering requirements on which browsers are allowed
         */
        public fun setBrowserMatcher(matcher: BrowserMatcher): Builder
    }
}

internal class AuthClientImpl internal constructor(
    private val config: AuthConfig,
    internal val storage: AuthStorageWrapper,
    private val authService: AuthorizationService
) : AuthClient {

    private val session: AuthClient.Session = SessionImpl(storage, authService)

    override fun createAuthenticationIntent(scopes: List<Scope>): Intent {
        val serviceConfig = AuthorizationServiceConfiguration(
            config.authorizationEndpoint,
            config.tokenEndpoint
        )
        val joined = scopes.joinToString { it.toString() }
        val request = AuthorizationRequest.Builder(
            serviceConfig,
            config.clientId,
            ResponseTypeValues.CODE,
            config.redirectUri
        ).setScopes(joined).build()

        val intent = authService.createCustomTabsIntentBuilder(request.toUri()).build()
        return authService.getAuthorizationRequestIntent(request, intent)
    }

    override suspend fun onAuthenticationResult(result: Intent): Results<Boolean> {
        return suspendCoroutine { continuation ->
            val response = AuthorizationResponse.fromIntent(result)
            val exception = AuthorizationException.fromIntent(result)

            storage.update {
                it.update(response, exception)
            }
            when {
                response == null && exception != null -> {
                    val code = exception.code
                    val message = exception.message ?: "No exception message given!"
                    continuation.resume(Failure(Error.Authentication.Authorization(code, message)))
                }
                response != null -> fetchAuthorizationToken(config, response) {
                    continuation.resume(it)
                }
                else -> {
                    val message = "Authentication flow resulted in neither actionable response nor exception!"
                    continuation.resume(Failure(Error.Authentication.Unknown(-1, message)))
                }
            }
        }
    }

    override fun session(): AuthClient.Session = session

    //
    //                      Private API implementation
    //////////////////////////////////////////////////////////////////////////

    private fun fetchAuthorizationToken(
        config: AuthConfig,
        response: AuthorizationResponse,
        receiver: (Results<Boolean>) -> Unit
    ) {
        val map = hashMapOf(Pair("client_secret", config.clientSecret))
        val request = response.createTokenExchangeRequest(map)
        authService.performTokenRequest(request) { result, exception ->
            val state = storage.update {
                it.update(result, exception)
            }
            val results = when {
                state.isAuthorized && exception == null -> Success(true)
                exception != null -> {
                    val message = exception.message ?: "No exception message given!"
                    Failure(Error.Authentication.TokenFetch(exception.code, message))
                }
                else -> {
                    val message = "Token fetch operation resulted in neither success nor failure!"
                    Failure(Error.Authentication.Unknown(-1, message))
                }
            }
            receiver(results)
        }
    }

    //
    //                      Internal class definitions
    //////////////////////////////////////////////////////////////////////////

    private class SessionImpl(
        private val storage: AuthStorageWrapper,
        private val service: AuthorizationService
    ) : AuthClient.Session {

        override fun isAuthorized(): Boolean {
            return when (authenticationMethod()) {
                null -> false
                Method.ApiKey -> apiKey().isNotEmpty()
                Method.OAuth -> storage.getState().isAuthorized
            }
        }

        override fun authorizedScopes(): Set<Scope> {
            val scopes = storage.getState().scopeSet?.joinToString(separator = ",") { it } ?: ""
            return Scope.extractScopes(scopes)
        }

        override fun accessToken(): String = storage.getState().accessToken ?: ""

        override fun refreshToken(): String = storage.getState().refreshToken ?: ""

        override fun apiKey(): String = storage.getKey()

        override suspend fun update(force: Boolean): Results<Unit> {
            return suspendCoroutine { continuation ->
                if (storage.getMethod() == Method.OAuth) {
                    val state = storage.getState()

                    // if forcing a token update mark it so
                    state.needsTokenRefresh = if (force) force else state.needsTokenRefresh

                    when {
                        state.needsTokenRefresh -> {
                            state.performActionWithFreshTokens(service) { _, _, exception ->
                                val results = if (exception == null) {
                                    Success(Unit)
                                } else {
                                    val message: String = exception.message ?: "Token refresh operation failed"
                                    val error = Error.Authentication.TokenRefresh(exception.code, message)
                                    Failure(error)
                                }
                                continuation.resumeWith(Result.success(results))
                            }
                        }
                        state.isAuthorized -> {
                            continuation.resumeWith(Result.success(Success(Unit)))
                        }
                        else -> {
                            val msg = "No authentication found! Halting token refresh operation"
                            val result = Failure(Error.Authentication.Unauthorized(msg))
                            continuation.resumeWith(Result.success(result))
                        }
                    }
                } else continuation.resumeWith(Result.success(Success(Unit)))
            }
        }

        override fun authenticationMethod(): Method? = storage.getMethod()
    }

    internal class Builder(
        private val apiKey: String,
        private val config: AuthConfig
    ) : AuthClient.Builder {

        private var matcher: BrowserMatcher = AnyBrowserMatcher.INSTANCE

        override fun setBrowserMatcher(matcher: BrowserMatcher): AuthClient.Builder = apply { this.matcher = matcher }

        internal fun build(context: Context, storage: AuthStorage): AuthClientImpl {
            val wrapper = AuthStorageWrapper(storage)
            wrapper.setMethod(config.method)

            if (config.method == Method.ApiKey) {
                wrapper.setKey(apiKey)
            }

            val appauthConfig = AppAuthConfiguration.Builder()
                .setBrowserMatcher(matcher)
                .build()

            return AuthClientImpl(
                config = config,
                storage = wrapper,
                authService = AuthorizationService(context.applicationContext, appauthConfig)
            )
        }
    }
}