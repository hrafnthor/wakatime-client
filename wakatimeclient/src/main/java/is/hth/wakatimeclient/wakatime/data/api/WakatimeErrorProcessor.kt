package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber

/**
 * Handles Wakatime specific error response payloads
 */
internal class WakatimeErrorProcessor : NetworkErrorProcessor() {

    override fun onError(response: Response<*>): Error {
        val serviceError = convert(response.errorBody())
        return onError(response.code(), serviceError.message).apply {
            serviceError.fieldErrors.forEach {
                extra.add("Field '${it.name}' had error '${it.description}'")
            }
        }
    }

    private fun convert(errorBody: ResponseBody?): ServiceError {
        return errorBody?.let {
            try {
                Json.decodeFromString<ServiceError>(it.string())
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        } ?: ServiceError("")
    }
}