package `is`.hth.wakatimeclient.wakatime.data.db.entities

import `is`.hth.wakatimeclient.wakatime.model.TotalRecord
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores the total recorded time for the current user
 */
@Entity(tableName = "total_record")
internal data class TotalRecordEntity(
    /**
     * Static primary key for easier insert with conflict replacement
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 1,
    @ColumnInfo(name = "is_up_to_date")
    val isUpToDate: Boolean,
    @ColumnInfo(name = "percent_calculated")
    val percentCalculated: Int,
    @ColumnInfo(name = "total_recorded_seconds")
    val totalRecordedSeconds: Float,
    @ColumnInfo(name = "total_recorded_time_readable")
    val totalRecordedTimeReadable: String
)

/**
 * Converts the database entity to the public model
 */
internal fun TotalRecordEntity.toTotalRecord(): TotalRecord = TotalRecord(
    isUpToDate = isUpToDate,
    percentCalculated = percentCalculated,
    totalRecordedSeconds = totalRecordedSeconds,
    totalRecordedTimeReadable = totalRecordedTimeReadable
)

/**
 * Converts the public model to the database entity
 */
internal fun TotalRecord.toTotalRecordEntity(): TotalRecordEntity = TotalRecordEntity(
    isUpToDate = isUpToDate,
    percentCalculated = percentCalculated,
    totalRecordedSeconds = totalRecordedSeconds,
    totalRecordedTimeReadable = totalRecordedTimeReadable
)