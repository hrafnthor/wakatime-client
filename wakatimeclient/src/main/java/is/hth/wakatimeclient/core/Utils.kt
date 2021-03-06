package `is`.hth.wakatimeclient.core

import `is`.hth.wakatimeclient.core.data.ErrorProcessor
import `is`.hth.wakatimeclient.core.data.Failure
import `is`.hth.wakatimeclient.core.data.Results

/**
 * Wrap a suspending operation in try/catch. In case an exception is thrown,
 * a [Failure] is created containing the error produced.
 * @param processor In case of an exception being thrown, processes it to standard form
 * @param operation A long running operation that might throw an exception
 */
internal inline fun <T : Any> safeOperation(
    processor: ErrorProcessor,
    operation: () -> Results<T>
): Results<T> = try {
    operation()
} catch (e: Exception) {
    Failure(processor.onError(e))
}