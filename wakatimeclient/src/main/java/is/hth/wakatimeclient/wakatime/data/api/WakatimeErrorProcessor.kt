package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber

/**
 * Handles Wakatime specific error response payloads
 */
class WakatimeErrorProcessor(gson: Gson) : NetworkErrorProcessor(gson) {

    override fun onError(response: Response<*>): Error {
        return onError(response.code(), convert(response.errorBody()).message)
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
}