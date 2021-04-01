package `is`.hth.wakatimeclient.core.data.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

internal class DefaultAuthenticator(
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
            response.request
                .newBuilder()
                .header(authorizationHeader, header)
                .build()
        } else null
    }
}