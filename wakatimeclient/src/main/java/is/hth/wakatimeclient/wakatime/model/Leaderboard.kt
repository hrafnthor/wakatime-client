package `is`.hth.wakatimeclient.wakatime.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Leaderboard(
        val id: String,
        val name: String,
        @SerialName("can_delete")
        val canDelete: Boolean,
        @SerialName("can_edit")
        val canEdit: Boolean,
        @SerialName("has_available_seat")
        val hasAvailableSeats: Boolean,
        @SerialName("members_count")
        val memberCount: Int,
        @SerialName("members_with_timezones_count")
        val membersWithTimezone: Int,
        @SerialName("time_range")
        val range: String,
        @SerialName("modified_at")
        val modifiedAt: String,
        @SerialName("created_at")
        val createdAt: String
)