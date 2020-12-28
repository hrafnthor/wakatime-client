package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: String,
    val name: String = "",
    val repository: String = "",
    val url: String = "",
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("last_heartbeat_at")
    val lastHeartbeatAt: String = "",
    @SerialName("has_public_url")
    val hasPublicUrl: Boolean
)
