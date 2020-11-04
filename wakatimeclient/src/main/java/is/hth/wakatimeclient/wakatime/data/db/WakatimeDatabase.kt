package `is`.hth.wakatimeclient.wakatime.data.db

import `is`.hth.wakatimeclient.BuildConfig
import `is`.hth.wakatimeclient.wakatime.data.db.dao.*
import `is`.hth.wakatimeclient.wakatime.data.db.entities.*
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors

@Database(
    entities = [
        UserEntity::class,
        ConfigEntity::class,
        TotalRecordEntity::class,
        LanguageEntity::class,
        LeaderboardEntity::class,
        UserRankEntity::class,
        PeriodEntity::class,
        ProjectEntity::class,
    ],
    views = [
        CurrentUserView::class
    ],
    version = 1,
    exportSchema = true
)
internal abstract class WakatimeDatabase : RoomDatabase(), MasterDao {

    companion object {

        private const val DB_NAME = "wakatime-db"

        @Volatile
        private var instance: WakatimeDatabase? = null

        @Synchronized
        fun getInstance(context: Context): WakatimeDatabase {
            return instance ?: synchronized(this) {
                build(context).also {
                    instance = it
                }
            }
        }

        private fun build(context: Context): WakatimeDatabase {
            return getBuilder(context)
                .addMigrations(*MIGRATIONS.toTypedArray())
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        Executors.newSingleThreadExecutor().execute {
                            getInstance(context).let {
                                // Perform any database initialization needed here
                                it.rankings()
                                    .insertOrIgnoreLeaderboard(LeaderboardEntity.publicLeaderboard)
                                it.languages().insertIgnoreLanguage(LanguageEntity.none)
                            }
                        }
                    }
                }).build()
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

internal interface MasterDao {

    /**
     * The user domain dao
     */
    fun users(): UserDao

    /**
     * The leaderboard and ranking domain
     */
    fun rankings(): RankingDao

    /**
     * Language domain dao
     */
    fun languages(): LanguageDao

    /**
     * Calendar related dao
     */
    fun calendar(): CalendarDao

    /**
     * Project domain dao
     */
    fun projects(): ProjectDao
}

/**
 * Implement migrations here and add them to this list for
 * implementation in the database if requested.
 */
val MIGRATIONS: List<Migration> = listOf()

