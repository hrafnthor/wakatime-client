package `is`.hth.wakatimeclient.core

import `is`.hth.wakatimeclient.core.data.ErrorProcessor
import `is`.hth.wakatimeclient.core.data.Results
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder

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
 * Convenience method for extracting a potential value from a JsonObject and
 * adding it to a new builder
 * @param builder that is being used to construct a new JsonObject
 * @param element from which fields are being parsed from
 * @param key for the element that is being looked for
 * @param default called when the element is not found and whatever default step should be
 * performed. Is passed the previously supplied key
 */
internal inline fun findValue(
    builder: JsonObjectBuilder,
    element: JsonObject,
    key: String,
    default: (String) -> Unit
) {
    element[key]?.let {
        builder.put(key, it)
    } ?: run {
        default(key)
    }
}