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

    /**
     *
     */
    suspend fun getLanguage(name: String): Results<Language>

    /**
     *
     */
    suspend fun getLanguages(): Results<Set<Language>>

    /**
     * Retrieves all locally stored [Leaderboard]s that the user is associated with.
     */
    suspend fun getLeaderboards(onlyPrivate: Boolean): Results<List<Leaderboard>>

    /**
     * Attempts to retrieve the [Leaderboard] matching the supplied identifier
     */
    suspend fun getLeaderboard(identifier: String): Results<Leaderboard>

    /**
     * Retrieves all locally stored [Project]s
     */
    suspend fun getProjects(): Results<List<Project>>

    /**
     * Attempts to retrieve the [Project] that matches the supplied id
     */
    suspend fun getProject(id: String): Results<Project>

    /**
     * Retrieves all of the locally stored user [Agent]s
     */
    suspend fun getAgents(): Results<List<Agent>>

    /**
     *
     */
    suspend fun storeLanguage(name: String): Results<Long>

    /**
     *
     */
    suspend fun storeLanguages(names: Set<String>): Results<Int>

    /**
     *
     */
    suspend fun storeUsers(users: List<User>): Results<Int>

    /**
     * Stores the supplied [UserRankEntity]s to the local database.
     * Returns the number of rows affected.
     */
    suspend fun storeRanks(ranks: List<UserRankEntity>): Results<Int>

    /**
     *
     */
    suspend fun storeLeaderboards(boards: List<Leaderboard>): Results<Int>

    /**
     *
     */
    suspend fun storePeriod(period: Period): Results<Long>

    /**
     *
     */
    suspend fun storeCurrentUser(currentUser: CurrentUserView): Results<Int>

    /**
     *
     */
    suspend fun storeTotalRecord(totalRecord: TotalRecordEntity): Results<Long>

    /**
     * Stores the supplied [Project]s to the local storage
     */
    suspend fun storeProjects(projects: List<Project>): Results<Int>

    /**
     * Stores the supplied [Agent]s to the local storage
     */
    suspend fun storeAgents(agents: List<Agent>): Results<Int>

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

    override suspend fun getLeaderboards(
        onlyPrivate: Boolean
    ): Results<List<Leaderboard>> = operate {
        if (onlyPrivate) {
            db.rankings().getPrivateLeaderboards()
        } else {
            db.rankings().getLeaderboards()
        }.map {
            it.toModel()
        }
    }

    override suspend fun getLeaderboard(
        identifier: String
    ): Results<Leaderboard> = operate {
        db.rankings().getLeaderboard(identifier)?.toModel()
    }

    override suspend fun getProjects(): Results<List<Project>> = operate {
        db.projects().getProjects().map {
            it.toModel()
        }
    }

    override suspend fun getProject(
        id: String
    ): Results<Project> = operate {
        db.projects().getProject(id)?.toModel()
    }

    override suspend fun getAgents(): Results<List<Agent>> = operate {
        db.users().getAgents().map {
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
    ): Results<Int> = operate {
        val languages: List<LanguageEntity> = names.map { LanguageEntity(name = it) }
        db.languages().insertIgnoreLanguages(*languages.toTypedArray()).size
    }

    override suspend fun storeUsers(
        users: List<User>
    ): Results<Int> = operate {
        db.users().insertOrUpdateUsers(*users.map {
            it.toEntity()
        }.toTypedArray())
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

    override suspend fun storeProjects(
        projects: List<Project>
    ): Results<Int> = operate {
        db.projects().insertOrUpdate(*projects.map {
            it.toEntity()
        }.toTypedArray())
    }

    override suspend fun storeAgents(
        agents: List<Agent>
    ): Results<Int> = operate {
        db.users().insertOrUpdateAgents(*agents.map {
            it.toEntity()
        }.toTypedArray())
    }

    override suspend fun removeUser(
        id: String
    ): Results<Boolean> = operate {
        db.users().removeUser(id) > 0
    }
}