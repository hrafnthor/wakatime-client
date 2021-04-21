package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.*
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.*

@Serializable(with = CommitTransformingSerializer::class)
data class Commit internal constructor(
    /**
     * Unique id of the commit
     */
    @SerialName(ID)
    val id: String = "",
    /**
     * The id of the author of the commit
     */
    @SerialName(AUTHOR_ID)
    val authorId: String = "",
    /**
     * refs/heads/master
     */
    @SerialName(REF)
    val ref: String = "",
    /**
     * revision control hash of this commit
     */
    @SerialName(HASH)
    val hash: String = "",
    /**
     * truncated revision control hash of this commit
     */
    @SerialName(TRUNCATED_HASH)
    val truncatedHash: String = "",
    /**
     * branch name, for ex: master
     */
    @SerialName(BRANCH)
    val branch: String = "",
    /**
     * The author's description of this commit
     */
    @SerialName(MESSAGE)
    val message: String = "",
    /**
     * link to an html page with details about current commit
     */
    @SerialName(HTML_URL)
    val htmlUrl: String = "",
    /**
     * time commit was synced in ISO 8601 format
     */
    @SerialName(CREATED_AT)
    val createdAt: String = "",
    /**
     *  Authoring time of the commit in ISO 8601 format
     */
    @SerialName(AUTHOR_DATE)
    val authoringDate: String = "",
    /**
     *  commit time in ISO 8601 format
     */
    @SerialName(COMMITTER_DATE)
    val commitDate: String = "",
    /**
     * Time coded in editor for this commit
     */
    @SerialName(HUMAN_READABLE_TOTAL)
    val humanReadableTotal: String = "",
    /**
     * The time coded in editor for this commit with seconds
     */
    @SerialName(HUMAN_READABLE_TOTAL_SECONDS)
    val humanReadableTotalWithSeconds: String = "",
    /**
     * Total time coded in editor for this commit in seconds
     */
    @SerialName(TOTAL_SECONDS)
    val totalSeconds: Float,
    /**
     * Api url with details about current commit
     */
    @SerialName(URL)
    val url: String = "",
    /**
     *
     */
    @SerialName(IS_AUTHOR_FOUND)
    val isAuthorFound: Boolean,
    /**
     * The author of this commit
     */
    @Serializable
    @SerialName(AUTHOR)
    val author: Entity,
    /**
     * The committer of this commit
     */
    @Serializable
    @SerialName(COMMITTER)
    val committer: Entity
) {
    internal companion object {
        const val ID = "id"
        const val AUTHOR_ID = "author_id"
        const val REF = "ref"
        const val HASH = "hash"
        const val TRUNCATED_HASH = "truncated_hash"
        const val BRANCH = "branch"
        const val MESSAGE = "message"
        const val HTML_URL = "html_url"
        const val CREATED_AT = "created_at"
        const val AUTHOR_DATE = "author_date"
        const val COMMITTER_DATE = "committer_date"
        const val HUMAN_READABLE_TOTAL = "human_readable_total"
        const val HUMAN_READABLE_TOTAL_SECONDS = "human_readable_total_with_seconds"
        const val TOTAL_SECONDS = "total_seconds"
        const val URL = "url"
        const val IS_AUTHOR_FOUND = "is_author_found"
        const val AUTHOR = "author"
        const val COMMITTER = "committer"
    }
}

internal object CommitTransformingSerializer : JsonTransformingSerializer<Commit>(CommitSerializer) {
    private val authorMap: Map<String, String> = mapOf(
        Pair("author_avatar_url", Entity.AVATAR_URL),
        Pair("author_email", Entity.EMAIL),
        Pair("author_html_url", Entity.PROFILE_URL),
        Pair("author_name", Entity.NAME),
        Pair("author_url", Entity.API_URL),
        Pair("author_username", Entity.USERNAME)
    )

    private val committerMap: Map<String, String> = mapOf(
        Pair("committer_avatar_url", Entity.AVATAR_URL),
        Pair("committer_email", Entity.EMAIL),
        Pair("committer_html_url", Entity.PROFILE_URL),
        Pair("committer_name", Entity.NAME),
        Pair("committer_url", Entity.API_URL),
        Pair("committer_username", Entity.USERNAME)
    )

    @ExperimentalSerializationApi
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return if (element is JsonObject) {
            val author: MutableMap<String, JsonElement> = mutableMapOf()
            val committer: MutableMap<String, JsonElement> = mutableMapOf()

            buildJsonObject {
                descriptor.elementNames.forEach { name ->
                    element[name]?.let { value ->
                        if (value is JsonNull) {
                            put(name, "")
                        } else {
                            put(name, value)
                        }
                    }
                }

                element.forEach { item ->
                    authorMap[item.key]?.let { key ->
                        author[key] = item.value
                    }
                    committerMap[item.key]?.let { key ->
                        committer[key] = item.value
                    }
                }
                put("author", JsonObject(author))
                put("committer", JsonObject(committer))
            }
        } else super.transformDeserialize(element)
    }
}

