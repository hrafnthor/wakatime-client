package `is`.hth.wakatimeclient.wakatime.data.db.entities

import androidx.room.*

@Entity(tableName = "leaderboards")
data class LeaderboardEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "member_count")
    val memberCount: Int,
    @ColumnInfo(name = "can_modify")
    val canModify: Boolean,
    @ColumnInfo(name = "can_delete")
    val canDelete: Boolean,
    @ColumnInfo(name = "is_full")
    val isFull: Boolean,
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
            canModify = false,
            canDelete = false,
            isFull = false,
            isPrivate = false,
            membersWithTimeZones = -1,
            name = "Public Wakatime leaderboards",
            range = "",
            createdAt = "",
            modifiedAt = ""
        )
    }
}