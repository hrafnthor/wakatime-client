package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The total recorded time for the user since account creation
 */
@Serializable
public data class TotalRecord internal constructor(
    /**
     * Indicates if this record is up to date or is being updated server side
     */
    @SerialName("is_up_to_date")
    val isUpToDate: Boolean = true,
    /**
     * Indicates the percentage progress of the ongoing calculation going on
     * server side if such work was required.
     */
    @SerialName("percent_calculated")
    val percentCalculated: Int = 100,
    /**
     * The total number of recorded seconds since the account was created
     */
    @SerialName("total_seconds")
    val totalRecordedSeconds: Float = 0f,
    /**
     * The total recorded human readable time since the account was created
     */
    @SerialName("text")
    val totalRecordedTimeReadable: String = "",
    /**
     * The user configured timeout in seconds
     */
    val timeout: Int = 0,
    /**
     * The date range that this record applies over
     */
    val range: Range
)