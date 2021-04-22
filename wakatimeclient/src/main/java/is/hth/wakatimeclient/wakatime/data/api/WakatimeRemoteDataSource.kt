package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.auth.AuthClient
import `is`.hth.wakatimeclient.core.data.auth.Scope.ReadOrganization
import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor
import `is`.hth.wakatimeclient.core.data.net.RemoteDataSource
import `is`.hth.wakatimeclient.wakatime.data.model.*
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.*

internal interface WakatimeRemoteDataSource {

    /**
     * Fetches information for the currently authenticated user
     */
    suspend fun getCurrentUser(): Results<CurrentUser>

    /**
     * Fetches the total recorded time for the currently authenticated user.
     *
     * If the data is out of date on the server then this operation will kick off a update
     * process which can take some time depending on how out of date the data is.
     *
     * During such processing, the endpoint will deliver updates on the progress
     * as the client polls for new information.
     */
    suspend fun getTotalRecord(): Results<TotalRecord>

    /**
     *  Fetches leaders from the public leaderboard, using the supplied request for filtering.
     *
     * The language can be skipped which will give the all round public result ordered by
     * total coding activity and daily averages irregardless of language.
     *
     * The page that will be given will be the one containing the current user's ranking
     * if the authentication scope given to the client allows for that.
     *
     * If the required authentication scope access has not been given, the result will be for
     * page 1.
     */
    suspend fun getPublicLeaders(
        request: Leaders.Request
    ): Results<Leaders>

    /**
     * Fetches the private leaderboards that the currently authenticated user is member of.
     */
    suspend fun getLeaderboards(): Results<PagedResponse<Leaderboard>>

    /**
     * Fetches leaders from the requested private leaderboard
     * @param request Contains configuration for filtering the request
     */
    suspend fun getPrivateLeaders(
        request: Leaders.Request
    ): Results<Leaders>

    /**
     * Fetches the projects that Wakatime has observed the currently authenticated user
     * working on.
     */
    suspend fun getProjects(): Results<List<Project>>

    /**
     * Fetches commits for the supplied project name
     *
     * @param projectName the name of the project for which to fetch git commits for
     * @param author optional: the name of the author by which the results should be filtered
     * @param branch optional: the name of the branch by which the results should be filtered. Defaults to the repo's default branch name
     * @param page optional: the page number to fetch
     */
    suspend fun getCommits(
        projectName: String,
        author: String? = null,
        branch: String? = null,
        page: Int? = null
    ): Results<PagedResponse<ProjectCommits>>

    /**
     * Fetches a specific commit from a specific project name
     *
     * @param projectName the name of the associated project
     * @param hash the hash of the commit which should be fetched
     * @param branch optional: the name of the branch by which the results should be filtered. Defaults to the repo's default branch name
     */
    suspend fun getCommit(
        projectName: String,
        hash: String,
        branch: String? = null
    ): Results<ProjectCommit>

    /**
     * Fetches the [Stats] for the current user filtered by the supplied request
     * @param request defines the filtering to apply
     */
    suspend fun getStats(
        request: Stats.Request
    ): Results<Stats>

    /**
     * Fetches the [Summaries] for the current user filtered by the supplied request
     * @param request defined the filtering to apply
     */
    suspend fun getSummaries(
        request: Summaries.Request
    ): Results<Summaries>

    /**
     * Fetches the [Agent]s used by the current user
     */
    suspend fun getAgents(): Results<List<Agent>>

    /**
     * Fetches all of the heartbeats given for the supplied date
     *
     * @param date to fetch heartbeats for
     */
    suspend fun getHeartbeats(
        date: Date
    ): Results<ChronologicalResponse<Heartbeat>>

    /**
     * Sends the supplied [Heartbeat.Beat] to the service for recording
     *
     * @param beat to record with the service
     */
    suspend fun sendHeartbeat(
        beat: Heartbeat.Beat
    ): Results<Confirmation>

    /**
     * Fetches all of the user's [Goal]s
     */
    suspend fun getGoals(): Results<PagedResponse<Goal>>

