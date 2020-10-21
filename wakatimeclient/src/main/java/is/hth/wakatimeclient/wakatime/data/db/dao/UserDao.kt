package `is`.hth.wakatimeclient.wakatime.data.db.dao

import `is`.hth.wakatimeclient.wakatime.data.db.entities.ConfigEntity
import `is`.hth.wakatimeclient.wakatime.data.db.entities.CurrentUserView
import `is`.hth.wakatimeclient.wakatime.data.db.entities.TotalRecordEntity
import `is`.hth.wakatimeclient.wakatime.data.db.entities.UserEntity
import androidx.room.*

@Dao
internal interface UserDao {

    /**
     * Selects the current user if one exists, else null
     */
    @Query("SELECT * FROM currentuserview")
    fun getCurrentUser(): CurrentUserView?

    /**
     * Selects a user matching the supplied id if one exists, else null
     */
    @Query("SELECT * FROM users WHERE id == :id")
    fun getUser(id: String): UserEntity?

    /**
     * Selects the total reported time record for the current user
     */
    @Query("SELECT * FROM total_record WHERE id == 1")
    fun getTotalRecord(): TotalRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(totalRecord: TotalRecordEntity): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnoreUser(user: UserEntity): Long

    @Update
    fun updateUser(user: UserEntity): Int

    @Transaction
    fun insertOrUpdateUsers(vararg users: UserEntity): Int {
        var count = 0
        users.forEach {
            if (insertOrIgnoreUser(it) > 0) {
                count++
            } else {
                count += updateUser(it)
            }
        }
        return count
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(configEntity: ConfigEntity): Long

    @Query("DELETE FROM users WHERE id == :id")
    fun removeUser(id: String): Int

}