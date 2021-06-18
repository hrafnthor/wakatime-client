package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class Leaderboard internal constructor(
    /**
     * The local database id for this leaderboard
     */
    @Transient
    val id: Long = -1L,
    /**
     * The server side unique identifier for this leaderboard
     */
    @SerialName("id")
    val identifier: String,
    /**
     * Name of this leaderboard
     */
    val name: String,
    /**
     * Indicates whether the user has access to delete this leaderboard
     */
    @SerialName("can_delete")
    val canDelete: Boolean,
    /**
     * Indicates whether the user has access to edit this leaderboard
     */
    @SerialName("can_edit")
    val canEdit: Boolean,
    /**
     * Indicates whether this leaderboard has room for more members
     */
    @SerialName("has_available_seat")
    val hasAvailableSeats: Boolean,
    /**
     * Indicates if this leaderboard is private
     */
    @Transient
    val isPrivate: Boolean = true,
    /**
     * Number of members in this leaderboard
     */
    @SerialName("members_count")
    val memberCount: Int,
    /**
     * The number of members who have timezones set. When a user does
     * not have a timezone, they will be hidden from leaderboards
     */
    @SerialName("members_with_timezones_count")
    val membersWithTimezones: Int,
    /**
     * The time range of this leaderboard, always [HumanRange.WEEK]
     */
    @SerialName("time_range")
    val range: HumanRange,
    /**
     * The time when the leaderboard was modified in ISO 8601 format
     */
    @SerialName("modified_at")
    val modifiedAt: String,
    /**
     * The time when the leaderboard was created in ISO 8601 format
     */
    @SerialName("created_at")
    val createdAt: String
)