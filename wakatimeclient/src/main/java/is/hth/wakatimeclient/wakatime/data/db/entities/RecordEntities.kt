package `is`.hth.wakatimeclient.wakatime.data.db.entities

import `is`.hth.wakatimeclient.wakatime.model.TotalRecord
import androidx.room.*

@Entity(
    tableName = "records",
    indices = [Index(value = ["user_id"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "period_total_seconds")
    val periodTotalSeconds: Int,
    @ColumnInfo(name = "period_total_human")
    val periodTotalHuman: Int,
    @ColumnInfo(name = "daily_average_seconds")
    val dailyAverageSeconds: Int,
    @ColumnInfo(name = "daily_average_human")
    val dailyAverageHuman: Int,
    @ColumnInfo(name = "modified_at")
    val modifiedAt: String
)

@Entity(
    tableName = "language_records",
    indices = [Index(
        value = [
            "record_id",
            "language_id"
        ],
        unique = true
    )],
    foreignKeys = [
        ForeignKey(
            entity = RecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["record_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LanguageEntity::class,
            parentColumns = ["id"],
            childColumns = ["language_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class LanguageRecordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "record_id")
    val recordId: Int,
    @ColumnInfo(name = "language_id")
    val languageId: Int,
    @ColumnInfo(name = "period_total_seconds")
    val periodTotalSeconds: Int
)

/**
 * Stores the total recorded time for the current user
 */
@Entity(tableName = "total_record")
data class TotalRecordEntity(
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