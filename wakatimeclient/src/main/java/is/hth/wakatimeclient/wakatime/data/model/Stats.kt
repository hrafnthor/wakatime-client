package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.findValue
import `is`.hth.wakatimeclient.wakatime.data.model.filters.MetaFilter
import `is`.hth.wakatimeclient.wakatime.data.model.filters.ProjectFilter
import `is`.hth.wakatimeclient.wakatime.data.model.filters.RequestDsl
import kotlinx.serialization.*
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
data class Day(
    /**
     * Unique id of this entry
     */
    val id: String = "",
    /**
     * The date for the day in ISO 8601 format
     */
    val date: String = "",
    /**
     * The human readable total recorded time for this day
     */
    @SerialName("text")
    val humanReadableTime: String = "",
    /**
     * The total recorded time for this day in seconds
     */
    @SerialName("total_seconds")
    val secondsTotal: Double = 0.0,
    /**
     * In ISO 8601 format
     */
    @SerialName("created_at")
    val createdAt: String = "",
    /**
     * In ISO 8601 format. Can be empty.
     */
    @SerialName("modified_at")
    val modifiedAt: String = ""
)

@Serializable
data class Measurement(
    /**
     * The full hour portion of this measurement
     */
    val hours: Int = 0,
    /**
     * The minutes portion of this measurement
     */
    val minutes: Int = 0,
    /**
     * The percentage that this measurement represents of the whole observed time
     * over the requested range
     */
    val percent: Double = 0.0,
    /**
     * The total amount of seconds in this measurement
     */
    @SerialName("total_seconds")
    val secondsTotal: Double = 0.0,
    /**
     * The total amount of time in this measurement in 24 hour format
     */
    @SerialName("digital")
    val total24Hour: String = "",
    /**
     * The descriptive name of this measurement, dependent on the context.
     * Could be a category, a project name or a ide name depending on the context
     */
    val name: String = "",
    /**
     * The total amount of time in this measurement in human readable 24 hour format
     */
    @SerialName("text")
    val humanReadableTotal24Hour: String = "",
)

@Serializable
data class Machine(
    /**
     * Unique id of this machine with Wakatime
     */
    val id: String = "",
    /**
     * The ip address of this machine
     */
    val ip: String = "",
    /**
     * The local name of the machine
     */
    val name: String = "",
    /**
     * Last seen date in ISO 8601 format
     */
    @SerialName("last_seen_at")
    val lastSeenAt: String = "",
    /**
     * First seen date in ISO 8601 format
     */
    @SerialName("created_at")
    val createdAt: String = "",
)

/**
 * A time measurement along with the associated [Machine] that the measurement was
 * observed coming from
 */
@Serializable
@Suppress("unused")
class MachineMeasurement(
    /**
     * The measurement done over the requested range
     */
    @SerialName(MEASUREMENT)
    val measurement: Measurement,
    /**
     * The machine associated with the measurement
     */
    @SerialName(MACHINE)
    val machine: Machine
) {
    internal companion object {
        const val MEASUREMENT = "measurement"
        const val MACHINE = "machine"
    }
}

/**
 * Modifies the incoming json stream to fit with the modified structure in the
 * [MachineMeasurement] object, for reuse of [Measurement] and [Machine]
 */
internal object MachineMeasurementListTransformer :
    JsonTransformingSerializer<List<MachineMeasurement>>(
        ListSerializer(MachineMeasurement.serializer())
    ) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element is JsonArray) {
            return buildJsonArray {
                element.map { innerElement ->
                    if (innerElement is JsonObject && innerElement.size == 8) {
                        // The inner element is of the correct type and contains as many
                        // keys as would be expected for the transformation to take place
                        buildJsonObject {

                            findValue(this, innerElement, MachineMeasurement.MACHINE) {}

                            put(MachineMeasurement.MEASUREMENT, buildJsonObject {
                                innerElement
                                    .filterKeys { it != MachineMeasurement.MACHINE }
                                    .forEach {
                                        put(it.key, it.value)
                                    }
                            })
                        }
                    } else innerElement
                }.forEach(this::add)
            }
        }
        throw IllegalArgumentException("Incorrect JsonElement type received for MachineMeasurement deserialization!")
    }
}

