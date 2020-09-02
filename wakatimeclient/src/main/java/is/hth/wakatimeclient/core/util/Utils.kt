package `is`.hth.wakatimeclient.core.util

import `is`.hth.wakatimeclient.core.data.ErrorProcessor
import `is`.hth.wakatimeclient.core.data.Results

/**
 * Wrap a suspending [operation] in try/catch. In case an exception is thrown,
 * a [Results.Failure] is created containing the [error] produced.
 */
suspend fun <T : Any> safeOperation(
    /**
     * In case of an exception being thrown, processes it to standard form
     */
    processor: ErrorProcessor,
    /**
     * A long running operation that might throw an exception
     */
    operation: suspend () -> Results<T>
): Results<T> = try {
    operation()
} catch (e: Exception) {
    Results.Failure(processor.onError(e))
}