package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Repository(
    /**
     * The unique id of the repository
     */
    val id: String = "",
    /**
     * The name of the repository
     */
    val name: String = "",
    /**
     * The username and repository name, ex: wakatime/wakadump
     */
    @SerialName("full_name")
    val fullName: String = "",
    /**
     * The remote provider of this repository, ex: Github
     */
    val provider: String = "",
    /**
     * Default branch if given for this repo
     */
    @SerialName("default_branch")
    val defaultBranch: String = "",
    /**
     * The remote repository description
     */
    val description: String = "",
    /**
     * The number of repository stars
     */
    @SerialName("star_count")
    val stars: Int,
    /**
     * The number of repository watchers
     */
    @SerialName("watch_count")
    val watchers: Int,
    /**
     * The number of repository forks
     */
    @SerialName("fork_count")
    val forks: Int,
    /**
     * The api url of the remote repository
     */
    val url: String = "",
    /**
     * The homepage of the repository
     */
    val homepage: String = "",
    /**
     * The html url for the repository
     */
    @SerialName("html_url")
    val htmlUrl: String = "",
    /**
     * Whether this repository is a fork or original
     */
    @SerialName("is_fork")
    val isFork: Boolean,
    /**
     * Whether this repository is private or public
     */
    @SerialName("is_private")
    val isPrivate: Boolean,
    /**
     * The last time that this repository was synced with remote provider in ISO 8601 format
     */
    @SerialName("last_synced_at")
    val lastSyncedAt: String = "",
    /**
     * The Wakatime badge configured for this repository
     */
    val badge: Badge
)

@Serializable
data class Badge(
    /**
     *
     */
    val id: String = "",
    /**
     *
     */
    val title: String = "",
    /**
     *
     */
    val url: String = "",
    /**
     *
     */
    val color: String = "",
    /**
     *
     */
    val link: String = "",
    /**
     *
     */
    val provider: String = "",
    /**
     *
     */
    val repository: String = "",
    /**
     *
     */
    @SerialName("created_at")
    val createdAt: String = "",
    /**
     *
     */
    val snippets: List<Snippet>
)

@Serializable
data class Snippet(
    /**
     *
     */
    val content: String,
    /**
     *
     */
    val name: String
)