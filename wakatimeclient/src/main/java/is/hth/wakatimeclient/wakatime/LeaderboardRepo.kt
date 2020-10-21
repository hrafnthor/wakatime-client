package `is`.hth.wakatimeclient.wakatime

import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.SingleLoader
import `is`.hth.wakatimeclient.core.takeIfNotEmpty
import `is`.hth.wakatimeclient.core.util.RateLimiter
import `is`.hth.wakatimeclient.core.util.unwrap
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeRemoteDataSource
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeLocalDataSource
import `is`.hth.wakatimeclient.wakatime.data.db.entities.*
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
    suspend fun getPublicLeaders(
        language: String,
        page: Int
    ): Results<Leaders>

    /**
     * Fetches the requested [page] of the private leaderboard matching the supplied
     * [leaderboardIdentifier] for an optional filtering of a [language].
     */
    suspend fun getPrivateLeaders(
        leaderboardIdentifier: String,
        language: String,
        page: Int
    ): Results<Leaders>

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

    override suspend fun getPublicLeaders(
        language: String,
        page: Int
    ): Results<Leaders> = processLeaders(
        LeaderboardEntity.publicLeaderboard.identifier,
        remote.getPublicLeaders(language, page)
    )

    override suspend fun getPrivateLeaders(
        leaderboardIdentifier: String,
        language: String,
        page: Int
    ): Results<Leaders> = processLeaders(
        leaderboardIdentifier,
        remote.getPrivateLeaders(leaderboardIdentifier, language, page)
    )

    override suspend fun getLeaderboards(): Results<List<Leaderboard>> = leaderboardLoader.execute()

    /**
     * Processes the received network results, updating the locally stored leaderboard information
     * as well as repackaging the results in case any errors arise.
     */
    private suspend fun processLeaders(
        leaderboardIdentifier: String,
        results: Results<Leaders>
    ): Results<Leaders> {
        return results.also {
            if (it is Results.Success.Values) {
                updateLeaders(leaderboardIdentifier, it.data)
            }
        }
    }

    /**
     * Stores the leadership positions for the given leaderboard for
     * the current period that they are valid for.
     * The purpose is to store certain data points for potential historical representation,
     * not to capture all the data.
     */
    private suspend fun updateLeaders(
        leaderboardIdentifier: String,
        leaders: Leaders
    ): Results<Leaders> {
        return unwrap(local.getLeaderboard(leaderboardIdentifier)) { leaderboard ->
            val ranks: List<Rank> = extractRanks(leaders)
            val languages: Set<String> = extractLanguages(ranks)
            unwrap(local.storeLanguages(languages)) {
                val name: String = leaders.language.takeIfNotEmpty() ?: LanguageEntity.none.name
                unwrap(local.getLanguage(name)) { language ->
                    unwrap(local.storePeriod(leaders.period)) { periodId ->
                        unwrap(local.storeUsers(extractUsers(ranks))) {
                            val rankings: List<UserRankEntity> = ranks.map { ranking ->
                                UserRankEntity(
                                    userId = ranking.user.id,
                                    languageId = language.id,
                                    leaderboardId = leaderboard.id,
                                    rank = ranking.rank,
                                    periodId = periodId,
                                    modifiedAt = leaders.modifiedAt
                                )
                            }
                            unwrap(local.storeRanks(rankings)) {
                                Results.Success.Values(leaders)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun extractRanks(leaders: Leaders): List<Rank> {
        return if (leaders.currentUserRank != null) {
            leaders.ranks.toMutableList().also {
                it.add(leaders.currentUserRank)
            }
        } else leaders.ranks
    }

    private fun extractUsers(ranks: List<Rank>): List<UserEntity> {
        return ranks
            .mapTo(mutableListOf()) {
                it.user.toEntity()
            }
    }

    private fun extractLanguages(ranks: List<Rank>): Set<String> {
        return ranks
            .flatMap { it.runningTotal.languages }
            .map { it.name }
            .toSet()
    }
}