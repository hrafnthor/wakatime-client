package `is`.hth.wakatimeclient.wakatime

import `is`.hth.wakatimeclient.core.data.Loader
import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.SingleLoader
import `is`.hth.wakatimeclient.core.util.RateLimiter
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeRemoteDataSource
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeLocalDataSource
import `is`.hth.wakatimeclient.wakatime.data.db.entities.toCurrentUserView
import `is`.hth.wakatimeclient.wakatime.data.db.entities.toTotalRecordEntity
import `is`.hth.wakatimeclient.wakatime.model.*

/**
 * Exposes data access functionality related to user information
 */
interface UserRepo {

    /**
     * Retrieves the user information for the currently authenticated user
     */
    suspend fun getCurrentUser(): Results<CurrentUser>

    /**
     * Retrieves the public user information for a user matching the supplied id
     */
    suspend fun getUser(id: String): Results<User>

    /**
     * Retrieves the total recorded time for the currently authenticated user.
     *
     * In case the total recorded time has not been calculated, or is out of date, on
     * the server side this call will spawn a processing task which, depending on
     * how out of date the record is, can take a while to process.
     *
     * Repeated calls to this endpoint will fetch the calculation progress, and at the point
     * of it finishing the results will be cached for the duration of the set cache limit.
     */
    suspend fun getTotalRecord(): Results<TotalRecord>
}

internal class UserRepoImpl(
    private val limiter: RateLimiter<String>,
    private val remote: WakatimeRemoteDataSource,
    private val local: WakatimeLocalDataSource
) : UserRepo {

    companion object {
        private const val keyCurrentUser = "user_current"
        private const val keyTotalRecord = "user_current_totalRecord"
    }

    private val userLoader = SingleLoader<User>()
        .expired {
            // As remote fetch for specific users is not possible
            // due to access restrictions in the API, if the local
            // value is found it is considered to be valid.
            false
        }

    private val currentUserLoader = Loader<FullUser, CurrentUser>()
        .cache {
            local.getCurrentUser()
        }.expired {
            limiter.shouldFetch(keyCurrentUser)
        }.remote {
            remote.getCurrentUser()
        }.update { user ->
            local.storeCurrentUser(user.toCurrentUserView()).also { result ->
                if (result is Results.Success) {
                    limiter.mark(keyCurrentUser)
                }
            }
        }

    private val totalRecordLoader = SingleLoader<TotalRecord>()
        .cache {
            local.getTotalRecord()
        }.expired {
            limiter.shouldFetch(keyTotalRecord)
        }.remote {
            remote.getTotalRecord()
        }.update { record ->
            local.storeTotalRecord(record.toTotalRecordEntity()).also { result ->
                if (result is Results.Success && record.isUpToDate) {
                    // Total record processing has finished service side, and
                    // no more progress updates will be emitted for a while
                    limiter.mark(keyTotalRecord)
                }
            }
        }

    override suspend fun getCurrentUser(): Results<CurrentUser> = currentUserLoader.execute()

    override suspend fun getUser(id: String): Results<User> = userLoader
        .cache {
            local.getUser(id)
        }.execute()

    override suspend fun getTotalRecord(): Results<TotalRecord> = totalRecordLoader.execute()
}