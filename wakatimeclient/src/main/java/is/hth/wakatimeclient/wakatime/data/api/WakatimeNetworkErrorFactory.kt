package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.NetworkErrorFactory
import `is`.hth.wakatimeclient.core.data.auth.Scope
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.net.HttpURLConnection

/**
 * Handles Wakatime specific error response payloads
 */
class WakatimeNetworkErrorFactory(
    private val gson: Gson
) : NetworkErrorFactory() {

    override fun onValue(value: Response<*>): Error {
        return if (value.code() == HttpURLConnection.HTTP_FORBIDDEN) {
            Error.Network.Forbidden(extractScopes(convert(value.errorBody()).message))
        } else onCode(value.code())
    }

    private fun convert(errorBody: ResponseBody?): ServiceError {
        return errorBody?.let {
            try {
                gson.fromJson<ServiceError>(
                    it.charStream(),
                    ServiceError::class.java
                )
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        } ?: ServiceError("")
    }

    private fun extractScopes(error: String): Set<Scope> {
        return mutableSetOf<Scope>().apply {
            Scope.scopes.forEach { scope ->
                if (error.contains(other = scope.key, ignoreCase = true)) {
                    add(scope.value)
                }
            }
        }
    }
}