package `is`.hth.wakatimeclient.wakatime.data

import kotlinx.serialization.json.*

/**
 * Convenience method for extracting a potential value from a JsonObject and
 * adding it to a new builder
 *
 * @param element from which fields are being parsed from
 * @param sourceKey for the field that is being looked for in the supplied element
 * @param destKey to use for storing the found value into the builder
 * @param default called when the element is not found, or the value is null, and whatever default step should be
 * performed. Is passed the previously supplied key
 */
internal inline fun JsonObjectBuilder.findValue(
    element: JsonObject,
    sourceKey: String,
    destKey: String,
    default: () -> JsonElement
) {
    val innerElement = element[sourceKey]?.let {
        when (it) {
            is JsonNull -> default()
            else -> it
        }
    } ?: default()
    put(destKey, innerElement)
}

/**
 * Convenience method for extracting and setting a value to the builder
 *
 * @param element to extract the value from
 * @param key to use for the extraction and placement in the builder
 * @param default value to use in case no value is found in the supplied element with the given key
 */
internal fun JsonObjectBuilder.findValue(
    element: JsonObject,
    key: String,
    default: Number
): Unit = findValue(
    element = element,
    sourceKey = key,
    destKey = key
) { JsonPrimitive(default) }

/**
 * Convenience method for extracting and setting a value to the builder
 *
 * @param element to extract the value from
 * @param key to use for the extraction and placement in the builder
 * @param default value to use in case no value is found in the supplied element with the given key
 */
internal fun JsonObjectBuilder.findValue(
    element: JsonObject,
    key: String,
    default: Boolean
): Unit = findValue(
    element = element,
    sourceKey = key,
    destKey = key
) { JsonPrimitive(default) }

/**
 * Convenience method for extracting and setting a value to the builder
 *
 * @param element to extract the value from
 * @param key to use for the extraction and placement in the builder
 * @param default value to use in case no value is found in the supplied element with the given key
 */
internal fun JsonObjectBuilder.findValue(
    element: JsonObject,
    key: String,
    default: String
): Unit = findValue(
    element = element,
    sourceKey = key,
    destKey = key
) { JsonPrimitive(default) }

/**
 * Convenience method for extracting and setting a value to the builder
 *
 * @param element to extract the value from
 * @param key to use for the extraction and placement in the builder
 * @param default value to use in case no value is found in the supplied element with the given key
 */
internal inline fun JsonObjectBuilder.findValue(
    element: JsonObject,
    key: String,
    default: () -> JsonElement
): Unit = findValue(
    element = element,
    sourceKey = key,
    destKey = key,
    default = default
)