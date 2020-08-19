package `is`.hth.wakatimeclient.wakatime.data.db.dao

import `is`.hth.wakatimeclient.wakatime.data.db.entities.ConfigEntity
import `is`.hth.wakatimeclient.wakatime.data.db.entities.CurrentUserView
import `is`.hth.wakatimeclient.wakatime.data.db.entities.TotalRecordEntity
import `is`.hth.wakatimeclient.wakatime.data.db.entities.UserEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface UserDao {

    /**
     * Selects the current user if one exists, else null
     */
    @Query("SELECT * FROM currentuserview")
    suspend fun getCurrentUser(): CurrentUserView?

    /**
     * Selects a user matching the supplied id if one exists, else null
     */
    @Query("SELECT * FROM users WHERE id == :id")
    suspend fun getUser(id: String): UserEntity?

    /**
     * Selects the total reported time record for the current user
     */
    @Query("SELECT * FROM total_record WHERE id == 1")
    suspend fun getTotalRecord(): TotalRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(configEntity: ConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(totalRecord: TotalRecordEntity)

}