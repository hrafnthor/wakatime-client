package `is`.hth.wakatimeclient.wakatime.data.db.entities

import androidx.room.*

/**
 * Contains records of the rank a users have for
 * specific languages on certain leaderboards
 */
@Entity(
    tableName = "user_rank",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["leaderboard_id"]),
        Index(value = ["language_id"]),
        Index(value = ["user_id", "leaderboard_id", "language_id", "period_id"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = LeaderboardEntity::class,
            parentColumns = ["id"],
            childColumns = ["leaderboard_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LanguageEntity::class,
            parentColumns = ["id"],
            childColumns = ["language_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PeriodEntity::class,
            parentColumns = ["id"],
            childColumns = ["period_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserRankEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "leaderboard_id")
    val leaderboardId: String,
    @ColumnInfo(name = "language_id")
    val languageId: Long,
    @ColumnInfo(name ="period_id")
    val periodId: Long,
    @ColumnInfo(name = "rank")
    val rank: Int,
    @ColumnInfo(name = "modified_at")
    val modifiedAt: String
)