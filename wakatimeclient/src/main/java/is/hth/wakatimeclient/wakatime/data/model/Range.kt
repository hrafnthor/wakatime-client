package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Range(
    /**
     * Start of time range as ISO 8601 UTC datetime
     */
    val start: String = "",
    /**
     * Start date of the time range, if available
     */
    @Transient
    @SerialName("start_date")
    val startDate: String = "",
    /**
     * Start date of the time range as a human readable string, if available
     */
    @Transient
    @SerialName("start_text")
    val startDateHumanReadable: String = "",
    /**
     * End of time range as ISO 8601 UTC datetime>
     */
    val end: String = "",
    /**
     * End date of the time range, if available
     */
    @Transient
    @SerialName("end_date")
    val endDate: String = "",
    /**
     * End date of the time range as human readable string, if available
     */
    @Transient
    @SerialName("end_text")
    val endDateHumanReadable: String = "",
    /**
     * Timezone used in Olson Country/Region format
     */
    val timezone: String = ""
)