package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    /**
     * The unique id of this project in the system
     */
    val id: String,
    /**
     * The name of the project
     */
    val name: String = "",
    /**
     * The associated repository if any is connected
     */
    val repository: String = "",
    /**
     * The postfix url for the project specific dashboard
     */
    val url: String = "",
    /**
     * The time when the project was created in ISO 8601 format
     */
    @SerialName("created_at")
    val createdAt: String = "",
    /**
     * The last time when the project had a heartbeat update in ISO 8601 format
     */
    @SerialName("last_heartbeat_at")
    val lastHeartbeatAt: String = "",
    /**
     * Indicates whether this project's dashboard has had a public url created by Wakatime.
     */
    @SerialName("has_public_url")
    val hasPublicUrl: Boolean
)
