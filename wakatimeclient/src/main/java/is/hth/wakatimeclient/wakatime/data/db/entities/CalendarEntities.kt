package `is`.hth.wakatimeclient.wakatime.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "periods",
    indices = [
        Index(
            value = ["start_date", "end_date"],
            unique = true
        )
    ]
)
data class PeriodEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "start_date")
    val startDate: String,
    @ColumnInfo(name = "end_date")
    val endDate: String
)