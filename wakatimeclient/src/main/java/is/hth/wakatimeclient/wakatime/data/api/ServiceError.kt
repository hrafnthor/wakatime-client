package `is`.hth.wakatimeclient.wakatime.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*

/**
 * The Wakatime service error payload
 */
@Serializable
data class ServiceError(
    @SerialName("error")
    val message: String = "",
    @SerialName("errors")
    @Serializable(FieldErrorTransformer::class)
    val fieldErrors: List<FieldError> = emptyList()
)

@Serializable
data class FieldError(
    val name: String,
    val description: String
)

/**
 * Performs modifications on the incoming 'errors' object, breaking it apart into
 * separate [FieldError]s
 */
internal object FieldErrorTransformer : JsonTransformingSerializer<List<FieldError>>(
    ListSerializer(FieldError.serializer())
) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return when (element) {
            is JsonObject -> JsonArray(element.mapTo(mutableListOf()) {
                val description = if (it.value is JsonArray) {
                    it.value.jsonArray.firstOrNull()?.jsonPrimitive?.content ?: ""
                } else ""
                buildJsonObject {
                    put("name", it.key)
                    put("description", description)
                }
            })
            else -> super.transformDeserialize(element)
        }
    }
}