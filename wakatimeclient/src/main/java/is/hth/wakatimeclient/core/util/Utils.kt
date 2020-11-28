package `is`.hth.wakatimeclient.core.util

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.ErrorProcessor
import `is`.hth.wakatimeclient.core.data.Results

/**
 * Wrap a suspending operation in try/catch. In case an exception is thrown,
 * a [Results.Failure] is created containing the error produced.
 * @param processor In case of an exception being thrown, processes it to standard form
 * @param operation A long running operation that might throw an exception
 */
inline fun <T : Any> safeOperation(
    processor: ErrorProcessor,
    operation: () -> Results<T>
): Results<T> = try {
    operation()
} catch (e: Exception) {
    Results.Failure(processor.onError(e))
}

/**
 * Unwraps a [Results] object and passes any contained value onwards
 * for transformation. A [Results.Failure] will be returned straight
 * back, and a [Results.Success.Empty] will cause an error to be returned
 * @param results
 * @param transform
 */
inline fun <T, R> unwrap(
    results: Results<R>,
    transform: (R) -> Results<T>
): Results<T> = when (results) {
    is Results.Success.Values -> transform.invoke(results.data)
    is Results.Success.Empty -> Results.Failure(
        Error.Unknown(
            -1,
            "Empty values received in unwrap operation"
        )
    )
    is Results.Failure -> results
}