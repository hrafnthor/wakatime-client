package `is`.hth.wakatimeclient.core.data.db

import androidx.room.RoomDatabase

internal open class DatabaseClient(private val database: RoomDatabase) {

    fun clear(): Unit = database.clearAllTables()
}