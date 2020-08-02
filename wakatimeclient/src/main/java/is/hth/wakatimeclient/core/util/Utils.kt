package `is`.hth.wakatimeclient.core.util

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.Results
import retrofit2.Response

/**
 * Executes a suspending operation and wraps its resulting value in a [Results]
 */
suspend fun <T : Any> valuesOrEmpty(
    /**
     * The operation to execute
     */
    operation: suspend () -> T?
): Results<T> {
    return operation()?.let {
        Results.Values(it)
    } ?: Results.Empty()
}

/**
 * Structured execution and result handling for network operations
 */
suspend fun <T : Any, R : Any> networkOperation(
    /**
     * Execute the network operation
     */
    operation: suspend () -> Response<T>,
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
    val response = operation()
    return when {
        response.isSuccessful -> {
            response.body()?.let {
                Results.Values(convert(it))
            } ?: Results.Empty()
        }
        else -> Results.Failure(error(response.code()))
    }
}

/**
 * Wrap a suspending [operation] in try/catch. In case an exception is thrown,
 * a [Results.Failure] is created containing the [error] produced.
 */
suspend fun <T : Any> safeOperation(
    /**
     * A long running operation that might throw an exception
     */
    operation: suspend () -> Results<T>,
    /**
     * In case of an exception being thrown, convert it to an [Error] object
     */
    error: (Exception) -> Error
): Results<T> {
    return try {
        operation()
    } catch (e: Exception) {
        Results.Failure(error(e))
    }
}