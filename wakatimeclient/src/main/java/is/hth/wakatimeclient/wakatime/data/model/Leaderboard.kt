package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Leaderboard(
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
        val name: String,
        @SerialName("can_delete")
        val canDelete: Boolean,
        @SerialName("can_edit")
        val canEdit: Boolean,
        @SerialName("has_available_seat")
        val hasAvailableSeats: Boolean,
        @Transient
        val isPrivate: Boolean = true,
        @SerialName("members_count")
        val memberCount: Int,
        @SerialName("members_with_timezones_count")
        val membersWithTimezones: Int,
        @SerialName("time_range")
        val range: String,
        @SerialName("modified_at")
        val modifiedAt: String,
        @SerialName("created_at")
        val createdAt: String
)