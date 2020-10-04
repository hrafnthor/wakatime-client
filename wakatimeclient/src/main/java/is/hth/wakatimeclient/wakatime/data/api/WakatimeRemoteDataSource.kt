package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.auth.AuthClient
import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor
import `is`.hth.wakatimeclient.core.data.net.RemoteDataSource
import `is`.hth.wakatimeclient.wakatime.model.FullUser
import `is`.hth.wakatimeclient.wakatime.model.Leaders
import `is`.hth.wakatimeclient.wakatime.model.TotalRecord

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
    suspend fun getPublicLeaderboard(language: String = "", page: Int): Results<Leaders>
}

internal class WakatimeRemoteDataSourceImpl(
    session: AuthClient.Session,
    processor: NetworkErrorProcessor,
    private val api: WakatimeApi,
) : RemoteDataSource(session, processor), WakatimeRemoteDataSource {

    override suspend fun getPublicLeaderboard(
        language: String,
        page: Int
    ): Results<Leaders> =
        makeCall(networkCall = {
            api.getPublicLeaderboard(language, page)
        }, convert = {
            it
        })

    override suspend fun getCurrentUser(): Results<FullUser> =
        makeCall(networkCall = {
            api.getCurrentUser()
        }, convert = {
            it.data
        })

    override suspend fun getTotalRecord(): Results<TotalRecord> =
        makeCall(networkCall = {
            api.getTotalRecord()
        }, convert = {
            it.data
        })
}