package `is`.hth.wakatimeclient.wakatime.data.users

import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.db.DbErrorProcessor
import `is`.hth.wakatimeclient.core.data.db.LocalDataSource
import `is`.hth.wakatimeclient.wakatime.data.db.dao.UserDao
import `is`.hth.wakatimeclient.wakatime.data.db.entities.*
import `is`.hth.wakatimeclient.wakatime.model.CurrentUser
import `is`.hth.wakatimeclient.wakatime.model.TotalRecord
import `is`.hth.wakatimeclient.wakatime.model.User

/**
 * Exposes local data operations related to user specific values
 */
interface UserLocalDataSource {

    /**
     * Attempts to retrieve the [CurrentUser] information from local storage
     */
    suspend fun getCurrentUser(): Results<CurrentUser>

    /**
     * Attempts to retrieve the [User] information from local storage
     */
    suspend fun getUser(id: String): Results<User>

    /**
     * Attempts to retrieve the [TotalRecord] information for the current user
     * from the local storage
     */
    suspend fun getTotalRecord(): Results<TotalRecord>

    suspend fun insert(user: User)

    suspend fun insert(currentUser: CurrentUser)

    suspend fun insert(totalRecord: TotalRecord)
}

internal class UserLocalDataSourceImp(
    private val dao: UserDao,
    processor: DbErrorProcessor
) : LocalDataSource(processor), UserLocalDataSource {

    override suspend fun getCurrentUser(): Results<CurrentUser> = load {
        dao.getCurrentUser()?.toCurrentUser()
    }

    override suspend fun getUser(id: String): Results<User> = load {
        dao.getUser(id)?.toUser()
    }

    override suspend fun getTotalRecord(): Results<TotalRecord> = load {
        dao.getTotalRecord()?.toTotalRecord()
    }

    override suspend fun insert(user: User): Unit = dao.insertReplace(user.toEntity())

    override suspend fun insert(currentUser: CurrentUser) {
        dao.insertReplace(currentUser.user.toEntity())
        dao.insertReplace(currentUser.toConfigEntity())
    }

    override suspend fun insert(totalRecord: TotalRecord) {
        dao.insertReplace(totalRecord.toTotalRecordEntity())
    }
}
