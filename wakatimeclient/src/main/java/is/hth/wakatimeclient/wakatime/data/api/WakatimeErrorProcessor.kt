package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Handles Wakatime specific error response payloads
 */
internal class WakatimeErrorProcessor(private val json: Json) : NetworkErrorProcessor() {

    override fun onNetworkError(code: Int, error: String): Error {
        val serviceError = convert(error)
        return onError(code, serviceError.message).apply {
            serviceError.fieldErrors.forEach {
                extra.add("Field '${it.name}' had error '${it.description}'")
            }
        }
    }

    private fun convert(error: String?): ServiceError {
        return error?.let {
            try {
                json.decodeFromString<ServiceError>(error)
            } catch (e: Exception) {
                null
            }
        } ?: ServiceError(error ?: "")
    }
}