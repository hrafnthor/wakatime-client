package `is`.hth.wakatimeclient.wakatime.model

import kotlinx.serialization.Serializable

/**
 * Predefined chronological range constants
 */
sealed class HumanRange(val description: String) {

    /**
     * The last week
     */
    object Week : HumanRange("last_7_days")

    /**
     * The last 30 days from today
     */
    object Month : HumanRange("last_30_days")

    /**
     * The last 6 months since today
     */
    object HalfYear : HumanRange("last_6_months")

    /**
     * The last year from today
     */
    object Year : HumanRange("last_year")
}

@Serializable
data class Range(
    /**
     * The current time range as Date string in YEAR-MONTH-DAY
     * format (only available when delta is a "day")
     */
    val date: String,
    /**
     * Start of current time range as ISO 8601 UTC datetime
     */
    val start: String,
    /**
     * End of current time range as ISO 8601 UTC datetime>
     */
    val end: String,
    /**
     *  Current range in human-readable format relative to the current day
     */
    val text: String,
    /**
     * Timezone used in Olson Country/Region format
     */
    val timezone: String
)