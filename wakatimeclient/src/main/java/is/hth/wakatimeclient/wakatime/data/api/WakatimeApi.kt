package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.net.EnvelopePayload
import `is`.hth.wakatimeclient.wakatime.data.api.model.ResponseWrapper
import `is`.hth.wakatimeclient.wakatime.model.*
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
    }

    /**
     * Retrieves the details of the currently authenticated user.
     */
    @EnvelopePayload("data")
    @GET(CURRENT_USER)
    suspend fun getCurrentUser(): Response<ResponseWrapper<FullUser>>

    /**
     * Retrieves the total recorded time for the current user.
     */
    @EnvelopePayload("data")
    @GET("$CURRENT_USER/all_time_since_today")
    suspend fun getTotalRecord(): Response<ResponseWrapper<TotalRecord>>

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
    suspend fun getPrivateLeaderboards(): Response<ResponseWrapper<List<Leaderboard>>>

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
    suspend fun getProjects(@Path("userId") userId: String): Response<List<Project>>

    /**
     * Retrieves a list of all [Project]s that Wakatime has observed the currently
     * authenticated user working on.
     */
    @GET("$CURRENT_USER/projects")
    suspend fun getCurrentUsersProjects(): Response<ResponseWrapper<List<Project>>>

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
    ): Response<ResponseWrapper<Stats>>

    /**
     * Retrieves the current user's coding activity for the given time range as a
     * list of summaries segmented by day
     * @param start [LocalDate] required: The start date of the time range in 'yyyy-MM-dd' format
     * @param end [LocalDate] required: The end date of the time range in 'yyyy-MM-dd' format
     * @param projectId [String] optional: Filter the summaries to only those related to this project
     * @param branches [Array] optional: Filter the summaries to only those related to these branch names
     * @param timeout [Int] optional: The timeout preference used when joining heartbeats into durations. Defaults to the user's timeout value
     * @param writesOnly [Boolean] optional: Defaults to user's 'writes only' preference
     * @param timezone [String] optional: The timezone for the given start and end dates. Defaults to the user's timezone
     */
    @GET("$CURRENT_USER/summaries")
    suspend fun getSummaries(
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("project") projectId: String?,
        @Query("branches") branches: String?,
        @Query("timeout") timeout: Int?,
        @Query("writes_only") writesOnly: Boolean?,
        @Query("timezone") timezone: String?,
    ): Response<Summaries>

    /**
     * Retrieves a list of [Agent]s used by the current user
     */
    @GET("$CURRENT_USER/user_agents")
    suspend fun getAgents(): Response<ResponseWrapper<List<Agent>>>

    /**
     * Retrieves all of the user's [Heartbeats] for the given date.
     *
     * @param date The day to return heartbeats for, in a YYYY-mm-dd format. Heartbeats will be returned
     * from 12:00 until 23:59 in the user's timezone for this day
     */
    @GET(HEARTBEATS)
    suspend fun getHeartbeats(
        @Query("date") date: String
    ): Response<Heartbeats>

    /**
     * Posts a new heartbeat to the service
     * @param beat to register with the server
     */
    @POST(HEARTBEATS)
    suspend fun sendBeat(
        @Body beat: Heartbeat.Beat
    ): Response<ResponseWrapper<Confirmation>>
}