    /**
     * A user's external durations for the given day. External durations
     * are not created by IDE plugins, but are activity from OAuth apps
     * such as meetings.
     */
    suspend fun getExternalDurations(
        request: ExternalDuration.Request
    ): Results<ChronologicalResponse<ExternalDuration>>

    /**
     * Creates a duration representing activity for a user with start and end time,
     * when Heartbeat pings aren’t available. For ex: meetings.
     *
     * External durations are not created by IDE plugins, only OAuth apps can create
     * external durations. External durations must be created within one year from
     * Today, and must not start before the associated user’s account was created.
     *
     * Use external_id to prevent creating duplicate durations.
     * Using the same external_id will update any existing duration with the provided attributes.
     */
    suspend fun sendExternalDuration(
        payload: ExternalDuration
    ): Results<ExternalDuration>

    /**
     * Creates a duration representing activity for a user with start and end time,
     * when Heartbeat pings aren’t available. For ex: meetings.
     *
     * External durations are not created by IDE plugins, only OAuth apps can create
     * external durations. External durations must be created within one year from
     * Today, and must not start before the associated user’s account was created.
     *
     * Use external_id to prevent creating duplicate durations.
     * Using the same external_id will update any existing duration with the provided attributes.
     *
     * Allows for the bulk delivery of [ExternalDuration]s.
     *
     * The bulk endpoint accepts an array of external durations, limited to 1,000 per
     * POST request. The bulk endpoint will return 201 response status code with an array
     * of status_codes for each duration sent. That’s because most invalid durations can
     * be omitted without problems while still allowing your app’s valid durations.
     *
     * Parsing of the resulting response is currently left to the consumer.
     */
    suspend fun sendExternalDurations(
        payloads: List<ExternalDuration>
    ): Results<ResponseBody>

    /**
     * List the user's organizations
     *
     * Requires the [ReadOrganization] authentication scope
     */
    suspend fun getOrganizations(): Results<PagedResponse<Organization>>

    /**
     * Lists all of the organization's dashboards
     *
     * Requires the [ReadOrganization] authentication scope
     */
    suspend fun getDashboards(
        organizationId: String
    ): Results<PagedResponse<Dashboard>>

    /**
     * List an organization’s members.
     */
    suspend fun getDashboardMembers(
        organizationId: String,
        dashboardId: String
    ): Results<PagedResponse<Member>>

    /**
     * An organization dashboard member’s coding activity for the
     * given time range as an array of summaries segmented by day.
     */
    suspend fun getMemberSummaries(
        request: Summaries.DashboardRequest
    ): Results<Summaries>

    suspend fun getExports(): Results<PagedResponse<Export>>
}

