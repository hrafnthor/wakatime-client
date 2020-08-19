package `is`.hth.wakatimeclient.wakatime.data.users

import `is`.hth.wakatimeclient.core.data.ErrorFactory
import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.util.networkOperation
import `is`.hth.wakatimeclient.core.util.safeOperation
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeService
import `is`.hth.wakatimeclient.wakatime.data.api.dto.toCurrentUser
import `is`.hth.wakatimeclient.wakatime.data.api.dto.toTotalRecord
import `is`.hth.wakatimeclient.wakatime.model.CurrentUser
import `is`.hth.wakatimeclient.wakatime.model.TotalRecord
import retrofit2.Response

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
    private val errors: ErrorFactory<Response<*>>,
    private val service: WakatimeService
) : UserRemoteDataSource {

    override suspend fun getCurrentUser(): Results<CurrentUser> = safeOperation(
        operation = {
            networkOperation(
                operation = { service.getCurrentUser() },
                convert = { it.toCurrentUser() },
                error = { errors.onValue(it) }
            )
        },
        error = { errors.onThrowable(it) }
    )

    override suspend fun getTotalRecord(): Results<TotalRecord> = safeOperation(
        operation = {
            networkOperation(
                operation = { service.getTotalRecord() },
                convert = { it.toTotalRecord() },
                error = { errors.onCode(it.code()) }
            )
        },
        error = { errors.onThrowable(it) }
    )
}

