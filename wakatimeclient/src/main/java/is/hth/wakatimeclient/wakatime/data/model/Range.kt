package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*

@Serializable(RangeJsonTransformer::class)
public data class Range internal constructor(
    /**
     * Start of time range as ISO 8601 UTC datetime
     */
    @SerialName(START)
    val start: String = "",
    /**
     * End of time range as ISO 8601 UTC datetime
     */
    @SerialName(END)
    val end: String = "",
    /**
     * Timezone used in Olson Country/Region format, if available
     */
    @SerialName(TIMEZONE)
    val timezone: String = ""
) {
    internal companion object {
        const val START = "start"
        const val START_DATE = "start_date"
        const val END = "end"
        const val END_DATE = "end_date"
        const val TIMEZONE = "timezone"
    }
}

/**
 * Transforms the common fields supplied in range objects to a standardized object
 * which can than be consumed universally where time ranges are needed.
 */
internal object RangeJsonTransformer : JsonTransformingSerializer<Range>(RangeSerializer) {

    // For adding to start_time fields to make them match start fields, which include time
    private const val START_DATE_TIME = "T00:00:00Z"

    // For adding to end_time fields to make them match end fields, which include time
    private const val END_DATE_TIME = "T23:59:59Z"

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return if (element is JsonObject) {
            buildJsonObject {
                val startDate: JsonElement = element[Range.START]
                    ?: element[Range.START_DATE]?.let {
                        JsonPrimitive("${it.jsonPrimitive.content}$START_DATE_TIME")
                    }
                    ?: JsonPrimitive("")

                val endDate: JsonElement = element[Range.END]
                    ?: element[Range.END_DATE]?.let {
                        JsonPrimitive("${it.jsonPrimitive.content}$END_DATE_TIME")
                    }
                    ?: JsonPrimitive("")

                put(Range.START, startDate)
                put(Range.END, endDate)
                put(Range.TIMEZONE, element[Range.TIMEZONE] ?: JsonPrimitive(""))
            }
        } else throw IllegalArgumentException(
            "Incorrect JsonElement received for Range deserialization!"
        )
    }
}


@OptIn(ExperimentalSerializationApi::class)
internal object RangeSerializer : KSerializer<Range> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("range") {
            element<String>(Range.START)
            element<String>(Range.END)
            element<String>(Range.TIMEZONE)
        }

    override fun deserialize(decoder: Decoder): Range {
        return decoder.decodeStructure(descriptor) {
            val startDate = decodeStringElement(
                descriptor,
                descriptor.getElementIndex(Range.START)
            )
            val endDate = decodeStringElement(
                descriptor,
                descriptor.getElementIndex(Range.END)
            )
            val timezone = decodeStringElement(
                descriptor,
                descriptor.getElementIndex(Range.TIMEZONE)
            )

            Range(
                start = startDate,
                end = endDate,
                timezone = timezone
            )
        }
    }

    override fun serialize(encoder: Encoder, value: Range) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(
                descriptor,
                descriptor.getElementIndex(Range.START),
                value.start
            )
            encodeStringElement(
                descriptor,
                descriptor.getElementIndex(Range.END),
                value.end
            )
            encodeStringElement(
                descriptor,
                descriptor.getElementIndex(Range.TIMEZONE),
                value.timezone
            )
        }
    }
}