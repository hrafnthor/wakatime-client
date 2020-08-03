package `is`.hth.wakatimeclient.wakatime.data.users

import `is`.hth.wakatimeclient.core.data.ErrorFactory
import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.util.networkOperation
import `is`.hth.wakatimeclient.core.util.safeOperation
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeService
import `is`.hth.wakatimeclient.wakatime.data.api.dto.toCurrentUser
import `is`.hth.wakatimeclient.wakatime.model.CurrentUser

/**
 * Exposes remote data load operations related to user specific values
 */
interface UserRemoteDataSource {

    /**
     * Fetches information for the currently authenticated user
     */
    suspend fun getCurrentUser(): Results<CurrentUser>
}

internal class UserRemoteDataSourceImpl(
    private val errors: ErrorFactory,
    private val service: WakatimeService
) : UserRemoteDataSource {

    override suspend fun getCurrentUser(): Results<CurrentUser> = safeOperation(
        operation = {
            networkOperation(
                operation = { service.getCurrentUser() },
                convert = { it.toCurrentUser() },
                error = { errors.onCode(it) }
            )
        },
        error = { errors.onThrowable(it) }
    )
}

