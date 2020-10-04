package `is`.hth.wakatimeclient.wakatime.data.db.entities

import `is`.hth.wakatimeclient.wakatime.model.Language
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "languages",
    indices = [Index(
        value = ["name"],
        unique = true
    )]
)
data class LanguageEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "name")
    val name: String
) {
    companion object {

        /**
         * A language record indicating no specific language connection
         */
        val none: LanguageEntity = LanguageEntity(1L, "none")
    }
}

fun LanguageEntity.toModel(): Language = Language(id = id, name = name)