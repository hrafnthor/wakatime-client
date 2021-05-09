package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.findValue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*
import java.util.*

@Serializable
data class Amount(
    /**
     * The number of seconds for this amount type
     */
    val seconds: Float = 0.0f,
    /**
     * The seconds field formatted as human readable text
     */
    @SerialName("text")
    val secondsHumanReadable: String = ""
)

@Serializable(AggregationJsonTransformer::class)
data class Aggregation(
    /**
     * The name of the entity that this aggregation is for
     */
    @SerialName(NAME)
    val name: String = "",
    /**
     * Indicates if the entity that this aggregation is for has been verified
     */
    @SerialName(VERIFIED)
    val verified: Boolean = false,
    /**
     * Contains the count of users for this aggregation formatted as human readable value
     */
    @SerialName(HUMAN_READABLE_COUNT)
    val humanReadableCount: String = "",
    /**
     * The average number of seconds by all Wakatime users for this aggregation
     */
    @SerialName(AVERAGE)
    val average: Amount,
    /**
     * The number of seconds by the Wakatime user who had the most activity for this aggregation
     */
    @SerialName(MAX)
    val max: Amount,
    /**
     * The median number of seconds for all Wakatime users for this aggregation
     */
    @SerialName(MEDIAN)
    val median: Amount,
    /**
     * The sum of all seconds for all Wakatime users for this aggregation
     */
    @SerialName(SUM)
    val sum: Amount
) {
    internal companion object {
        const val NAME = "name"
        const val VERIFIED = "is_verified"
        const val HUMAN_READABLE_COUNT = "human_readable_count"
        const val AVERAGE = "average"
        const val MAX = "max"
        const val MEDIAN = "median"
        const val SUM = "sum"
    }
}

/**
 * Performs flattening operation on incoming Aggregation payload, specifically changing
 * the 'count' field from an object to primitive
 */
internal object AggregationJsonTransformer : JsonTransformingSerializer<Aggregation>(
    AggregationSerializer
) {

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return when {
            element is JsonObject && !element.containsKey(Aggregation.HUMAN_READABLE_COUNT) -> {
                // Payload has not had its contents transformed yet
                buildJsonObject {
                    findValue(this, element, Aggregation.NAME) {
                        put(it, "")
                    }

                    findValue(this, element, Aggregation.VERIFIED) {
                        put(it, false)
                    }

                    element["count"]?.let { inner ->
                        if (inner is JsonObject) {
                            inner["text"]?.let { put(Aggregation.HUMAN_READABLE_COUNT, it) }
                        }
                    }

                    findValue(this, element, Aggregation.AVERAGE) {
                        put(it, JsonArray(emptyList()))
                    }

                    findValue(this, element, Aggregation.MAX) {
                        put(it, JsonArray(emptyList()))
                    }

                    findValue(this, element, Aggregation.MEDIAN) {
                        put(it, JsonArray(emptyList()))
                    }

                    findValue(this, element, Aggregation.SUM) {
                        put(it, JsonArray(emptyList()))
                    }
                }
            }
            element is JsonObject -> element
            else -> throw IllegalArgumentException(
                "Incorrect json element type received for Aggregation serialization!"
            )
        }
    }
}

internal object AggregationSerializer : KSerializer<Aggregation> {

    private val amountSerializer = Amount.serializer()

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("aggregation") {
            element<String>(Aggregation.NAME)
            element<Boolean>(Aggregation.VERIFIED)
            element<String>(Aggregation.HUMAN_READABLE_COUNT)
            element(Aggregation.AVERAGE, amountSerializer.descriptor)
            element(Aggregation.MAX, amountSerializer.descriptor)
            element(Aggregation.MEDIAN, amountSerializer.descriptor)
            element(Aggregation.SUM, amountSerializer.descriptor)
        }

    @ExperimentalSerializationApi
    override fun deserialize(decoder: Decoder): Aggregation {
        return decoder.decodeStructure(descriptor) {
            val name = decodeStringElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.NAME)
            )
            val verified = decodeBooleanElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.VERIFIED)
            )
            val count = decodeStringElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.HUMAN_READABLE_COUNT)
            )
            val average = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.AVERAGE),
                amountSerializer
            )
            val max = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.MAX),
                amountSerializer
            )
            val median = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.MEDIAN),
                amountSerializer
            )
            val sum = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.SUM),
                amountSerializer
            )
            Aggregation(
                name = name,
                verified = verified,
                humanReadableCount = count,
                average = average,
                max = max,
                median = median,
                sum = sum
            )
        }
    }

    @ExperimentalSerializationApi
    override fun serialize(encoder: Encoder, value: Aggregation) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.NAME),
                value.name
            )
            encodeBooleanElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.VERIFIED),
                value.verified
            )
            encodeStringElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.HUMAN_READABLE_COUNT),
                value.humanReadableCount
            )
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.AVERAGE),
                amountSerializer,
                value.average
            )
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.MAX),
                amountSerializer,
                value.max
            )
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.MEDIAN),
                amountSerializer,
                value.median
            )
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Aggregation.SUM),
                amountSerializer,
                value.sum
            )
        }
    }
}