internal class WakatimeRemoteDataSourceImpl(
    session: AuthClient.Session,
    processor: NetworkErrorProcessor,
    private val api: WakatimeApi,
) : RemoteDataSource(session, processor), WakatimeRemoteDataSource {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    override suspend fun getPublicLeaders(
        request: Leaders.Request
    ): Results<Leaders> {
        return makeCall {
            api.getPublicLeaders(request.language, request.page)
        }
    }

    override suspend fun getCurrentUser(): Results<CurrentUser> {
        return makeCall(networkCall = {
            api.getCurrentUser()
        }, transform = {
            it.data.toCurrentUser()
        })
    }

    override suspend fun getTotalRecord(): Results<TotalRecord> {
        return makeCall(networkCall = {
            api.getTotalRecord()
        }, transform = {
            it.data
        })
    }

    override suspend fun getLeaderboards(): Results<PagedResponse<Leaderboard>> {
        return makeCall {
            api.getPrivateLeaderboards()
        }
    }

    override suspend fun getPrivateLeaders(
        request: Leaders.Request
    ): Results<Leaders> {
        return makeCall {
            api.getPrivateLeaders(
                leaderboardId = request.leaderboardId,
                language = request.language,
                page = request.page
            )
        }
    }

    override suspend fun getProjects(): Results<List<Project>> {
        return makeCall(networkCall = {
            api.getCurrentUsersProjects()
        }, transform = {
            it.data
        })
    }

    override suspend fun getCommits(
        projectName: String,
        author: String?,
        branch: String?,
        page: Int?
    ): Results<PagedResponse<ProjectCommits>> {
        return makeCall {
            api.getProjectCommits(
                projectName = projectName,
                author = author,
                branch = branch,
                page = page
            )
        }
    }

    override suspend fun getCommit(
        projectName: String,
        hash: String,
        branch: String?
    ): Results<ProjectCommit> {
        return makeCall {
            api.getProjectCommit(
                projectName = projectName,
                hash = hash,
                branch = branch
            )
        }
    }

    override suspend fun getStats(
        request: Stats.Request
    ): Results<Stats> {
        return makeCall(networkCall = {
            api.getStats(
                range = request.range.description,
                timeout = request.meta?.timeout,
                writesOnly = request.meta?.writesOnly,
                projectId = request.project?.projectName
            )
        }, transform = {
            it.data
        })
    }

    override suspend fun getSummaries(
        request: Summaries.Request
    ): Results<Summaries> {
        return makeCall(networkCall = {
            api.getSummaries(
                start = format(request.startDate.time),
                end = format(request.endDate.time),
                projectName = request.project?.projectName,
                branches = request.project?.branches,
                timeout = request.meta?.timeout,
                timezone = request.timezone,
                writesOnly = request.meta?.writesOnly
            )
        })
    }

    override suspend fun getAgents(): Results<List<Agent>> {
        return makeCall(networkCall = {
            api.getAgents()
        }, transform = {
            it.data
        })
    }

    override suspend fun getHeartbeats(
        date: Date
    ): Results<ChronologicalResponse<Heartbeat>> {
        return makeCall {
            api.getHeartbeats(format(date))
        }
    }

    override suspend fun sendHeartbeat(
        beat: Heartbeat.Beat
    ): Results<Confirmation> {
        return makeCall(
            networkCall = {
                api.sendBeat(beat)
            }, transform = {
                it.data.first()
            })
    }

    override suspend fun getGoals(): Results<PagedResponse<Goal>> {
        return makeCall {
            api.getGoals()
        }
    }

    override suspend fun getExternalDurations(
        request: ExternalDuration.Request
    ): Results<ChronologicalResponse<ExternalDuration>> {
        return makeCall {
            api.getExternalDurations(
                day = format(request.date.time),
                project = request.project?.projectName,
                branches = request.project?.branches,
                timezone = request.timezone
            )
        }
    }

    override suspend fun sendExternalDuration(
        payload: ExternalDuration
    ): Results<ExternalDuration> {
        return makeCall(
            networkCall = {
                api.sendExternalDuration(payload)
            }, transform = {
                it.data.first()
            })
    }

    override suspend fun sendExternalDurations(
        payloads: List<ExternalDuration>
    ): Results<ResponseBody> {
        return makeCall {
            api.sendExternalDurations(payloads)
        }
    }

    override suspend fun getOrganizations(): Results<PagedResponse<Organization>> {
        return makeCall {
            api.getOrganizations()
        }
    }

    override suspend fun getDashboards(
        organizationId: String
    ): Results<PagedResponse<Dashboard>> {
        return makeCall {
            api.getDashboards(organizationId)
        }
    }

    override suspend fun getDashboardMembers(
        organizationId: String,
        dashboardId: String
    ): Results<PagedResponse<Member>> {
        return makeCall {
            api.getDashboardMembers(organizationId, dashboardId)
        }
    }

    override suspend fun getMemberSummaries(
        request: Summaries.DashboardRequest
    ): Results<Summaries> {
        return makeCall {
            api.getMemberSummaries(
                organizationId = request.organizationId,
                dashboardId = request.dashboardId,
                userId = request.userId,
                start = format(request.startDate.time),
                end = format(request.endDate.time),
                branches = request.project?.branches,
                projectName = request.project?.projectName
            )
        }
    }

    override suspend fun getExports(): Results<PagedResponse<Export>> {
        return makeCall {
            api.getExports()
        }
    }

    private fun format(date: Date): String = dateFormatter.format(date)
}
