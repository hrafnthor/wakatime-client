package `is`.hth.wakatimeclient.wakatime.data.db.dao

import `is`.hth.wakatimeclient.wakatime.data.db.entities.LanguageEntity
import androidx.room.*

@Dao
interface LanguageDao {

    @Query("SELECT * FROM languages")
    fun getLanguages(): List<LanguageEntity>

    @Query("SELECT * FROM languages WHERE name == :name")
    fun getLanguage(name: String): LanguageEntity?

    /**
     * Attempts to inserts the supplied [LanguageEntity]s, ignoring them if they
     * already exist. Returns a list of all new rowids that were inserted.
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnoreLanguages(vararg languages: LanguageEntity): List<Long>

    /**
     * Attempts to inserts the supplied [LanguageEntity], ignoring it if a row
     * already exist. Returns the new rowid that was inserted or -1 if ignored
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnoreLanguage(language: LanguageEntity): Long

    /**
     * Stores the supplied language name, if not already in the database,
     * returning the generated id of the language. Returns -1 in case of error
     */
    @Transaction
    fun setLanguage(name: String): Long {
        val id: Long = insertIgnoreLanguage(LanguageEntity(name = name))
        return if (id != -1L) id else getLanguage(name)?.id ?: -1L
    }
}