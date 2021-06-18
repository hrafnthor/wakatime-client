package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
public data class Leaders internal constructor(
    /**
     *  current page number
     */
    val page: Int,
    /**
     * number of pages available
     */
    @SerialName("total_pages")
    val totalPages: Int,
    /**
     * timeout setting in minutes used by this leaderboard
     */
    val timeout: Int,
    /**
     * writes_only setting used by this leaderboard>
     */
    @SerialName("writes_only")
    val writesOnly: Boolean,
    /**
     * language of this leaderboard
     */
    val language: String = "",
    /**
     * time when this leaderboard was last updated in ISO 8601 format
     */
    @SerialName("modified_at")
    val modifiedAt: String = "",
    /**
     * The period over which these results cover
     */
    val range: Range,
    /**
     * The current user's rank if access has been given to that data.
     */
    @SerialName("current_user")
    val currentUserRank: Rank?,
    /**
     * The user ranks for this leaderboard
     */
    @SerialName("data")
    val ranks: List<Rank>,
) {
    /**
     * A request object for [Leaders] defining querying values
     */
    public class Request(
        public val leaderboardId: String,
        public val language: String?,
        public val page: Int?
    ) {

        public class Builder(
            public val leaderboardId: String,
            public var language: String? = null,
            public var page: Int? = null,
        ) {

            /**
             * Constructs a new [Request] from set values
             */
            public fun build(): Request = Request(leaderboardId, language, page)
        }
    }

    public companion object {

        /**
         * Returns a [Request.Builder] configured for public leaderboard querying
         */
        public inline fun publicLeaderboardRequest(
            construct: Request.Builder.() -> Unit
        ): Request = Request.Builder("").also(construct).build()

        /**
         * Returns a [Request.Builder] configured for a private leaderboard querying
         * @param leaderboardId The unique id of the leaderboard that should be queried
         */
        public fun privateLeaderboardRequest(leaderboardId: String): Request.Builder {
            return Request.Builder(leaderboardId)
        }
    }
}

@Serializable
public data class Rank(
    /**
     * Rank of this leader
     */
    val rank: Int,
    /**
     * The running total for this leader
     */
    @SerialName("running_total")
    val runningTotal: RunningTotal,
    /**
     * The user matching this leader
     */
    val user: User,
)

@Serializable
public data class RunningTotal(
    /**
     * total coding activity for this user as seconds
     */
    @SerialName("total_seconds")
    val totalSeconds: Float,
    /**
     * daily average for this user as seconds
     */
    @SerialName("daily_average")
    val dailyAverage: Float,
    /**
     * total coding activity for this user as human readable string
     */
    @SerialName("human_readable_total")
    val totalSecondsReadable: String = "",
    /**
     * daily average for this user as human readable string
     */
    @SerialName("human_readable_daily_average")
    val dailyAverageReadable: String = "",
    /**
     * List of languages and their respected logged time
     */
    val languages: List<LanguageTotal>
)

@Serializable
public data class LanguageTotal(
    /**
     * The language name
     */
    val name: String = "",
    /**
     * Total seconds that the user has logged in with this language
     */
    @SerialName("total_seconds")
    val totalSeconds: Float
)