@Serializable
data class StatsData(
    val id: String = "",
    /**
     * The unique id of the owner of these stats
     */
    @SerialName("user_id")
    val userId: String = "",
    /**
     * The public username for the owner of these stats
     */
    val username: String = "",
    /**
     * The average coding activity per day as seconds for the given range of time
     */
    @SerialName("daily_average")
    val dailyAverage: Int = 0,
    /**
     * The average coding activity per day as seconds for the given range of time without any filtering
     */
    @SerialName("daily_average_including_other_language")
    val dailyAverageTotal: Int = 0,
    /**
     * Total number of days in this range
     */
    @SerialName("days_including_holidays")
    val daysWithHolidays: Int = 0,
    /**
     * Number of days in this range excluding days with no coding time logged
     */
    @SerialName("days_minus_holidays")
    val daysWithoutHolidays: Int = 0,
    /**
     * Number of days in this range with no coding time logged
     */
    val holidays: Int = 0,
    /**
     * Value of the user's timeout setting in minutes
     */
    val timeout: Int = 0,
    /**
     * Total coding activity as seconds for the given range of time.
     */
    @SerialName("total_seconds")
    val totalSeconds: Double = 0.0,
    /**
     * Total coding activity as seconds for the given range of time without any language filtering
     */
    @SerialName("total_seconds_including_other_language")
    val totalSecondsAllLanguages: Double = 0.0,
    /**
     * The daily average coding activity as a human readable string
     */
    @SerialName("human_readable_daily_average")
    val humanReadableDailyAverage: String = "",
    /**
     * The total coding activity for the given range of time as a human readable string
     */
    @SerialName("human_readable_total")
    val humanReadableTotal: String = "",
    /**
     * The time when these stats were created in ISO 8601 format
     */
    @SerialName("created_at")
    val createdAt: String = "",
    /**
     * The time when these stats were modified in ISO 8601 format
     */
    @SerialName("modified_at")
    val modifiedAt: String = "",
    /**
     *  The start of this time range as ISO 8601 UTC datetime
     */
    val start: String = "",
    /**
     * The end of this time range as ISO 8601 UTC datetime
     */
    val end: String = "",
    /**
     * The time range of these stats
     */
    val range: HumanRange = HumanRange.WEEK,
    /**
     * The timezone used in Olson Country/Region format
     */
    val timezone: String = "",
    /**
     * Indicates if this user's coding activity is publicly visible
     */
    @SerialName("is_coding_activity_visible")
    val codingActivityPubliclyVisible: Boolean = false,
    /**
     * Indicates if these stats include the current day; normally false except when [range] is [HumanRange.All]
     */
    @SerialName("is_including_today")
    val includesToday: Boolean = false,
    /**
     * Indicates if this user's languages, editors, and operating system stats are publicly visible
     */
    @SerialName("is_other_usage_visible")
    val otherActivityPubliclyVisible: Boolean = false,
    /**
     * Status of the user's writes_only setting
     */
    @SerialName("writes_only")
    val writesOnly: Boolean = false,
    /**
     * The singular day with the most activity within the requested range
     */
    @SerialName("best_day")
    val bestDay: Day = Day(),
    /**
     * List of measurements based on the machines that were observed
     */
    @Serializable(MachineMeasurementListTransformer::class)
    val machines: List<MachineMeasurement> = emptyList(),
    /**
     * List of measurements grouped by their categories
     */
    val categories: List<Measurement> = emptyList(),
    /**
     * List of measurements grouped by their dependencies
     */
    val dependencies: List<Measurement> = emptyList(),
    /**
     * List of measurements grouped by the editors observed being used
     */
    val editors: List<Measurement> = emptyList(),
    /**
     * List of measurements grouped by the languages they were observed using
     */
    val languages: List<Measurement> = emptyList(),
    /**
     * List of measurements grouped by the operating systems they were observed coming from
     */
    @SerialName("operating_systems")
    val operatingSystems: List<Measurement> = emptyList(),
    /**
     * List of measurements grouped by the projects they were observed for
     */
    val projects: List<Measurement> = emptyList(),
)


@Serializable
data class Status(
    /**
     * Indicates if these stats got stuck while processing and will be recalculated in the background
     */
    @SerialName(IS_STUCK)
    val isStuck: Boolean = false,
    /**
     * Indicates if these stats are being updated in the background
     */
    @SerialName(IS_ALREADY_UPDATING)
    val isAlreadyUpdating: Boolean = false,
    /**
     * The status of these stats in the cache
     */
    @SerialName(STATUS)
    val status: ProcessingStatus = ProcessingStatus.Done,
    /**
     * If the stats are being computed, this field will indicate the progress
     */
    @SerialName(PERCENTAGE_CALCULATED)
    val percentCalculated: Int = 100,
    /**
     * Indicates if these stats are up to date; when false, stats are missing or from an old
     * time range and will be refreshed soon
     */
    @SerialName(IS_UP_TO_DATE)
    val isUpToDate: Boolean = true,
) {
    internal companion object {
        internal const val IS_STUCK = "is_stuck"
        internal const val IS_ALREADY_UPDATING = "is_already_updating"
        internal const val STATUS = "status"
        internal const val PERCENTAGE_CALCULATED = "percent_calculated"
        internal const val IS_UP_TO_DATE = "is_up_to_date"
    }

    init {
        require(percentCalculated in 0..100)
    }
}

