package `is`.hth.wakatimeclient.wakatime

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.auth.AuthClient
import `is`.hth.wakatimeclient.core.data.auth.AuthConfig
import `is`.hth.wakatimeclient.core.data.auth.AuthStorage
import `is`.hth.wakatimeclient.core.data.auth.Method
import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor
import `is`.hth.wakatimeclient.core.safeOperation
import `is`.hth.wakatimeclient.wakatime.data.api.OauthApi

interface SessionManager {

    /**
     * Performs a logout operation, clearing the local cache and
     * stored authentication values as well as revoking the
     * authentication tokens on the remote server.
     *
     * If the remote token revoke operation fails, then this
     * operation fails, and returns a [Results.Failure]
     *
     * The operation can be forced to run to completion even though
     * the remote token revoke operation fails. Any errors that
     * occur will be delivered in the final report.
     *
     * If the operation runs to completion it will return a
     * [Results.Success.Value] containing a [Report].
     */
    suspend fun logout(force: Boolean): Results<Report>
}

/**
 * A detailed report on the result of the logout process
 */
data class Report(
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
    private val storage: AuthStorage,
    private val session: AuthClient.Session,
    private val netProcessor: NetworkErrorProcessor
) : SessionManager {

    override suspend fun logout(force: Boolean): Results<Report> {
        val errors: MutableSet<Error> = mutableSetOf()
        if (session.isAuthorized() && session.authenticationMethod() is Method.OAuth) {
            // Revoke the access token
            val accessRevoke = revoke(config.clientId, config.clientSecret, session.accessToken())
            if (accessRevoke is Results.Failure) {
                if (force) {
                    errors.add(accessRevoke.error)
                } else {
                    return accessRevoke
                }
            }

            // Revoke the refresh token
            val refreshRevoke = revoke(config.clientId, config.clientSecret, session.refreshToken())
            if (refreshRevoke is Results.Failure) {
                if (force) {
                    errors.add(refreshRevoke.error)
                } else {
                    return refreshRevoke
                }
            }
            storage.resetAuthState()
        }

        return Results.Success.Value(Report(forced = force, errors = errors))
    }

    private suspend fun revoke(id: String, secret: String, token: String): Results<Nothing> {
        return safeOperation(netProcessor) {
            val response = oauthApi.revoke(id, secret, token)
            when {
                response.isSuccessful -> Results.Success.Empty
                else -> Results.Failure(netProcessor.onError(response))
            }
        }
    }
}