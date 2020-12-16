package `is`.hth.wakatimeclient.wakatime

import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.SingleLoader
import `is`.hth.wakatimeclient.core.util.RateLimiter
import `is`.hth.wakatimeclient.core.util.firstOr
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeRemoteDataSource
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeLocalDataSource
import `is`.hth.wakatimeclient.wakatime.model.*
import java.util.*

/**
 * Exposes data access functionality related to the user's coding information
 */
interface ActivityRepo {

    /**
     * Retrieves all of the user's [Project]s
     */
    suspend fun getProjects(): Results<List<Project>>

    /**
     * Attempts to retrieve a [Project] matching the supplied id
     * @param id The project id to fetch
     */
    suspend fun getProject(id: String): Results<Project>

    /**
     * Retrieves the [Stats] for the current user over the supplied range, optionally filtered
     * by the other inputs
     * @param request defines filtering to apply to the network request
     */
    suspend fun getStats(
        request: Stats.Request
    ): Results<Stats>

    /**
     * Retrieves the [Summaries] for the current user
     * @param request Defines the network request for the summaries
     */
    suspend fun getSummaries(request: Summaries.Request): Results<Summaries>

    /**
     * Retrieves any [Heartbeats] that the user got for the requested date
     * @param date for which to fetch a list of heartbeats for
     */
    suspend fun getHeartbeats(
        date: Date
    ): Results<Heartbeats>

    /**
     * Sends the supplied [Heartbeat.Beat] to the service for recording
     */
    suspend fun sendHeartbeat(
        beat: Heartbeat.Beat
    ): Results<Confirmation>
}

internal class ActivityRepoImpl(
    private val limiter: RateLimiter<String>,
    private val remote: WakatimeRemoteDataSource,
    private val local: WakatimeLocalDataSource
) : ActivityRepo {

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
        request: Stats.Request
    ): Results<Stats> = remote.getStats(request)

    override suspend fun getSummaries(
        request: Summaries.Request
    ): Results<Summaries> = remote.getSummaries(request)

    override suspend fun getHeartbeats(
        date: Date
    ): Results<Heartbeats> = remote.getHeartbeats(date)

    override suspend fun sendHeartbeat(
        beat: Heartbeat.Beat
    ): Results<Confirmation> = remote.sendHeartbeat(beat)

    companion object {
        private const val keyProjects = "projects"
    }
}