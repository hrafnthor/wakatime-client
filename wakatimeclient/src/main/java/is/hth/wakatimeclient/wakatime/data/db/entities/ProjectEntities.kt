package `is`.hth.wakatimeclient.wakatime.data.db.entities

import `is`.hth.wakatimeclient.wakatime.model.Project
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
internal data class ProjectEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "repository")
    val repository: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @ColumnInfo(name = "last_heartbeat_at")
    val lastHeartbeatAt: String,
    @ColumnInfo(name = "has_public_url")
    val hasPublicUrl: Boolean
)

internal fun ProjectEntity.toModel(): Project = Project(
    id = id,
    name = name,
    repository = repository,
    createdAt = createdAt,
    lastHeartbeatAt = lastHeartbeatAt,
    hasPublicUrl = hasPublicUrl
)

internal fun Project.toEntity(): ProjectEntity = ProjectEntity(
    id = id,
    name = name,
    repository = repository,
    createdAt = createdAt,
    lastHeartbeatAt = lastHeartbeatAt,
    hasPublicUrl = hasPublicUrl
)