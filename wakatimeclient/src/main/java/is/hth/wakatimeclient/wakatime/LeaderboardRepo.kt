package `is`.hth.wakatimeclient.wakatime

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.SingleLoader
import `is`.hth.wakatimeclient.core.util.RateLimiter
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeRemoteDataSource
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeLocalDataSource
import `is`.hth.wakatimeclient.wakatime.data.db.entities.*
import `is`.hth.wakatimeclient.wakatime.model.Language
import `is`.hth.wakatimeclient.wakatime.model.Leaderboard
import `is`.hth.wakatimeclient.wakatime.model.Leaders
import `is`.hth.wakatimeclient.wakatime.model.Rank

interface LeaderboardRepo {

    /**
     * Fetches the requested [page] of the public leaderboard for the supplied [language].
     *
     * The language can be skipped which will give the all round public result ordered by
     * total coding activity and daily averages irregardless of language.
     *
     * The page that will be given will be the one containing the current user's ranking
     * if the authentication scope given to the client allows for that.
     *
     * If the required authentication scope access has not been given, the result will be for
     * page 1.
     */
    suspend fun getPublicLeaders(language: String, page: Int): Results<Leaders>

    /**
     * Fetches the private leaderboards for the currently authenticated user
     */
    suspend fun getLeaderboards(): Results<List<Leaderboard>>
}

internal class LeaderboardRepoImpl(
        private val limiter: RateLimiter<String>,
        private val remote: WakatimeRemoteDataSource,
        private val local: WakatimeLocalDataSource,
) : LeaderboardRepo {

    companion object {
        private const val keyLeaderboards = "leaderboards"
    }

    private val leaderboardLoader = SingleLoader<List<Leaderboard>>()
            .cache {
                local.getLeaderboards(true)
            }.expired {
                limiter.shouldFetch(keyLeaderboards)
            }.remote {
                remote.getLeaderboards()
            }.update {
                local.storeLeaderboards(it).also { result ->
                    if (result is Results.Success) {
                        limiter.mark(keyLeaderboards)
                    }
                }
            }

    override suspend fun getPublicLeaders(language: String, page: Int): Results<Leaders> {
        return remote.getPublicLeaders(language, page).also {
            if (it is Results.Success.Values) {
                updateLeaders(LeaderboardEntity.publicLeaderboard.id, it.data)
            }
        }
    }

    override suspend fun getLeaderboards(): Results<List<Leaderboard>> = leaderboardLoader.execute()

    /**
     * Stores the leadership positions for the given leaderboard for
     * the current period that they are valid for.
     * The purpose is to store certain data points for potential historical representation,
     * not to capture all the data.
     */
    private suspend fun updateLeaders(
            leaderboardId: String,
            leaders: Leaders
    ): Results<Unit> {
        // Get the primary language id for this leaderboard, if any
        val languageId: Long = if (leaders.language.isNotEmpty()) {
            when (val results: Results<Language> = local.getLanguage(leaders.language)) {
                is Results.Success.Values -> results.data.id
                is Results.Failure -> return results
                Results.Success.Empty -> return Results.Failure(
                        Error.Database.Empty("Unable to find id for language ${leaders.language}")
                )
            }
        } else LanguageEntity.none.id

        // Store the period for which these records are valid for
        val periodId: Long = when (val results: Results<Long> = local.storePeriod(
                startDate = leaders.period.startDate,
                endDate = leaders.period.endDate
        )) {
            is Results.Success.Values -> results.data
            is Results.Failure -> return results
            Results.Success.Empty -> return Results.Failure(
                    Error.Database.Insert("Unable to insert or select id for period")
            )
        }

        // Store all users from the leadership set
        val ranks: List<Rank> = extractRanks(leaders)
        val users: Set<UserEntity> = extractUsers(ranks)
        val userResults: Results<Unit> = local.storeUsers(*users.toTypedArray())
        if (userResults is Results.Failure) {
            return userResults
        }

        // Store each user's leadership rank
        ranks.forEach { ranking ->
            val rank = UserRankEntity(
                    userId = ranking.user.id,
                    languageId = languageId,
                    leaderboardId = leaderboardId,
                    rank = ranking.rank,
                    periodId = periodId,
                    modifiedAt = leaders.modifiedAt
            )
            when (val results: Results<Long> = local.storeRank(rank)) {
                is Results.Failure -> return results
                Results.Success.Empty -> return Results.Failure(
                        Error.Database.Insert("Unable to insert or select id for user rank")
                )
            }
        }
        return Results.Success.Empty
    }

    private fun extractRanks(leaders: Leaders): List<Rank> {
        return if (leaders.currentUserRank != null) {
            leaders.ranks.toMutableList().also {
                it.add(leaders.currentUserRank)
            }
        } else leaders.ranks
    }

    private fun extractUsers(ranks: List<Rank>): Set<UserEntity> = ranks
            .map {
                it.user.toEntity()
            }.toHashSet()
}