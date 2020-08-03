package `is`.hth.wakatimeclient.wakatime.data.users

import `is`.hth.wakatimeclient.core.data.ErrorFactory
import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.util.safeOperation
import `is`.hth.wakatimeclient.core.util.valuesOrEmpty
import `is`.hth.wakatimeclient.wakatime.data.db.dao.UserDao
import `is`.hth.wakatimeclient.wakatime.data.db.entities.toConfigEntity
import `is`.hth.wakatimeclient.wakatime.data.db.entities.toCurrentUser
import `is`.hth.wakatimeclient.wakatime.data.db.entities.toEntity
import `is`.hth.wakatimeclient.wakatime.data.db.entities.toUser
import `is`.hth.wakatimeclient.wakatime.model.CurrentUser
import `is`.hth.wakatimeclient.wakatime.model.User


interface UserLocalDataSource {

    /**
     * Attempts to retrieve the [CurrentUser] information from local storage
     */
    suspend fun getCurrentUser(): Results<CurrentUser>

    /**
     * Attempts to retrieve the [User] information from local storage
     */
    suspend fun getUser(id: String): Results<User>

    suspend fun insert(user: User)

    suspend fun insert(currentUser: CurrentUser)
}

internal class UserLocalDataSourceImp(
    private val dao: UserDao,
    private val errors: ErrorFactory
) : UserLocalDataSource {

    override suspend fun getCurrentUser(): Results<CurrentUser> = safeOperation(
        operation = {
            valuesOrEmpty {
                dao.getCurrentUser()?.toCurrentUser()
            }
        },
        error = {
            errors.onThrowable(it)
        }
    )

    override suspend fun getUser(id: String): Results<User> = safeOperation(
        operation = {
            valuesOrEmpty {
                dao.getUser(id)?.toUser()
            }
        },
        error = {
            errors.onThrowable(it)
        }
    )

    override suspend fun insert(user: User): Unit = dao.insertReplace(user.toEntity())

    override suspend fun insert(currentUser: CurrentUser) {
        dao.insertReplace(currentUser.user.toEntity())
        dao.insertReplace(currentUser.toConfigEntity())
    }
}
