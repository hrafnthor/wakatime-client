package `is`.hth.wakatimeclient.wakatime.data.db.entities

import `is`.hth.wakatimeclient.wakatime.model.Leaderboard
import androidx.room.*

@Entity(tableName = "leaderboards")
data class LeaderboardEntity(
        @PrimaryKey
        @ColumnInfo(name = "id")
        val id: String,
        @ColumnInfo(name = "member_count")
        val memberCount: Int,
        @ColumnInfo(name = "can_edit")
        val canEdit: Boolean,
        @ColumnInfo(name = "can_delete")
        val canDelete: Boolean,
        @ColumnInfo(name = "has_available_seats")
        val hasAvailableSeats: Boolean,
        @ColumnInfo(name = "is_private")
        val isPrivate: Boolean,
        @ColumnInfo(name = "members_with_timezones")
        val membersWithTimeZones: Int,
        @ColumnInfo(name = "name")
        val name: String,
        @ColumnInfo(name = "range")
        val range: String,
        @ColumnInfo(name = "created_at")
        val createdAt: String,
        @ColumnInfo(name = "modified_at")
        val modifiedAt: String
) {
    companion object {

        /**
         * A unique leaderboard presenting the public leaderboard
         */
        val publicLeaderboard: LeaderboardEntity = LeaderboardEntity(
                id = "##public_wakatime_leaderboards##",
                memberCount = -1,
                canEdit = false,
                canDelete = false,
                hasAvailableSeats = false,
                isPrivate = false,
                membersWithTimeZones = -1,
                name = "Public Wakatime leaderboards",
                range = "",
                createdAt = "",
                modifiedAt = ""
        )
    }
}

/**
 * Converts the model to a database entity
 */
internal fun Leaderboard.toEntity(isPrivate: Boolean): LeaderboardEntity = LeaderboardEntity(
        id = id,
        memberCount = memberCount,
        canEdit = canEdit,
        canDelete = canDelete,
        hasAvailableSeats = hasAvailableSeats,
        isPrivate = isPrivate,
        membersWithTimeZones = membersWithTimezone,
        name = name,
        range = range,
        createdAt = createdAt,
        modifiedAt = modifiedAt
)