package `is`.hth.wakatimeclient.wakatime.data.db.dao

import `is`.hth.wakatimeclient.wakatime.data.db.entities.ProjectEntity
import androidx.room.*

@Dao
internal interface ProjectDao {

    @Query("SELECT * FROM projects")
    fun getProjects(): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE id == :id")
    fun getProject(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(project: ProjectEntity): Long

    @Update
    fun update(project: ProjectEntity): Int

    /**
     * Attempts to insert the supplied values if they do not already exist,
     * in which case they will be updated. Returns the number of rows affected
     */
    @Transaction
    fun insertOrUpdate(vararg projects: ProjectEntity): Int {
        return projects.fold(0) { count, project ->
            if (insertOrIgnore(project) > 0) {
                count + 1
            } else {
                count + update(project)
            }
        }
    }
}