package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.auth.Scope.ReadLoggedTime
import `is`.hth.wakatimeclient.core.data.auth.Scope.ReadOrganization
import `is`.hth.wakatimeclient.wakatime.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.time.LocalDate

/**
 * Wakatime API interface as defined here https://wakatime.com/developer
 */
@Suppress("unused")
interface WakatimeApi {

    companion object {
        private const val API_ENDPOINT = "/api/v1"
        private const val USERS = "$API_ENDPOINT/users"
        private const val CURRENT_USER = "$USERS/current"
        private const val PUBLIC_LEADERS = "$API_ENDPOINT/leaders"
        private const val PRIVATE_BOARDS = "$CURRENT_USER/leaderboards"
        private const val STATS = "$CURRENT_USER/stats"
        private const val HEARTBEATS = "$CURRENT_USER/heartbeats"
        private const val GOALS = "$CURRENT_USER/goals"
        private const val DURATIONS_EXTERNAL = "$CURRENT_USER/external_durations"
        private const val DURATIONS_EXTERNAL_BULK = "$DURATIONS_EXTERNAL.bulk"
        private const val ORGANIZATIONS = "$CURRENT_USER/orgs"
        private const val DASHBOARDS = "$ORGANIZATIONS/{organizationId}/dashboards"
        private const val DASHBOARD_MEMBERS = "$DASHBOARDS/{dashboardId}/members"
        private const val MEMBERS_SUMMARY = "$DASHBOARD_MEMBERS/{userId}/summaries"
    }

    /**
     * Retrieves the details of the currently authenticated user.
     */
    @GET(CURRENT_USER)
    suspend fun getCurrentUser(): Response<WrappedResponse<NetworkUser>>

    /**
     * Retrieves the total recorded time for the current user.
     */
    @GET("$CURRENT_USER/all_time_since_today")
    suspend fun getTotalRecord(): Response<WrappedResponse<TotalRecord>>

    /**
     * Retrieves the public leaderboards leaders.
     * Can filter by [language] which will give the public leaders for that language.
     * The results are paginated so iterate by requesting a [page]
     */
    @GET(PUBLIC_LEADERS)
    suspend fun getPublicLeaders(
        @Query("language") language: String?,
        @Query("page") page: Int?,
    ): Response<Leaders>

    /**
     * Retrieves all of the private leaderboards that the currently authenticated
     * user is a member off.
     */
    @GET(PRIVATE_BOARDS)
    suspend fun getPrivateLeaderboards(): Response<PagedResponse<List<Leaderboard>>>

    /**
     * Retrieves the leaders for the specified leaderboard that the currently
     * authenticated user is a member off. Results can be filtered by [language]
     * and [page] number
     */
    @GET("$PRIVATE_BOARDS/{leaderboardId}")
    suspend fun getPrivateLeaders(
        @Path("leaderboardId") leaderboardId: String,
        @Query("language") language: String?,
        @Query("page") page: Int?,
    ): Response<Leaders>

    /**
     * Retrieves a list of all [Project]s that Wakatime has observed this user
     * working on.
     */
    @GET("$USERS/{userId}/projects")
    suspend fun getProjects(
        @Path("userId") userId: String
    ): Response<List<Project>>

    /**
     * Retrieves a list of all [Project]s that Wakatime has observed the currently
     * authenticated user working on.
     */
    @GET("$CURRENT_USER/projects")
    suspend fun getCurrentUsersProjects(): Response<WrappedResponse<List<Project>>>

    /**
     * Retrieves the stats for the current user over the supplied range, optionally filtered
     * by the other inputs
     * @param timeout The timeout value used to calculate these stats. Defaults the the user's timeout value.
     * @param writesOnly The writes_only value used to calculate these stats. Defaults to the user's writes_only setting.
     * @param projectId Show more detailed stats limited to this project
     * @param range The range to filter the stats by
     */
    @GET("$STATS/{range}")
    suspend fun getStats(
        @Path("range") range: String,
        @Query("timeout") timeout: Int? = null,
        @Query("writes_only") writesOnly: Boolean? = null,
        @Query("project") projectId: String? = null,
    ): Response<WrappedResponse<Stats>>

    /**
     * Retrieves the current user's coding activity for the given time range as a
     * list of summaries segmented by day
     * @param start [LocalDate] required: The start date of the time range in 'yyyy-MM-dd' format
     * @param end [LocalDate] required: The end date of the time range in 'yyyy-MM-dd' format
     * @param projectName [String] optional: Filter the summaries to only those related to this project
     * @param branches [Array] optional: Filter the summaries to only those related to these branch names
     * @param timeout [Int] optional: The timeout preference used when joining heartbeats into durations. Defaults to the user's timeout value
     * @param writesOnly [Boolean] optional: Defaults to user's 'writes only' preference
     * @param timezone [String] optional: The timezone for the given start and end dates. Defaults to the user's timezone
     */
    @GET("$CURRENT_USER/summaries")
    suspend fun getSummaries(
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("project") projectName: String?,
        @Query("branches") branches: String?,
        @Query("timeout") timeout: Int?,
        @Query("writes_only") writesOnly: Boolean?,
        @Query("timezone") timezone: String?,
    ): Response<Summaries>

