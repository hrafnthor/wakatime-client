package `is`.hth.wakatimeclient.wakatime

import `is`.hth.wakatimeclient.core.data.*
import `is`.hth.wakatimeclient.core.data.auth.AuthClient
import `is`.hth.wakatimeclient.core.data.auth.AuthConfig
import `is`.hth.wakatimeclient.core.data.auth.AuthStorageWrapper
import `is`.hth.wakatimeclient.core.data.auth.Method
import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor
import `is`.hth.wakatimeclient.core.safeOperation
import `is`.hth.wakatimeclient.wakatime.data.api.OauthApi

public interface SessionManager {

    /**
     * Performs a logout operation, clearing the local cache and
     * stored authentication values as well as revoking the
     * authentication tokens on the remote server.
     *
     * If the remote token revoke operation fails, then this
     * operation fails, and returns a [Failure]
     *
     * The operation can be forced to run to completion even though
     * the remote token revoke operation fails. Any errors that
     * occur will be delivered in the final report.
     *
     * If the operation runs to completion it will return a
     * [Success] containing a [Report].
     */
    public suspend fun logout(force: Boolean): Results<Report>
}

/**
 * A detailed report on the result of the logout process
 */
public data class Report(
    /**
     * Indicates if the logout operation was forced
     */
    val forced: Boolean,
    /**
     * Any errors that occurred as part of the logout process.
     */
    val errors: Set<Error>
)

internal class SessionManagerImpl(
    private val config: AuthConfig,
    private val oauthApi: OauthApi,
    private val storage: AuthStorageWrapper,
    private val session: AuthClient.Session,
    private val processor: NetworkErrorProcessor
) : SessionManager {

    override suspend fun logout(force: Boolean): Results<Report> {
        val errors: MutableSet<Error> = mutableSetOf()
        if (session.isAuthorized() && session.authenticationMethod() == Method.OAuth) {
            // Revoke the access token
            val accessRevoke = revoke(config.clientId, config.clientSecret, session.accessToken())
            if (accessRevoke is Failure) {
                if (force) {
                    errors.add(accessRevoke.error)
                } else {
                    return accessRevoke
                }
            }

            // Revoke the refresh token
            val refreshRevoke = revoke(config.clientId, config.clientSecret, session.refreshToken())
            if (refreshRevoke is Failure) {
                if (force) {
                    errors.add(refreshRevoke.error)
                } else {
                    return refreshRevoke
                }
            }
            storage.clear()
        }

        return Success(Report(forced = force, errors = errors))
    }

    private suspend fun revoke(id: String, secret: String, token: String): Results<Unit> {
        return safeOperation(processor) {
            with(oauthApi.revoke(id, secret, token)) {
                when {
                    isSuccessful -> Success(Unit)
                    else -> Failure(errorBody()?.charStream().use {
                        processor.onNetworkError(
                            code = code(),
                            error = it?.readText() ?: message() ?: ""
                        )
                    })
                }
            }
        }
    }
}