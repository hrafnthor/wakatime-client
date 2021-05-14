package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Repository internal constructor(
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
public data class Badge internal constructor(
    /**
     * The unique id of this badge
     */
    val id: String = "",
    /**
     * The title shown inside of the badge
     */
    val title: String = "",
    /**
     * The url to the badge as a pre rendered image asset, ready to be displayed
     */
    @SerialName("url")
    val preRenderedBadgeUrl: String = "",
    /**
     * The hexadecimal color that the badge should show
     */
    val color: String = "",
    /**
     * The link that should be navigated to when clicking the badge
     */
    val link: String = "",
    /**
     * The hosting provider of the repository this badge is for
     */
    val provider: String = "",
    /**
     * The full repository name that this badge is for
     */
    val repository: String = "",
    /**
     * Creation date of the badge
     */
    @SerialName("created_at")
    val createdAt: String = "",
    /**
     *
     */
    val snippets: List<Snippet>
)

@Serializable
public data class Snippet internal constructor(
    /**
     *
     */
    val content: String,
    /**
     *
     */
    val name: String
)