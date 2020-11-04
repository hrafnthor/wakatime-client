package `is`.hth.wakatimeclient.wakatime

import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.SingleLoader
import `is`.hth.wakatimeclient.core.util.RateLimiter
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeRemoteDataSource
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeLocalDataSource
import `is`.hth.wakatimeclient.wakatime.model.Project

/**
 * Exposes data access functionality related to the user's coding information
 */
interface CodeRepo {

    /**
     * Retrieves all of the user's [Project]s
     */
    suspend fun getProjects(): Results<List<Project>>

    /**
     * Attempts to retrieve a [Project] matching the supplied id
     */
    suspend fun getProject(id: String): Results<Project>
}

internal class CodeRepoImpl(
    private val limiter: RateLimiter<String>,
    private val remote: WakatimeRemoteDataSource,
    private val local: WakatimeLocalDataSource
) : CodeRepo {

    private val projectsLoader = SingleLoader<List<Project>>()
        .cache {
            local.getProjects()
        }.expired {
            limiter.shouldFetch(keyProjects)
        }.remote {
            remote.getProjects()
        }.update { projects ->
            local.storeProjects(projects).also { result ->
                if (result is Results.Success) {
                    limiter.mark(keyProjects)
                }
            }
        }

    override suspend fun getProjects(): Results<List<Project>> = projectsLoader.execute()

    override suspend fun getProject(id: String): Results<Project> {
        return when (val result: Results<List<Project>> = getProjects()) {
            is Results.Success.Values -> result.data.firstOrNull {
                it.id == id
            }?.let {
                Results.Success.Values(it)
            } ?: Results.Success.Empty
            is Results.Success.Empty -> result
            is Results.Failure -> result
        }
    }

    companion object {
        private const val keyProjects = "projects"
    }
}