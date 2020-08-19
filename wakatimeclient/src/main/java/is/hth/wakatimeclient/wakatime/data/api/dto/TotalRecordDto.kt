package `is`.hth.wakatimeclient.wakatime.data.api.dto

import `is`.hth.wakatimeclient.wakatime.model.TotalRecord
import com.google.gson.annotations.SerializedName

data class TotalRecordDto(
    /**
     * Indicates if this record is up to date or is being updated server side
     */
    @SerializedName("is_up_to_date")
    val isUpToDate: Boolean = true,
    /**
     * Indicates the percentage progress of the ongoing calculation going on
     * server side if such work was required.
     */
    @SerializedName("percent_calculated")
    val percentCalculated: Int = 100,
    /**
     * The total number of recorded seconds since the account was created
     */
    @SerializedName("total_seconds")
    val totalRecordedSeconds: Float = 0f,
    /**
     * The total recorded human readable time since the account was created
     */
    @SerializedName("text")
    val totalRecordedTimeReadable: String = ""
)

/**
 * Converts the dto to the public model
 */
fun TotalRecordDto.toTotalRecord(): TotalRecord = TotalRecord(
    isUpToDate = isUpToDate,
    percentCalculated = percentCalculated,
    totalRecordedSeconds = totalRecordedSeconds,
    totalRecordedTimeReadable = totalRecordedTimeReadable
)