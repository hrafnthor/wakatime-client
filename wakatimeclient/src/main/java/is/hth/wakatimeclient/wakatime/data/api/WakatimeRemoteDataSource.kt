package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.auth.AuthClient
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
    suspend fun getLeaderboards(): Results<List<Leaderboard>>

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
    suspend fun getGoals(): Results<List<Goal>>

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
        payload: ExternalDuration.Payload
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
        payloads: List<ExternalDuration.Payload>
    ): Results<ResponseBody>
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

    override suspend fun getLeaderboards(): Results<List<Leaderboard>> {
        return makeCall(networkCall = {
            api.getPrivateLeaderboards()
        }, transform = {
            it.data
        })
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

    override suspend fun getStats(
        request: Stats.Request
    ): Results<Stats> {
        return makeCall(networkCall = {
            api.getStats(
                range = request.range.description,
                timeout = request.timeout,
                writesOnly = request.writesOnly,
                projectId = request.projectId
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
                start = request.start,
                end = request.end,
                projectName = request.projectName,
                branches = request.branches,
                timeout = request.timeout,
                timezone = request.timezone,
                writesOnly = request.writesOnly
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
            api.getHeartbeats(dateFormatter.format(date))
        }
    }

    override suspend fun sendHeartbeat(
        beat: Heartbeat.Beat
    ): Results<Confirmation> {
        return makeCall(
            networkCall = {
                api.sendBeat(beat)
            }, transform = {
                it.data
            })
    }

    override suspend fun getGoals(): Results<List<Goal>> {
        return makeCall(
            networkCall = {
                api.getGoals()
            }, transform = {
                it.data
            })
    }

    override suspend fun getExternalDurations(
        request: ExternalDuration.Request
    ): Results<ChronologicalResponse<ExternalDuration>> {
        return makeCall {
            api.getExternalDurations(
                day = request.date,
                project = request.projectName,
                branches = request.branches,
                timezone = request.timezone
            )
        }
    }

    override suspend fun sendExternalDuration(
        payload: ExternalDuration.Payload
    ): Results<ExternalDuration> {
        return makeCall(
            networkCall = {
                api.sendExternalDuration(payload)
            }, transform = {
                it.data
            })
    }

    override suspend fun sendExternalDurations(
        payloads: List<ExternalDuration.Payload>
    ): Results<ResponseBody> {
        return makeCall {
            api.sendExternalDurations(payloads)
        }
    }
}