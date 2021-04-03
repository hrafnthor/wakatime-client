package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Commit(
    /**
     * Unique id of the commit
     */
    val id: String = "",
    /**
     * refs/heads/master
     */
    val ref: String = "",
    /**
     * revision control hash of this commit
     */
    val hash: String = "",
    /**
     * truncated revision control hash of this commit
     */
    @SerialName("truncated_hash")
    val truncatedHash: String = "",
    /**
     *  url of author's avatar image
     */
    @SerialName("author_avatar_url")
    val authorAvatarUrl: String = "",
    /**
     *  time when commit was authored in ISO 8601 format
     */
    @SerialName("author_date")
    val authorDate: String = "",
    /**
     * The email address of the author
     */
    @SerialName("author_email")
    val authorEmail: String = "",
    /**
     * link to author's profile on GitHub, Bitbucket, GitLab, etc
     */
    @SerialName("author_html_url")
    val authorHtmlUrl: String = "",
    /**
     * The authors name
     */
    @SerialName("author_name")
    val authorName: String = "",
    /**
     *  api url for author's profile
     */
    @SerialName("author_url")
    val authorUrl: String = "",
    /**
     * The author's username
     */
    @SerialName("author_username")
    val authorUsername: String = "",
    /**
     * branch name, for ex: master
     */
    val branch: String = "",
    /**
     * url of committer's avatar image
     */
    @SerialName("committer_avatar_url")
    val committerAvatarUrl: String = "",
    /**
     *  commit time in ISO 8601 format
     */
    @SerialName("committer_date")
    val committerDate: String = "",
    /**
     *  email address of committer
     */
    @SerialName("committer_email")
    val committerEmail: String = "",
    /**
     * link to the committer's profile on GitHub, Bitbucket, GitLab, etc
     */
    @SerialName("committer_html_url")
    val committerHtmlUrl: String = "",
    /**
     *
     */
    @SerialName("committer_name")
    val committerName: String = "",
    /**
     * api url for committer's profile
     */
    @SerialName("committer_url")
    val committerUrl: String = "",
    /**
     * Committers username
     */
    @SerialName("committer_username")
    val committerUserName: String = "",
    /**
     * time commit was synced in ISO 8601 format
     */
    @SerialName("created_at")
    val createdAt: String = "",
    /**
     * link to an html page with details about current commit
     */
    @SerialName("html_url")
    val htmlUrl: String = "",
    /**
     * Time coded in editor for this commit
     */
    @SerialName("human_readable_total")
    val humanReadableTotal: String = "",
    /**
     * The time coded in editor for this commit with seconds
     */
    @SerialName("human_readable_with_seconds")
    val humanReadableWithSeconds: String = "",
    /**
     * The author's description of this commit
     */
    val message: String = "",
    /**
     * Total time coded in editor for this commit in seconds
     */
    @SerialName("total_seconds")
    val totalSeconds: Float,
    /**
     * Api url with details about current commit
     */
    val url: String = "",
)

@Serializable
data class Commits(
    /**
     * current author or null if showing commits from all authors
     */
    val author: String = "",
    /**
     * The branch name containing the commits
     */
    val branch: String = "",
    /**
     * The project's sync status
     */
    val status: String = "",
    /**
     *
     */
    val project: Project,
    /**
     *
     */
    val commits: List<Commit>
)