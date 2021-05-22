package `is`.hth.wakatimeclient.wakatime.data

import kotlinx.serialization.json.*

/**
 * Convenience method for extracting a potential value from a JsonObject and
 * adding it to a new builder
 *
 * @param builder that is being used to construct a new JsonObject
 * @param element from which fields are being parsed from
 * @param key for the element that is being looked for
 * @param default called when the element is not found, or the value is null, and whatever default step should be
 * performed. Is passed the previously supplied key
 */
internal inline fun findValue(
    builder: JsonObjectBuilder,
    element: JsonObject,
    key: String,
    default: (String) -> Unit
) {
    val innerElement = element[key]
    if (innerElement != null && innerElement != JsonNull) {
        builder.put(key, innerElement)
    } else default(key)
}

internal fun findValue(
    builder: JsonObjectBuilder,
    element: JsonObject,
    key: String,
    default: Boolean
) {
    findValue(builder, element, key) {
        builder.put(key, default)
    }
}

internal fun findValue(
    builder: JsonObjectBuilder,
    element: JsonObject,
    key: String,
    default: String
) {
    findValue(builder, element, key) {
        builder.put(key, default)
    }
}