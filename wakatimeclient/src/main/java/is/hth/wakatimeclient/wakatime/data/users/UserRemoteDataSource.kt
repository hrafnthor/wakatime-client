package `is`.hth.wakatimeclient.wakatime.data.users

import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor
import `is`.hth.wakatimeclient.core.data.net.RemoteDataSource
import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.auth.AuthClient
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeApi
import `is`.hth.wakatimeclient.wakatime.data.api.dto.FullUserDto
import `is`.hth.wakatimeclient.wakatime.data.api.dto.TotalRecordDto
import `is`.hth.wakatimeclient.wakatime.data.api.dto.toCurrentUser
import `is`.hth.wakatimeclient.wakatime.data.api.dto.toTotalRecord
import `is`.hth.wakatimeclient.wakatime.model.CurrentUser
import `is`.hth.wakatimeclient.wakatime.model.TotalRecord

/**
 * Exposes remote data load operations related to user specific values
 */
interface UserRemoteDataSource {

    /**
     * Fetches information for the currently authenticated user
     */
    suspend fun getCurrentUser(): Results<CurrentUser>

    /**
     * Fetches the total recorded time for the currently authenticated user.
     *
     * If the data is out of date then this operation will kick off a update
     * process which can take some time depending on how out of date the data is.
     *
     * During such processing, the endpoint will deliver updates on the progress.
     */
    suspend fun getTotalRecord(): Results<TotalRecord>
}

internal class UserRemoteDataSourceImpl(
    session: AuthClient.Session,
    processor: NetworkErrorProcessor,
    private val api: WakatimeApi
) : RemoteDataSource(session, processor), UserRemoteDataSource {

    override suspend fun getCurrentUser(): Results<CurrentUser> =
        makeCall(FullUserDto::toCurrentUser) {
            api.getCurrentUser()
        }

    override suspend fun getTotalRecord(): Results<TotalRecord> =
        makeCall(TotalRecordDto::toTotalRecord) {
            api.getTotalRecord()
        }
}

