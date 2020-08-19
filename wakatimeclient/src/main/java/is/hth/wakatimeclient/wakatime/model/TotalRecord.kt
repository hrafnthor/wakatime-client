package `is`.hth.wakatimeclient.wakatime.model

/**
 * The total recorded time for the user since account creation
 */
data class TotalRecord(
    /**
     * Indicates if this record is up to date or is being updated server side
     */
    val isUpToDate: Boolean = true,
    /**
     * Indicates the percentage progress of the ongoing calculation going on
     * server side if such work was required.
     */
    val percentCalculated: Int = 100,
    /**
     * The total number of recorded seconds since the account was created
     */
    val totalRecordedSeconds: Float = 0f,
    /**
     * The total recorded human readable time since the account was created
     */
    val totalRecordedTimeReadable: String = ""
)