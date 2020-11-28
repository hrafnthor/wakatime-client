package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.auth.AuthClient
import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor
import `is`.hth.wakatimeclient.core.data.net.RemoteDataSource
import `is`.hth.wakatimeclient.wakatime.model.*

internal interface WakatimeRemoteDataSource {
    /**
     * Fetches information for the currently authenticated user
     */
    suspend fun getCurrentUser(): Results<FullUser>

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
     * Fetches the requested [page] of the public leaderboard for the supplied [language].
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
    suspend fun getPublicLeaders(language: String = "", page: Int): Results<Leaders>

    /**
     * Fetches the private leaderboards that the currently authenticated user is member of.
     */
    suspend fun getLeaderboards(): Results<List<Leaderboard>>

    /**
     * Fetches the leaders on the specific leaderboard matching the supplied [leaderboardId],
     * optionally filtered by [language] and a certain page
     */
    suspend fun getPrivateLeaders(
        leaderboardId: String,
        language: String = "",
        page: Int = 0
    ): Results<Leaders>

    /**
     * Fetches the projects that Wakatime has observed the currently authenticated user
     * working on.
     */
    suspend fun getProjects(): Results<List<Project>>

    /**
     * Fetches the [Stats] for the current user over using the supplied request
     * @param request defines the filtering to apply
     */
    suspend fun getStats(
       request: Stats.Request
    ): Results<Stats>

    suspend fun getSummaries(request: Summaries.Request): Results<Summaries>
}

internal class WakatimeRemoteDataSourceImpl(
    session: AuthClient.Session,
    processor: NetworkErrorProcessor,
    private val api: WakatimeApi,
) : RemoteDataSource(session, processor), WakatimeRemoteDataSource {

    override suspend fun getPublicLeaders(
        language: String,
        page: Int
    ): Results<Leaders> {
        return makeCall {
            api.getPublicLeaders(language, page)
        }
    }

    override suspend fun getCurrentUser(): Results<FullUser> {
        return makeCall(networkCall = {
            api.getCurrentUser()
        }, transform = {
            it.data
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
        leaderboardId: String,
        language: String,
        page: Int
    ): Results<Leaders> {
        return makeCall {
            api.getPrivateLeaders(leaderboardId, language, page)
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

    override suspend fun getSummaries(request: Summaries.Request): Results<Summaries> {
        return makeCall(networkCall = {
            api.getSummaries(
                start = request.start,
                end = request.end,
                projectId = request.projectId,
                branches = request.branches,
                timeout = request.timeout,
                timezone = request.timezone,
                writesOnly = request.writesOnly
            )
        })
    }
}