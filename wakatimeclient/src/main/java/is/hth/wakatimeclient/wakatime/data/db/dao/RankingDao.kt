package `is`.hth.wakatimeclient.wakatime.data.db.dao

import `is`.hth.wakatimeclient.wakatime.data.db.entities.LeaderboardEntity
import `is`.hth.wakatimeclient.wakatime.data.db.entities.UserRankEntity
import androidx.room.*

@Dao
internal interface RankingDao {

    @Query(
        """
        SELECT * 
        FROM user_rank 
        WHERE user_id ==:userId
            AND language_id==:languageId 
            AND leaderboard_id==:leaderboardId
    """
    )
    fun getUserRankHistory(
        userId: String,
        languageId: Long,
        leaderboardId: String
    ): List<UserRankEntity>

    @Query(
        """
        SELECT * 
        FROM user_rank u1
        WHERE user_id ==:userId 
            AND u1.language_id==:languageId 
            AND u1.leaderboard_id==:leaderboardId 
            AND u1.period_id==(
                SELECT MAX(period_id) 
                FROM user_rank u2 
                where u2.user_id ==:userId 
                    AND u2.language_id==:languageId 
                    AND u2.leaderboard_id==:leaderboardId )
    """
    )
    fun getUserRankLatest(
        userId: String,
        languageId: Long,
        leaderboardId: String
    ): UserRankEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(leaderboardEntity: LeaderboardEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore(leaderboardEntity: LeaderboardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(entity: UserRankEntity): Long

    @Transaction
    fun setUserRank(rank: UserRankEntity): Long {
        val id: Long = insertReplace(rank)
        return if (id != -1L) id else getUserRankLatest(
            rank.userId,
            rank.languageId,
            rank.leaderboardId,
        )?.id ?: -1L
    }
}