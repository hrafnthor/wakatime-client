package `is`.hth.wakatimeclient.wakatime.data.db.dao

import `is`.hth.wakatimeclient.wakatime.data.db.entities.PeriodEntity
import androidx.room.*

@Dao
interface CalendarDao {

    /**
     * Attempts to find a [PeriodEntity] that matches the supplied start and end
     * date if any exists
     */
    @Query("SELECT * FROM periods WHERE start_date==:startDate AND end_date==:endDate")
    fun getPeriod(startDate: String, endDate: String): PeriodEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore(period: PeriodEntity): Long

    /**
     * Creates a new period record for the supplied start and end dates, if one does
     * not already exist. Returns the record id or -1L if an error occurs
     */
    @Transaction
    fun setPeriod(startDate: String, endDate: String): Long {
        val id: Long = insertIgnore(PeriodEntity(startDate = startDate, endDate = endDate))
        return if (id != -1L) id else getPeriod(startDate, endDate)?.id ?: -1L
    }
}