/**
 * Performs custom object serialization of a [Commit] object
 */
internal object CommitSerializer : KSerializer<Commit> {

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("Commit") {
            element<String>(elementName = Commit.ID)
            element<String>(elementName = Commit.AUTHOR_ID)
            element<String>(elementName = Commit.REF)
            element<String>(elementName = Commit.HASH)
            element<String>(elementName = Commit.TRUNCATED_HASH)
            element<String>(elementName = Commit.BRANCH)
            element<String>(elementName = Commit.MESSAGE)
            element<String>(elementName = Commit.HTML_URL)
            element<String>(elementName = Commit.CREATED_AT)
            element<String>(elementName = Commit.AUTHOR_DATE)
            element<String>(elementName = Commit.COMMITTER_DATE)
            element<String>(elementName = Commit.HUMAN_READABLE_TOTAL)
            element<String>(elementName = Commit.HUMAN_READABLE_TOTAL_SECONDS)
            element<Float>(elementName = Commit.TOTAL_SECONDS)
            element<String>(elementName = Commit.URL)
            element<Boolean>(elementName = Commit.IS_AUTHOR_FOUND)
            element<Entity>(elementName = Commit.AUTHOR)
            element<Entity>(elementName = Commit.COMMITTER)
        }

    override fun serialize(encoder: Encoder, value: Commit) {
        throw NotImplementedError("Commit serialization not implemented yet!")
    }

    @ExperimentalSerializationApi
    override fun deserialize(decoder: Decoder): Commit {
        return decoder.decodeStructure(descriptor) {
            val id = decodeNullableString(Commit.ID, this)
            val authorId = decodeNullableString(Commit.AUTHOR_ID, this)
            val ref = decodeNullableString(Commit.REF, this)
            val hash = decodeNullableString(Commit.HASH, this)
            val truncatedHash = decodeNullableString(Commit.TRUNCATED_HASH, this)
            val branch = decodeNullableString(Commit.BRANCH, this)
            val message = decodeNullableString(Commit.MESSAGE, this)
            val htmlUrl = decodeNullableString(Commit.HTML_URL, this)
            val createdAt = decodeNullableString(Commit.CREATED_AT, this)
            val authorDate = decodeNullableString(Commit.AUTHOR_DATE, this)
            val commitDate = decodeNullableString(Commit.COMMITTER_DATE, this)
            val humanTotal = decodeNullableString(Commit.HUMAN_READABLE_TOTAL, this)
            val humanTotalSec = decodeNullableString(Commit.HUMAN_READABLE_TOTAL_SECONDS, this)
            val totalSec = decodeFloatElement(descriptor, getIndex(Commit.TOTAL_SECONDS))
            val url = decodeNullableString(Commit.URL, this)
            val found = decodeBooleanElement(descriptor, getIndex(Commit.IS_AUTHOR_FOUND))
            val author = decodeSerializableElement(descriptor, getIndex(Commit.AUTHOR), Entity.serializer())
            val committer = decodeSerializableElement(
                descriptor,
                getIndex(Commit.COMMITTER),
                Entity.serializer()
            )

            Commit(
                id = id,
                authorId = authorId,
                ref = ref,
                hash = hash,
                truncatedHash = truncatedHash,
                branch = branch,
                message = message,
                htmlUrl = htmlUrl,
                createdAt = createdAt,
                authoringDate = authorDate,
                commitDate = commitDate,
                humanReadableTotal = humanTotal,
                humanReadableTotalWithSeconds = humanTotalSec,
                totalSeconds = totalSec,
                url = url,
                isAuthorFound = found,
                author = author,
                committer = committer
            )
        }
    }

    @ExperimentalSerializationApi
    private fun decodeNullableString(key: String, decoder: CompositeDecoder): String {
        return decoder.decodeNullableSerializableElement(
            descriptor,
            getIndex(key),
            String.serializer().nullable
        ) ?: ""
    }

    @ExperimentalSerializationApi
    private fun getIndex(key: String): Int = descriptor.getElementIndex(key)
}

@Serializable
data class Entity internal constructor(
    /**
     * The entity's name
     */
    @SerialName(NAME)
    val name: String = "",
    /**
     * The entity's username
     */
    @SerialName(USERNAME)
    val username: String = "",
    /**
     * The email address associated with this entity
     */
    @SerialName(EMAIL)
    val email: String = "",
    /**
     *  url of entity's avatar image
     */
    @SerialName(AVATAR_URL)
    val avatarUrl: String = "",
    /**
     * link to entity's profile on GitHub, Bitbucket, GitLab, etc
     */
    @SerialName(PROFILE_URL)
    val profileUrl: String = "",
    /**
     * Link to entity's api profile on Github, Bitbucket, Gitlab, etc
     */
    @SerialName(API_URL)
    val apiUrl: String = ""
) {
    internal companion object {
        const val NAME = "name"
        const val USERNAME = "username"
        const val EMAIL = "email"
        const val AVATAR_URL = "avatar_url"
        const val PROFILE_URL = "profile_url"
        const val API_URL = "api_url"
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