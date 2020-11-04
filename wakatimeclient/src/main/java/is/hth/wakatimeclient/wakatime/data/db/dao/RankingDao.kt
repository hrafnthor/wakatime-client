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
        userId: Long,
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
        leaderboardId: Long
    ): UserRankEntity?

    /**
     * Retrieves all leaderboards marked as being private
     */
    @Query("SELECT * FROM leaderboards WHERE is_private==1")
    fun getPrivateLeaderboards(): List<LeaderboardEntity>

    /**
     * Retrieves all leaderboards
     */
    @Query("SELECT * FROM leaderboards")
    fun getLeaderboards(): List<LeaderboardEntity>

    /**
     * Attempts to retrieve the [LeaderboardEntity] matching the supplied [identifier]
     */
    @Query("SELECT * FROM leaderboards WHERE identifier==:identifier")
    fun getLeaderboard(identifier: String): LeaderboardEntity?

    /**
     * Attempts to retrieve the id of the [LeaderboardEntity] matching the
     * supplied [identifier]. If none is found returns -1
     */
    @Query("SELECT id FROM leaderboards WHERE identifier==:identifier")
    fun getLeaderboardId(identifier: String): Long

    /**
     * Inserts and replaces the supplied [LeaderboardEntity], returning the rowid
     * of the newly inserted row
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(leaderboardEntity: LeaderboardEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnoreLeaderboard(entity: LeaderboardEntity): Long

    @Update
    fun updateLeaderboard(leaderboard: LeaderboardEntity): Int

    /**
     * Attempts to insert the supplied values if they do not already exist,
     * in which case they will be updated. Returns the number of rows affected
     */
    @Transaction
    fun insertOrUpdateLeaderboards(leaderboards: List<LeaderboardEntity>): Int {
        return leaderboards.fold(0) { count, leaderboard ->
            if (insertOrIgnoreLeaderboard(leaderboard) > 0) {
                count + 1
            } else {
                count + updateLeaderboard(leaderboard)
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnoreRank(entity: UserRankEntity): Long

    @Update
    fun updateRank(rank: UserRankEntity): Int

    /**
     * Attempts to insert the supplied values if they do not already exist,
     * in which case they will be updated. Returns the number of rows affected.
     */
    @Transaction
    fun insertOrUpdateRanks(ranks: List<UserRankEntity>): Int {
        return ranks.fold(0) { count, rank ->
            if (insertOrIgnoreRank(rank) > 0) {
                count + 1
            } else {
                count + updateRank(rank)
            }
        }
    }
}