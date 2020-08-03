package `is`.hth.wakatimeclient.wakatime.data.db.dao

import `is`.hth.wakatimeclient.wakatime.data.db.entities.ConfigEntity
import `is`.hth.wakatimeclient.wakatime.data.db.entities.CurrentUserView
import `is`.hth.wakatimeclient.wakatime.data.db.entities.UserEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    /**
     * Selects the current user if one exists, else null
     */
    @Query("SELECT * FROM currentuserview")
    suspend fun getCurrentUser(): CurrentUserView?

    /**
     * Selects a user matching the supplied id if one exists, else null
     */
    @Query("SELECT * FROM users WHERE user_id == :id")
    suspend fun getUser(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(configEntity: ConfigEntity)
}