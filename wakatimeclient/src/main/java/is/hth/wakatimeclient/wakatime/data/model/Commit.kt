package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = CommitSerializer::class)
data class Commit internal constructor(
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
     * branch name, for ex: master
     */
    val branch: String = "",
    /**
     * The author's description of this commit
     */
    val message: String = "",
    /**
     * link to an html page with details about current commit
     */
    @SerialName("html_url")
    val htmlUrl: String = "",
    /**
     * time commit was synced in ISO 8601 format
     */
    @SerialName("created_at")
    val createdAt: String = "",
    /**
     *  Authoring time of the commit in ISO 8601 format
     */
    @SerialName("author_date")
    val authoringDate: String = "",
    /**
     *  commit time in ISO 8601 format
     */
    @SerialName("committer_date")
    val committerDate: String = "",
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
     * Total time coded in editor for this commit in seconds
     */
    @SerialName("total_seconds")
    val totalSeconds: Float,
    /**
     * Api url with details about current commit
     */
    val url: String = "",
    /**
     *
     */
    val isAuthorFound: Boolean,
    /**
     * The author of this commit
     */
    val author: Entity,
    /**
     * The committer of this commit
     */
    val committer: Entity
)

@Serializable
data class Entity internal constructor(
    /**
     * The entity's name
     */
    val name: String = "",
    /**
     * The entity's username
     */
    val username: String = "",
    /**
     * The email address associated with this entity
     */
    val email: String = "",
    /**
     *  url of entity's avatar image
     */
    val avatarUrl: String = "",
    /**
     * link to entity's profile on GitHub, Bitbucket, GitLab, etc
     */
    val profileUrl: String = "",
    /**
     * Link to entity's api profile on Github, Bitbucket, Gitlab, etc
     */
    val apiUrl: String = ""
)

/**
 * Handles serialization of the [Commit] model, and creating author and committer
 * entities out of the data.
 */
internal object CommitSerializer : KSerializer<Commit> {

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("Commit")

    override fun serialize(encoder: Encoder, value: Commit) {
        throw NotImplementedError("Commit serialization not implemented yet!")
    }

    override fun deserialize(decoder: Decoder): Commit {
        require(decoder is JsonDecoder)
        val element = decoder.decodeJsonElement()
        if (element is JsonObject) {
            val id: String = getValue("id", "", element) { it.content }
            val ref: String = getValue("ref", "", element) { it.content }
            val hash: String = getValue("hash", "", element) { it.content }
            val truncatedHash: String = getValue("truncated_hash", "", element) { it.content }
            val branch: String = getValue("branch", "", element) { it.content }
            val message: String = getValue("message", "", element) { it.content }
            val htmlUrl: String = getValue("html_url", "", element) { it.content }
            val createdAt: String = getValue("created_at", "", element) { it.content }
            val authoringDate: String = getValue("author_date", "", element) { it.content }
            val committerDate: String = getValue("committer_date", "", element) { it.content }
            val humanTotal: String = getValue("human_readable_total", "", element) { it.content }
            val humanTotalSeconds: String = getValue("human_readable_with_seconds", "", element) { it.content }
            val totalSeconds: Float = getValue("total_seconds", -1F, element) { it.float }
            val url: String = getValue("url", "", element) { it.content }
            val authorFound: Boolean = getValue("is_author_found", false, element) { it.boolean }

            val authorAvatarUrl: String = getValue("author_avatar_url", "", element) { it.content }
            val authorEmail: String = getValue("author_email", "", element) { it.content }
            val authorProfileUrl: String = getValue("author_html_url", "", element) { it.content }
            val authorName: String = getValue("author_name", "", element) { it.content }
            val authorApiUrl: String = getValue("author_url", "", element) { it.content }
            val authorUsername: String = getValue("author_username", "", element) { it.content }

            val commitAvatarUrl: String = getValue("committer_avatar_url", "", element) { it.content }
            val commitEmail: String = getValue("committer_email", "", element) { it.content }
            val commitProfileUrl: String = getValue("committer_html_url", "", element) { it.content }
            val commitName: String = getValue("committer_name", "", element) { it.content }
            val commitApiUrl: String = getValue("committer_url", "", element) { it.content }
            val commitUsername: String = getValue("committer_username", "", element) { it.content }

            return Commit(
                id = id,
                ref = ref,
                hash = hash,
                truncatedHash = truncatedHash,
                branch = branch,
                message = message,
                htmlUrl = htmlUrl,
                createdAt = createdAt,
                authoringDate = authoringDate,
                committerDate = committerDate,
                humanReadableTotal = humanTotal,
                humanReadableWithSeconds = humanTotalSeconds,
                totalSeconds = totalSeconds,
                url = url,
                isAuthorFound = authorFound,
                author = Entity(
                    name = authorName,
                    username = authorUsername,
                    email = authorEmail,
                    avatarUrl = authorAvatarUrl,
                    profileUrl = authorProfileUrl,
                    apiUrl = authorApiUrl
                ),
                committer = Entity(
                    name = commitName,
                    username = commitUsername,
                    email = commitEmail,
                    avatarUrl = commitAvatarUrl,
                    profileUrl = commitProfileUrl,
                    apiUrl = commitApiUrl
                )
            )
        }
        throw SerializationException("JsonObject was expected!")
    }

    private fun <T> getValue(
        key: String,
        default: T,
        element: JsonObject,
        extract: (JsonPrimitive) -> T
    ): T {
        return if (key in element) {
            when (val value = element.getValue(key)) {
                is JsonNull -> default
                is JsonPrimitive -> extract(value)
                else -> default
            }
        } else default
    }
}

@Serializable
data class ProjectCommits internal constructor(
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

@Serializable
data class ProjectCommit internal constructor(
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
    val commit: Commit
)