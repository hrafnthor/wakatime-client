package `is`.hth.wakatimeclient.wakatime.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Leaders(
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
    @SerialName("range")
    val period: Period,
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
)

@Serializable
data class Rank(
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
data class RunningTotal(
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
data class LanguageTotal(
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

@Serializable
data class Period(
    /**
     * Start of this range as ISO 8601 UTC datetime>
     */
    @SerialName("start_date")
    val startDate: String = "",
    /**
     * Start of range in human-readable format relative to the current day
     */
    @SerialName("start_text")
    val startText: String = "",
    /**
     * End of range as ISO 8601 UTC datetime
     */
    @SerialName("end_date")
    val endDate: String = "",
    /**
     * End of range in human-readable format relative to the current day
     */
    @SerialName("end_text")
    val endText: String = "",
    /**
     * Time range of this leaderboard
     */
    val name: String = "",
    /**
     * Time range in human-readable format relative to the current day
     */
    val text: String = ""
)