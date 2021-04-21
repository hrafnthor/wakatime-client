package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.wakatime.data.model.filters.MetaFilter
import `is`.hth.wakatimeclient.wakatime.data.model.filters.ProjectFilter
import `is`.hth.wakatimeclient.wakatime.data.model.filters.RequestDsl
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
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
    val secondsTotal: Double,
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
    val hours: Int,
    /**
     * The minutes portion of this measurement
     */
    val minutes: Int,
    /**
     * The percentage that this measurement represents of the whole observed time
     * over the requested range
     */
    val percent: Double,
    /**
     * The total amount of seconds in this measurement
     */
    @SerialName("total_seconds")
    val secondsTotal: Double,
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
 * [MachineMeasurement] object
 */
internal object MMListSerializer : JsonTransformingSerializer<List<MachineMeasurement>>(
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

                            innerElement[MachineMeasurement.MACHINE]?.let { value ->
                                put(MachineMeasurement.MACHINE, value)
                            }

                            put(MachineMeasurement.MEASUREMENT, buildJsonObject {
                                innerElement
                                    .filterKeys { it != MachineMeasurement.MACHINE }
                                    .forEach(this::put)
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
data class Stats(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    val username: String = "",
    @SerialName("daily_average")
    val dailyAverage: Int,
    @SerialName("daily_average_including_other_language")
    val dailyAverageTotal: Int,
    @SerialName("days_including_holidays")
    val daysWithHolidays: Int,
    @SerialName("days_minus_holidays")
    val daysWithoutHolidays: Int,
    val holidays: Int,
    @SerialName("percent_calculated")
    val percentCalculated: Int,
    val timeout: Int,
    @SerialName("total_seconds")
    val totalSeconds: Double,
    @SerialName("total_seconds_including_other_language")
    val totalSecondsIncludingOtherLanguage: Double,
    @SerialName("human_readable_daily_average")
    val humanReadableDailyAverage: String = "",
    @SerialName("human_readable_total")
    val humanReadableTotal: String = "",
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("modified_at")
    val modifiedAt: String = "",
    val start: String = "",
    val status: String = "",
    val end: String = "",
    val range: String = "",
    val timezone: String = "",
    @SerialName("is_already_updating")
    val isAlreadyUpdating: Boolean,
    @SerialName("is_coding_activity_visible")
    val isCodingActivityVisible: Boolean,
    @SerialName("is_including_today")
    val isIncludingToday: Boolean,
    @SerialName("is_other_usage_visible")
    val isOtherUsageVisible: Boolean,
    @SerialName("is_stuck")
    val isStuck: Boolean,
    @SerialName("is_up_to_date")
    val isUpToDate: Boolean,
    @SerialName("writes_only")
    val writesOnly: Boolean,
    @SerialName("best_day")
    val bestDay: Day,
    @Serializable(MMListSerializer::class)
    val machines: List<MachineMeasurement>,
    val categories: List<Measurement>,
    val dependencies: List<Measurement>,
    val editors: List<Measurement>,
    val languages: List<Measurement>,
    @SerialName("operating_systems")
    val operatingSystems: List<Measurement>,
    val projects: List<Measurement>,
) {

    @Suppress("unused")
    companion object {

        /**
         * Make a request for the authenticated user's [Stats] over the defined range
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