    /**
     * Retrieves a list of [Agent]s used by the current user
     */
    @GET("$CURRENT_USER/user_agents")
    suspend fun getAgents(): Response<WrappedResponse<List<Agent>>>

    /**
     * Retrieves all of the user's [Heartbeat]s for the given date.
     *
     * @param date The day to return heartbeats for, in a YYYY-mm-dd format. Heartbeats will be returned
     * from 12:00 until 23:59 in the user's timezone for this day
     */
    @GET(HEARTBEATS)
    suspend fun getHeartbeats(
        @Query("date") date: String
    ): Response<ChronologicalResponse<Heartbeat>>

    /**
     * Posts a new heartbeat to the service
     * @param beat to register with the server
     */
    @POST(HEARTBEATS)
    suspend fun sendBeat(
        @Body beat: Heartbeat.Beat
    ): Response<PagedResponse<Confirmation>>

    /**
     * Retrieves all of the user's [Goal]s
     *
     * Requires the [ReadLoggedTime] authentication scope
     */
    @GET(GOALS)
    suspend fun getGoals(): Response<PagedResponse<List<Goal>>>

    /**
     * A user's external durations for the given day. External durations
     * are not created by IDE plugins, but are activity from OAuth apps
     * such as meetings.
     *
     * @param day [String] required: to request durations for. Durations will be returned
     * from 00:00 until 23:59 in the user's timezone on this day
     * @param project [String] optional: Only shows durations for this project
     * @param branches [String] optional: Only show durations for these branches;
     * comma separated list of branch names
     * @param timezone [String] optional: The timezone for a given date. Defaults to
     * the user's timezone
     *
     */
    @GET(DURATIONS_EXTERNAL)
    suspend fun getExternalDurations(
        @Query("date") day: String,
        @Query("project") project: String?,
        @Query("branches") branches: String?,
        @Query("timezone") timezone: String?
    ): Response<ChronologicalResponse<ExternalDuration>>

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
    @POST(DURATIONS_EXTERNAL)
    suspend fun sendExternalDuration(
        @Body payload: ExternalDuration
    ): Response<PagedResponse<ExternalDuration>>

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
    @POST(DURATIONS_EXTERNAL_BULK)
    suspend fun sendExternalDurations(
        @Body payloads: List<ExternalDuration>
    ): Response<ResponseBody>

    /**
     * List the user's organizations
     *
     * Requires the [ReadOrganization] authentication scope
     */
    @GET(ORGANIZATIONS)
    suspend fun getOrganizations(): Response<PagedResponse<List<Organization>>>

    /**
     * Fetches all of the organizations [Dashboard]s
     *
     * Requires the [ReadOrganization] authentication scope
     */
    @GET(DASHBOARDS)
    suspend fun getDashboards(
        @Path("organizationId") organizationId: String
    ): Response<PagedResponse<List<Dashboard>>>

    /**
     * Lists a dashboard's members
     *
     * Requires the [ReadOrganization] authentication scope
     */
    @GET(DASHBOARD_MEMBERS)
    suspend fun getDashboardMembers(
        @Path("organizationId") organizationId: String,
        @Path("dashboardId") dashboardId: String
    ): Response<PagedResponse<List<Member>>>

    /**
     * An organization dashboard member’s coding activity for the given time
     * range as an array of summaries segmented by day.
     *
     * Requires the [ReadOrganization] authentication scope
     */
    @GET(MEMBERS_SUMMARY)
    suspend fun getMemberSummaries(
        @Path("organizationId") organizationId: String,
        @Path("dashboardId") dashboardId: String,
        @Path("userId") userId: String,
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("project") projectName: String?,
        @Query("branches") branches: String?,
    ): Response<Summaries>

    /**
     * List of commits for a WakaTime project showing the time spent coding in each commit.
     *
     * Requires the [ReadLoggedTime] authentication scope.
     *
     * @param projectName The human readable name of the project as shown on Wakatime
     * @param author optional: Filter commits to only those authored by the given username.
     * @param branch optional: Filter commits to a branch; defaults to the repo’s default branch name.
     * @param page optional: Page number of commits.
     */
    @GET("users/current/projects/{projectName}/commits")
    suspend fun getProjectCommits(
        @Path("projectName") projectName: String,
        @Query("author") author: String?,
        @Query("branch") branch: String?,
        @Query("page") page: Int?
    ): Response<PagedResponse<Commits>>

    /**
     * A single commit from a WakaTime project showing the time spent coding on the commit.
     *
     * Requires the [ReadLoggedTime] authentication scope.
     *
     * @param projectName The human readable name of the project as shown on Wakatime
     * @param branch optional: Filter the commit to a branch; defaults to the repo’s default branch name.
     */
    @GET("users/current/projects/{projectName}/commits/{hash}")
    suspend fun getProjectCommit(
        @Path("projectName") projectName: String,
        @Path("hash") hash: String,
        @Query("branch") branch: String?
    ): Response<String>
}

interface OauthApi {

    /**
     * Revokes the [token] belonging to the client with matching [id] and [secret].
     * The token can either be the access token or refresh token, depending on
     * which should be revoked.
     */
    @FormUrlEncoded
    @POST("/oauth/revoke")
    suspend fun revoke(
        @Field("client_id") id: String,
        @Field("client_secret") secret: String,
        @Field("token") token: String
    ): Response<Unit>
}