@Serializable(StatsTransformer::class)
data class Stats(
    /**
     * The data as requested. Depending on if the data was not available and required
     * processing, it might contain default values. Verify the stats [Status] before
     * using them.
     */
    @SerialName(DATA)
    val data: StatsData = StatsData(),
    /**
     * Defines the status of the data, and if any background processing is taking place.
     */
    @SerialName(STATUS)
    val status: Status = Status()
) {
    companion object {
        internal const val DATA = "data"
        internal const val STATUS = "status"

        /**
         * Make a request for the authenticated user's [StatsData] over the defined range
         */
        inline fun request(
            range: HumanRange,
            construct: Request.Builder.() -> Unit = {}
        ) = Request.Builder(range).also(construct).build()
    }

    class Request(
        val range: HumanRange,
        val project: ProjectFilter?,
        val meta: MetaFilter?,
    ) {
        @RequestDsl
        @SuppressWarnings("unused")
        class Builder(
            var range: HumanRange,
            var project: ProjectFilter? = null,
            var meta: MetaFilter? = null,
        ) {
            fun build() = Request(
                range = range,
                project = project,
                meta = meta,
            )
        }
    }
}

/**
 * Performs Json transformation on the incoming payload to extract values not relevant
 * to the [StatsData] into another object [Status]
 */
internal object StatsTransformer : JsonTransformingSerializer<Stats>(StatsSerializer) {

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return if (element is JsonObject && element.size != 2) {
            // Payload is of the correct type and does not seem to
            // already have the modified structure
            buildJsonObject {

                put(Stats.DATA, buildJsonObject {
                    element.filterKeys {
                        it != Status.IS_STUCK
                            && it != Status.IS_ALREADY_UPDATING
                            && it != Status.STATUS
                            && it != Status.PERCENTAGE_CALCULATED
                            && it != Status.IS_UP_TO_DATE
                    }.forEach { entry ->
                        put(entry.key, entry.value)
                    }
                })

                put(Stats.STATUS, buildJsonObject {
                    findValue(this, element, Status.IS_STUCK) {
                        put(it, false)
                    }
                    findValue(this, element, Status.IS_ALREADY_UPDATING) {
                        put(it, false)
                    }
                    findValue(this, element, Status.STATUS) {
                        put(it, ProcessingStatus.Done.name)
                    }
                    findValue(this, element, Status.PERCENTAGE_CALCULATED) {
                        put(it, 100)
                    }
                    findValue(this, element, Status.IS_UP_TO_DATE) {
                        put(it, true)
                    }
                })
            }
        } else super.transformDeserialize(element)
    }
}

internal object StatsSerializer : KSerializer<Stats> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("stats") {
            element<StatsData>(Stats.DATA)
            element<Status>(Stats.STATUS)
        }

    @ExperimentalSerializationApi
    override fun serialize(encoder: Encoder, value: Stats) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(Stats.DATA),
                serializer = StatsData.serializer(),
                value = value.data)
            encodeSerializableElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(Stats.STATUS),
                serializer = Status.serializer(),
                value = value.status
            )
        }
    }

    @ExperimentalSerializationApi
    override fun deserialize(decoder: Decoder): Stats {
        return decoder.decodeStructure(descriptor) {
            val data = decodeSerializableElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(Stats.DATA),
                deserializer = StatsData.serializer()
            )
            val status = decodeSerializableElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(Stats.STATUS),
                deserializer = Status.serializer()
            )

            Stats(
                data = data,
                status = status
            )
        }
    }
}

@Suppress("unused")
inline fun Stats.Request.Builder.project(
    filter: ProjectFilter.Builder.() -> Unit
) {
    val builder = ProjectFilter.Builder()
    builder.filter()
    project = builder.build()
}


@Suppress("unused")
inline fun Stats.Request.Builder.meta(
    filter: MetaFilter.Builder.() -> Unit
) {
    val builder = MetaFilter.Builder()
    builder.filter()
    meta = builder.build()
}