@Serializable(GlobalStatsJsonTransformer::class)
data class GlobalStats(
    @SerialName(CATEGORIES)
    val categories: List<Aggregation>,
    @SerialName(AVERAGES)
    val dailyAverages: List<Aggregation>,
    @SerialName(EDITORS)
    val editors: List<Aggregation>,
    @SerialName(LANGUAGES)
    val languages: List<Aggregation>,
    @SerialName(OSYSTEMS)
    val operatingSystems: List<Aggregation>,
    @SerialName(TOTAL)
    val total: List<Aggregation>,
    @SerialName(RANGE)
    val range: Range,
    @SerialName(TIMEOUT)
    val timeout: Int,
    @SerialName(WRITES_ONLY)
    val writesOnly: Boolean
) {
    companion object {
        internal const val CATEGORIES = "categories"
        internal const val AVERAGES = "daily_averages"
        internal const val EDITORS = "editors"
        internal const val LANGUAGES = "languages"
        internal const val OSYSTEMS = "operating_systems"
        internal const val TOTAL = "total"
        internal const val RANGE = "range"
        internal const val TIMEOUT = "timeout"
        internal const val WRITES_ONLY = "writes_only"

        /**
         * Constructs a [GlobalStats] request for the last 7 day period
         */
        fun requestLastWeek(): Request = Request(HumanRange.WEEK.description)

        /**
         * Constructs a [GlobalStats] request for the whole of the supplied year
         */
        fun requestYear(year: Int): Request = Request(year.toString())
    }

    data class Request internal constructor(
        val range: String
    )
}

/**
 * Performs json transformation on unmodified payload from the api, moving the
 * values inside of the 'data' field up into the root
 */
internal object GlobalStatsJsonTransformer : JsonTransformingSerializer<GlobalStats>(
    GlobalStatsSerializer
) {

    private val empty = JsonArray(emptyList())

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return when {
            element is JsonObject && element.size == 4 -> buildJsonObject {
                element["data"]?.let { data ->
                    put(GlobalStats.CATEGORIES, getAggregation(data, GlobalStats.CATEGORIES))
                    put(GlobalStats.AVERAGES, getAggregation(data, GlobalStats.AVERAGES))
                    put(GlobalStats.EDITORS, getAggregation(data, GlobalStats.EDITORS))
                    put(GlobalStats.LANGUAGES, getAggregation(data, GlobalStats.LANGUAGES))
                    put(GlobalStats.OSYSTEMS, getAggregation(data, GlobalStats.OSYSTEMS))
                    put(GlobalStats.TOTAL, getAggregation(data, GlobalStats.TOTAL))
                } ?: run {
                    put(GlobalStats.CATEGORIES, empty)
                    put(GlobalStats.AVERAGES, empty)
                    put(GlobalStats.EDITORS, empty)
                    put(GlobalStats.LANGUAGES, empty)
                    put(GlobalStats.OSYSTEMS, empty)
                    put(GlobalStats.TOTAL, empty)
                }
                findValue(this, element, GlobalStats.RANGE) {}
                findValue(this, element, GlobalStats.TIMEOUT) {
                    put(it, 15)
                }
                findValue(this, element, GlobalStats.WRITES_ONLY) {
                    put(it, false)
                }
            }
            element is JsonObject -> element
            else -> throw IllegalArgumentException(
                "Incorrect json element type received for GlobalStats serialization!"
            )
        }
    }

    private fun getAggregation(element: JsonElement, key: String): JsonElement {
        return if (element is JsonObject) {
            element[key] ?: empty
        } else empty
    }
}

internal object GlobalStatsSerializer : KSerializer<GlobalStats> {

    private val aggregationListSerializer = ListSerializer(Aggregation.serializer())
    private val rangeSerializer = Range.serializer()

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("global_stats") {
            element(GlobalStats.CATEGORIES, aggregationListSerializer.descriptor)
            element(GlobalStats.AVERAGES, aggregationListSerializer.descriptor)
            element(GlobalStats.EDITORS, aggregationListSerializer.descriptor)
            element(GlobalStats.LANGUAGES, aggregationListSerializer.descriptor)
            element(GlobalStats.OSYSTEMS, aggregationListSerializer.descriptor)
            element(GlobalStats.TOTAL, aggregationListSerializer.descriptor)
            element(GlobalStats.RANGE, rangeSerializer.descriptor)
            element<Int>(GlobalStats.TIMEOUT)
            element<Boolean>(GlobalStats.WRITES_ONLY)
        }

    @ExperimentalSerializationApi
    override fun deserialize(decoder: Decoder): GlobalStats {
        return decoder.decodeStructure(descriptor) {
            val categories = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.CATEGORIES),
                aggregationListSerializer
            )
            val averages = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.AVERAGES),
                aggregationListSerializer
            )
            val editors = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.EDITORS),
                aggregationListSerializer
            )
            val languages = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.LANGUAGES),
                aggregationListSerializer
            )
            val os = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.OSYSTEMS),
                aggregationListSerializer
            )
            val total = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.TOTAL),
                aggregationListSerializer
            )
            val range = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.RANGE),
                rangeSerializer
            )
            val timeout = decodeIntElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.TIMEOUT)
            )
            val writesOnly = decodeBooleanElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.WRITES_ONLY)
            )
            GlobalStats(
                categories = categories,
                dailyAverages = averages,
                editors = editors,
                languages = languages,
                operatingSystems = os,
                total = total,
                range = range,
                timeout = timeout,
                writesOnly = writesOnly
            )
        }
    }

    override fun serialize(encoder: Encoder, value: GlobalStats) {
        TODO("Not yet implemented")
    }
}