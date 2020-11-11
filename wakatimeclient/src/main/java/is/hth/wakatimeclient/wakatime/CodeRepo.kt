package `is`.hth.wakatimeclient.wakatime

import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.SingleLoader
import `is`.hth.wakatimeclient.core.util.RateLimiter
import `is`.hth.wakatimeclient.core.util.firstOr
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeRemoteDataSource
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeLocalDataSource
import `is`.hth.wakatimeclient.wakatime.model.Project
import `is`.hth.wakatimeclient.wakatime.model.Range
import `is`.hth.wakatimeclient.wakatime.model.Stats

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

    /**
     * Retrieves the [Stats] for the current user over the supplied range, optionally filtered
     * by the other inputs
     * @param timeout       The timeout value used to calculate these stats.
     *                      Defaults the the user's timeout value.
     * @param writesOnly    The writes_only value used to calculate these stats.
     *                      Defaults to the user's writes_only setting.
     * @param projectId     Show more detailed stats limited to this project
     */
    suspend fun getStats(
        timeout: Int? = null,
        writesOnly: Boolean? = null,
        projectId: String? = null,
        range: Range
    ): Results<Stats>
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
            is Results.Success.Values -> result.data.firstOr(
                default = Results.Success.Empty,
                predicate = { it.id == id }) {
                Results.Success.Values(it)
            }
            is Results.Success.Empty -> result
            is Results.Failure -> result
        }
    }

    override suspend fun getStats(
        timeout: Int?,
        writesOnly: Boolean?,
        projectId: String?,
        range: Range
    ): Results<Stats> = remote.getStats(timeout, writesOnly, projectId, range = range)

    companion object {
        private const val keyProjects = "projects"
    }
}