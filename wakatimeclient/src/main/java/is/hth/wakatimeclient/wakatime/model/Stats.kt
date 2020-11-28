package `is`.hth.wakatimeclient.wakatime.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Day(
    val id: String = "",
    val date: String = "",
    val text: String = "",
    @SerialName("total_seconds")
    val secondsTotal: Double,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("modified_at")
    val modifiedAt: String = ""
)

@Serializable
data class Measurement(
    val hours: Int,
    val minutes: Int,
    val percent: Double,
    @SerialName("total_seconds")
    val secondsTotal: Double,
    val digital: String = "",
    val name: String = "",
    val text: String = "",
)

@Serializable
data class MachineMeasurement(
    val hours: Int,
    val minutes: Int,
    val percent: Double,
    @SerialName("total_seconds")
    val secondsTotal: Double,
    val digital: String = "",
    val name: String = "",
    val text: String = "",
    val machine: Machine
)

@Serializable
data class Machine(
    val id: String = "",
    val ip: String = "",
    val name: String = "",
    val value: String = "",
    @SerialName("last_seen_at")
    val lastSeenAt: String = "",
    @SerialName("created_at")
    val createdAt: String = "",
)

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
    val machines: List<MachineMeasurement>,
    val categories: List<Measurement>,
    val dependencies: List<Measurement>,
    val editors: List<Measurement>,
    val languages: List<Measurement>,
    @SerialName("operating_systems")
    val operatingSystems: List<Measurement>,
    val projects: List<Measurement>,
) {

    companion object {

        /**
         * Returns a [Request.Builder] for network request construction
         */
        fun makeRequest(range: HumanRange): Request.Builder = Request.Builder(range)
    }

    class Request private constructor(builder: Builder) {

        /**
         * The range to filter the stats by
         */
        val range: HumanRange = builder.range

        /**
         * The timeout value used to calculate these stats. Defaults the the user's timeout value.
         */
        val timeout: Int? = builder.timeout

        /**
         * The writes_only value used to calculate these stats. Defaults to the user's writes_only setting.
         */
        val writesOnly: Boolean? = builder.writesOnly

        /**
         * Filters the returned stats to the relevant project
         */
        val projectId: String? = builder.projectId

        class Builder internal constructor(range: HumanRange){

            var range: HumanRange = range
                private set
            var timeout: Int? = null
                private set
            var writesOnly: Boolean? = null
                private set
            var projectId: String? = null
                private set

            fun setRange(range: HumanRange): Builder = apply { this.range = range }

            fun setTimeout(timeout: Int?): Builder = apply { this.timeout = timeout }

            fun writesOnly(writesOnly: Boolean?): Builder = apply { this.writesOnly = writesOnly }

            fun filterByProject(projectId: String?): Builder = apply { this.projectId = projectId }
        }
    }
}