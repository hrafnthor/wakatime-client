package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Predefined chronological range constants
 */
@Suppress("unused")
@Serializable
enum class HumanRange(val description: String) {

    /**
     * The last week
     */
    @SerialName("last_7_days")
    WEEK("last_7_days"),

    /**
     * The last 30 days from today
     */
    @SerialName("last_30_days")
    MONTH("last_30_days"),

    /**
     * The last 6 months since today
     */
    @SerialName("last_6_months")
    HALF_YEAR("last_6_months"),

    /**
     * The last year from today
     */
    @SerialName("last_year")
    YEAR("last_year")
}

@Serializable
data class Range(
    /**
     * Start of current time range as ISO 8601 UTC datetime
     */
    val start: String,
    /**
     * End of current time range as ISO 8601 UTC datetime>
     */
    val end: String,
    /**
     * Timezone used in Olson Country/Region format
     */
    val timezone: String
)