package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import `is`.hth.wakatimeclient.wakatime.data.findValue
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

@Serializable
public data class Amount internal constructor(
    /**
     * The number of seconds for this amount type
     */
    @SerialName(SECONDS)
    val seconds: Float = 0.0f,
    /**
     * The seconds field formatted as human readable text
     */
    @SerialName(SECONDS_HUMAN_READABLE)
    val secondsHumanReadable: String = ""
) {
    internal companion object {
        const val SECONDS = "seconds"
        const val SECONDS_HUMAN_READABLE = "text"
    }
}

@Serializable(AggregationJsonTransformer::class)
public data class Aggregation internal constructor(
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
    private val defaultAmount: JsonObject = buildJsonObject {
        put(Amount.SECONDS, 0.0f)
        put(Amount.SECONDS_HUMAN_READABLE, "")
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return when {
            element is JsonObject && !element.containsKey(Aggregation.HUMAN_READABLE_COUNT) -> {
                // Payload has not had its contents transformed yet
                buildJsonObject {
                    findValue(element, Aggregation.NAME, "")
                    findValue(element, Aggregation.VERIFIED, false)

                    element["count"]?.let { innerElement ->
                        if (innerElement is JsonObject) {
                            // Flatten count object into a simple field in the root
                            findValue(
                                builder = this,
                                element = innerElement,
                                sourceKey = "text",
                                destKey = Aggregation.HUMAN_READABLE_COUNT
                            ) { JsonPrimitive("") }
                        }
                    }

                    findValue(element, Aggregation.AVERAGE) { defaultAmount }
                    findValue(element, Aggregation.MAX) { defaultAmount }
                    findValue(element, Aggregation.MEDIAN) { defaultAmount }
                    findValue(element, Aggregation.SUM) { defaultAmount }
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

@Suppress("unused")
@Serializable(GlobalStatsJsonTransformer::class)
public data class GlobalStats internal constructor(
    /**
     * Contains total value aggregation for all users
     */
    @SerialName(TOTAL)
    val total: Aggregation,
    /**
     * Contains global aggregated average stats for all users
     */
    @SerialName(AVERAGES)
    val dailyAverages: Aggregation,
    /**
     * Contains global stats aggregation by categories. See [Category] for options
     */
    @SerialName(CATEGORIES)
    val categories: List<Aggregation>,
    /**
     * Contains global stats aggregation by editors
     */
    @SerialName(EDITORS)
    val editors: List<Aggregation>,
    /**
     * Contains global stats aggregation by languages
     */
    @SerialName(LANGUAGES)
    val languages: List<Aggregation>,
    /**
     * Contains global stats aggregation by operating systems
     */
    @SerialName(OPERATING_SYSTEMS)
    val operatingSystems: List<Aggregation>,
    /**
     * The range for these stats
     */
    @SerialName(RANGE)
    val range: Range,
    /**
     * The default timeout preference for public stats
     */
    @SerialName(TIMEOUT)
    val timeout: Int,
    /**
     * The default 'writes only' preference for public stats
     */
    @SerialName(WRITES_ONLY)
    val writesOnly: Boolean
) {
    public companion object {
        internal const val CATEGORIES = "categories"
        internal const val AVERAGES = "daily_average"
        internal const val EDITORS = "editors"
        internal const val LANGUAGES = "languages"
        internal const val OPERATING_SYSTEMS = "operating_systems"
        internal const val TOTAL = "total"
        internal const val RANGE = "range"
        internal const val TIMEOUT = "timeout"
        internal const val WRITES_ONLY = "writes_only"

        /**
         * Constructs a [GlobalStats] request for the last 7 day period
         */
        public fun requestLastWeek(): Request = Request(HumanRange.WEEK.toString())

        /**
         * Constructs a [GlobalStats] request for the whole of the supplied year
         */
        public fun requestYear(year: Int): Request = Request(year.toString())
    }

    /**
     * Request payload for [GlobalStats] fetching.
     *
     * Use either [GlobalStats.requestLastWeek] or [GlobalStats.requestYear] to construct
     */
    public data class Request internal constructor(
        /**
         * The range for which to request [GlobalStats] for
         */
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

    private val json = WakatimeJsonFactory.makeJson()
    private val empty = JsonArray(emptyList())
    private val defaultRange: JsonElement = json.encodeToJsonElement(Range())

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return when {
            element is JsonObject && element.size == 4 -> buildJsonObject {
                element["data"]?.let { data ->
                    put(GlobalStats.TOTAL, getAggregation(data, GlobalStats.TOTAL))
                    put(GlobalStats.AVERAGES, getAggregation(data, GlobalStats.AVERAGES))
                    put(GlobalStats.CATEGORIES, getAggregation(data, GlobalStats.CATEGORIES))
                    put(GlobalStats.EDITORS, getAggregation(data, GlobalStats.EDITORS))
                    put(GlobalStats.LANGUAGES, getAggregation(data, GlobalStats.LANGUAGES))
                    put(
                        GlobalStats.OPERATING_SYSTEMS,
                        getAggregation(data, GlobalStats.OPERATING_SYSTEMS)
                    )
                }

                findValue(element, GlobalStats.RANGE) { defaultRange }
                findValue(element, GlobalStats.TIMEOUT, 15)
                findValue(element, GlobalStats.WRITES_ONLY, false)
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

    private val aggregationSerializer = Aggregation.serializer()
    private val aggregationListSerializer = ListSerializer(aggregationSerializer)
    private val rangeSerializer = Range.serializer()

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("global_stats") {
            element(GlobalStats.TOTAL, aggregationSerializer.descriptor)
            element(GlobalStats.AVERAGES, aggregationSerializer.descriptor)
            element(GlobalStats.CATEGORIES, aggregationListSerializer.descriptor)
            element(GlobalStats.EDITORS, aggregationListSerializer.descriptor)
            element(GlobalStats.LANGUAGES, aggregationListSerializer.descriptor)
            element(GlobalStats.OPERATING_SYSTEMS, aggregationListSerializer.descriptor)
            element(GlobalStats.RANGE, rangeSerializer.descriptor)
            element<Int>(GlobalStats.TIMEOUT)
            element<Boolean>(GlobalStats.WRITES_ONLY)
        }

    @ExperimentalSerializationApi
    override fun deserialize(decoder: Decoder): GlobalStats {
        return decoder.decodeStructure(descriptor) {
            val total = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.TOTAL),
                aggregationSerializer
            )
            val averages = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.AVERAGES),
                aggregationSerializer
            )
            val categories = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.CATEGORIES),
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
                descriptor.getElementIndex(GlobalStats.OPERATING_SYSTEMS),
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

    @ExperimentalSerializationApi
    override fun serialize(encoder: Encoder, value: GlobalStats) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.TOTAL),
                AggregationSerializer,
                value.total
            )
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.AVERAGES),
                AggregationSerializer,
                value.dailyAverages
            )
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.CATEGORIES),
                aggregationListSerializer,
                value.categories
            )
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.EDITORS),
                aggregationListSerializer,
                value.editors
            )
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.LANGUAGES),
                aggregationListSerializer,
                value.languages
            )
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.OPERATING_SYSTEMS),
                aggregationListSerializer,
                value.operatingSystems
            )
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.RANGE),
                rangeSerializer,
                value.range
            )
            encodeIntElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.TIMEOUT),
                value.timeout
            )
            encodeBooleanElement(
                descriptor,
                descriptor.getElementIndex(GlobalStats.WRITES_ONLY),
                value.writesOnly
            )
        }
    }
}