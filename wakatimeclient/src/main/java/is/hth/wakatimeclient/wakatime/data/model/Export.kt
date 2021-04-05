package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Export(
    /**
     * The unique id of this data export
     */
    val id: String = "",
    /**
     * Indicates the current state of the export process
     */
    val status: ProcessingStatus,
    /**
     * Indicates the percentage of coding stats that have been exported
     */
    @SerialName("percentage_complete")
    val percentage: Float = 0f,
    /**
     * When the export has reached [ProcessingStatus.COMPLETED] there will be a url for
     * downloading the activity.
     */
    @SerialName("download_url")
    val downloadUrl: String = "",
    /**
     * The time when this export was created, in ISO 8601 format
     */
    @SerialName("created_at")
    val createdAt: String = "",
    /**
     * The time when this export will be unavailable for download, in ISO 8601 format
     */
    @SerialName("expires_at")
    val expiresAt: String = "",
)