package `is`.hth.wakatimeclient.core.util

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.Results
import retrofit2.Response

/**
 * Structured execution and result handling for network operations
 */
suspend fun <T : Any, R : Any> networkOperation(
    /**
     * Execute the network operation
     */
    load: suspend () -> Response<T>,
    /**
     * Perform any required payload conversion before delivering the final results
     */
    convert: (T) -> R,
    /**
     * In case the response does not indicate success attempt
     * to convert the response's code to an [Error]
     */
    error: (Int) -> Error
): Results<R> {
    val response = load()
    return when {
        response.isSuccessful -> {
            response.body()?.let {
                Results.Values(convert(it))
            } ?: Results.Empty()
        }
        else -> Results.Failure(error(response.code()))
    }
}