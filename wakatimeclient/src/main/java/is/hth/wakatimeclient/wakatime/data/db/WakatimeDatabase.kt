package `is`.hth.wakatimeclient.wakatime.data.db

import `is`.hth.wakatimeclient.BuildConfig
import `is`.hth.wakatimeclient.wakatime.data.db.dao.UserDao
import `is`.hth.wakatimeclient.wakatime.data.db.entities.ConfigEntity
import `is`.hth.wakatimeclient.wakatime.data.db.entities.CurrentUserView
import `is`.hth.wakatimeclient.wakatime.data.db.entities.UserEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

@Database(
    entities = [
        UserEntity::class,
        ConfigEntity::class
    ],
    views = [
        CurrentUserView::class
    ],
    version = 1,
    exportSchema = true
)
internal abstract class WakatimeDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {

        private const val DB_NAME = "wakatime-db"

        @Volatile
        private var instance: WakatimeDatabase? = null

        @Synchronized
        fun getInstance(context: Context): WakatimeDatabase {
            return instance ?: getBuilder(context)
                .addMigrations(*MIGRATIONS.toTypedArray())
                .build()
                .also {
                    instance = it
                }
        }

        private fun getBuilder(context: Context): Builder<WakatimeDatabase> {
            return if (BuildConfig.DEBUG) {
                Room.inMemoryDatabaseBuilder(context, WakatimeDatabase::class.java)
            } else {
                Room.databaseBuilder(context, WakatimeDatabase::class.java, DB_NAME)
            }
        }
    }
}

/**
 * Implement migrations here and add them to this list for
 * implementation in the database if requested.
 */
val MIGRATIONS: List<Migration> = listOf()

