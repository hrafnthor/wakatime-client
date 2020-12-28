package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A user agent, plugin, used by the user
 */
@Serializable
data class Agent(
    /**
     * Unique id of this user agent
     */
    val id: String,
    /**
     * A user agent string
     */
    val value: String = "",
    /**
     * The editor / IDE name of this user agent
     */
    val editor: String = "",
    /**
     * The Wakatime plugin version of this user agent
     */
    val version: String = "",
    /**
     * The operating system of this user agent
     */
    val os: String = "",
    /**
     * The time when this user agent was last seen in ISO 8601 format
     */
    @SerialName("last_seen")
    val lastSeen: String = "",
    /**
     * The time when this user agent was first seen in ISO 8601 format
     */
    @SerialName("created_at")
    val createdAt: String = ""
)