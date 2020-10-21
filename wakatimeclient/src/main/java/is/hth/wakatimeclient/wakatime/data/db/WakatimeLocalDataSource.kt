package `is`.hth.wakatimeclient.wakatime.data.db

import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.db.DbErrorProcessor
import `is`.hth.wakatimeclient.core.data.db.LocalDataSource
import `is`.hth.wakatimeclient.wakatime.data.db.entities.*
import `is`.hth.wakatimeclient.wakatime.model.*

// TODO: 5.10.2020 Convert all entity inputs to models, as models are what is being returned
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

    /**
     * Attempts to retrieve the [Leaderboard]s that the user is associated with.
     */
    suspend fun getLeaderboards(onlyPrivate: Boolean): Results<List<Leaderboard>>

    suspend fun getLeaderboard(identifier: String): Results<Leaderboard>

    suspend fun storeLanguage(name: String): Results<Long>

    suspend fun storeLanguages(names: Set<String>): Results<Int>

    suspend fun storeUsers(users: List<UserEntity>): Results<Int>

    /**
     * Stores the supplied [UserRankEntity]s to the local database.
     * Returns the number of rows affected.
     */
    suspend fun storeRanks(ranks: List<UserRankEntity>): Results<Int>

    suspend fun storeLeaderboards(boards: List<Leaderboard>): Results<Int>

    suspend fun storePeriod(period: Period): Results<Long>

    suspend fun storeCurrentUser(currentUser: CurrentUserView): Results<Int>

    suspend fun storeTotalRecord(totalRecord: TotalRecordEntity): Results<Long>

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

    override suspend fun getLeaderboards(onlyPrivate: Boolean): Results<List<Leaderboard>> =
        operate {
            if (onlyPrivate) {
                db.rankings().getPrivateLeaderboards()
            } else {
                db.rankings().getLeaderboards()
            }.map {
                it.toModel()
            }
        }

    override suspend fun getLeaderboard(identifier: String): Results<Leaderboard> = operate {
        db.rankings().getLeaderboard(identifier)?.toModel()
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
    ): Results<Int> = operate {
        val languages: List<LanguageEntity> = names.map { LanguageEntity(name = it) }
        db.languages().insertIgnoreLanguages(*languages.toTypedArray()).size
    }

    override suspend fun storeUsers(
        users: List<UserEntity>
    ): Results<Int> = operate {
        db.users().insertOrUpdateUsers(*users.toTypedArray())
    }

    override suspend fun storeRanks(
        ranks: List<UserRankEntity>
    ): Results<Int> = operate {
        db.rankings().insertOrUpdateRanks(ranks)
    }

    override suspend fun storeLeaderboards(
        boards: List<Leaderboard>
    ): Results<Int> = operate {
        db.rankings().insertOrUpdateLeaderboards(boards.map(Leaderboard::toEntity))
    }

    override suspend fun storePeriod(
        period: Period
    ): Results<Long> = operate {
        db.calendar().setPeriod(period.startDate, period.endDate)
    }

    override suspend fun storeCurrentUser(
        currentUser: CurrentUserView
    ): Results<Int> = operate {
        db.users().insertOrUpdateUsers(currentUser.user).also {
            db.users().insertReplace(currentUser.config)
        }
    }

    override suspend fun storeTotalRecord(
        totalRecord: TotalRecordEntity
    ): Results<Long> = operate {
        db.users().insertReplace(totalRecord)
    }

    override suspend fun removeUser(
        id: String
    ): Results<Boolean> = operate {
        db.users().removeUser(id) > 0
    }
}