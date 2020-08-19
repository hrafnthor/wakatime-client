package `is`.hth.wakatimeclient.wakatime.data.users

import `is`.hth.wakatimeclient.core.data.Loader
import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.util.RateLimiter
import `is`.hth.wakatimeclient.wakatime.model.CurrentUser
import `is`.hth.wakatimeclient.wakatime.model.TotalRecord
import `is`.hth.wakatimeclient.wakatime.model.User
import java.util.concurrent.TimeUnit

/**
 * Exposes data access functionality related to user information
 */
interface UserRepository {

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

class UserRepositoryImpl(
    cacheLimit: Int,
    private val remote: UserRemoteDataSource,
    private val local: UserLocalDataSource
) : UserRepository {

    companion object {
        private const val keyCurrentUser = "current"
        private const val keyTotalRecord = "totalRecord"
    }

    private val limiter: RateLimiter<String> = RateLimiter(cacheLimit, TimeUnit.MINUTES)
    private val userLoader: Loader<User> = Loader()
    private val currentUserLoader: Loader<CurrentUser> = Loader<CurrentUser>()
        .local {
            local.getCurrentUser()
        }.expired {
            limiter.shouldFetch(keyCurrentUser)
        }.remote {
            remote.getCurrentUser()
        }.update {
            local.insert(it)
            limiter.mark(keyCurrentUser)
        }
    private val totalRecordLoader: Loader<TotalRecord> = Loader<TotalRecord>()
        .local {
            local.getTotalRecord()
        }.expired {
            limiter.shouldFetch(keyTotalRecord)
        }.remote {
            remote.getTotalRecord()
        }.update {
            local.insert(it)
            if (it.isUpToDate) {
                // Total record processing has finished service side, and
                // no more progress updates will be emitted for a while
                limiter.mark(keyTotalRecord)
            }
        }

    override suspend fun getCurrentUser(): Results<CurrentUser> = currentUserLoader.execute()

    override suspend fun getUser(id: String): Results<User> = userLoader
        .local {
            local.getUser(id)
        }.expired {
            // As remote fetch for specific users is not possible
            // due to access restrictions in the API, if the local
            // value is found it is considered to be valid.
            false
        }.remote {
            // Remotely fetching users based on their id is
            // currently not allowed through the API
            Results.Empty()
        }.execute()

    override suspend fun getTotalRecord(): Results<TotalRecord> = totalRecordLoader.execute()
}









