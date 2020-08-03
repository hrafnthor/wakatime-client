package `is`.hth.wakatimeclient.wakatime.data.users

import `is`.hth.wakatimeclient.core.data.Loader
import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.util.RateLimiter
import `is`.hth.wakatimeclient.wakatime.model.CurrentUser
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
}

class UserRepositoryImpl(
    cacheLimit: Int,
    private val remote: UserRemoteDataSource,
    private val local: UserLocalDataSource
) : UserRepository {

    private val keyCurrentUser = "current"

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
}









