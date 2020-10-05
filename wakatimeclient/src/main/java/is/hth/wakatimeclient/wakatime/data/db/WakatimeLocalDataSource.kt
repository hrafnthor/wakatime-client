package `is`.hth.wakatimeclient.wakatime.data.db

import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.db.DbErrorProcessor
import `is`.hth.wakatimeclient.core.data.db.LocalDataSource
import `is`.hth.wakatimeclient.wakatime.data.db.entities.*
import `is`.hth.wakatimeclient.wakatime.data.db.entities.toCurrentUser
import `is`.hth.wakatimeclient.wakatime.model.CurrentUser
import `is`.hth.wakatimeclient.wakatime.model.Language
import `is`.hth.wakatimeclient.wakatime.model.TotalRecord
import `is`.hth.wakatimeclient.wakatime.model.User


internal interface WakatimeLocalDataSource {

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

    suspend fun getLanguage(name: String): Results<Language>

    suspend fun getLanguages(): Results<Set<Language>>

    suspend fun storeLanguage(name: String): Results<Long>

    suspend fun storeLanguages(names: Set<String>): Results<Unit>

    suspend fun storeUsers(vararg entities: UserEntity): Results<Unit>

    suspend fun storeRank(rank: UserRankEntity): Results<Long>

    suspend fun storeLeaderboards(vararg entities: LeaderboardEntity): Results<Unit>

    suspend fun storePeriod(startDate: String, endDate: String): Results<Long>

    suspend fun storeCurrentUser(currentUser: CurrentUserView): Results<Unit>

    suspend fun storeTotalRecord(totalRecord: TotalRecordEntity): Results<Unit>

    suspend fun removeUser(id: String): Results<Boolean>
}

internal class WakatimeLocalDataSourceImpl(
    private val db: WakatimeDatabase,
    processor: DbErrorProcessor
) : LocalDataSource(processor), WakatimeLocalDataSource {

    override suspend fun getCurrentUser(): Results<CurrentUser> = operate {
        db.users().getCurrentUser()?.toCurrentUser()
    }

    override suspend fun getUser(id: String): Results<User> = operate {
        db.users().getUser(id)?.toUser()
    }

    override suspend fun getTotalRecord(): Results<TotalRecord> = operate {
        db.users().getTotalRecord()?.toTotalRecord()
    }

    override suspend fun getLanguage(name: String): Results<Language> = operate {
        db.languages().getLanguage(name)?.toModel()
    }

    override suspend fun getLanguages(): Results<Set<Language>> = operate {
        db.languages().getLanguages().mapTo(mutableSetOf()) {
            it.toModel()
        }
    }

    override suspend fun storeLanguage(
        name: String
    ): Results<Long> = operate {
        db.languages().setLanguage(name).also { id ->
            if (id == -1L) throw IllegalStateException(
                "Unable to insert language $name into database!"
            )
        }
    }

    override suspend fun storeLanguages(
        names: Set<String>
    ): Results<Unit> = operate {
        val languages: List<LanguageEntity> = names.map { LanguageEntity(name = it) }
        db.languages().insertIgnoreLanguages(*languages.toTypedArray())
    }

    override suspend fun storeUsers(
        vararg entities: UserEntity
    ): Results<Unit> = operate {
        db.users().insertReplaceUsers(*entities)
    }

    override suspend fun storeRank(
        rank: UserRankEntity
    ): Results<Long> = operate {
        db.rankings().setUserRank(rank).also { id ->
            if (id == -1L) throw IllegalStateException("Unable to insert user rank: $rank")
        }
    }

    override suspend fun storeLeaderboards(
            vararg entities: LeaderboardEntity
    ): Results<Unit> = operate {
        db.rankings().insertReplaceLeaderboards(*entities)
    }

    override suspend fun storePeriod(
        startDate: String,
        endDate: String
    ): Results<Long> = operate {
        db.calendar().setPeriod(startDate, endDate)
    }

    override suspend fun storeCurrentUser(
        currentUser: CurrentUserView
    ): Results<Unit> = operate {
        db.users().insertReplaceUsers(currentUser.user)
        db.users().insertReplace(currentUser.config)
    }

    override suspend fun storeTotalRecord(
        totalRecord: TotalRecordEntity
    ): Results<Unit> = operate {
        db.users().insertReplace(totalRecord)
    }

    override suspend fun removeUser(
        id: String
    ): Results<Boolean> = operate {
        db.users().removeUser(id) > 0
    }
}