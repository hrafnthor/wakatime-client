package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

class CommitTests : DescribeSpec({

    val json = WakatimeJsonFactory.makeJson()

    val commit = Commit(
        id = "some kind of url",
        authorId = "id of author",
        ref = "the commit ref",
        hash = "the commit hash",
        truncatedHash = "shorter hash",
        branch = "develop",
        message = "commit message",
        htmlUrl = "html url",
        createdAt = "date of commit creation",
        authoringDate = "date of the commit authoring",
        commitDate = "date of the commit closing",
        humanReadableDate = "human readable date",
        humanReadableTimeSinceCommit = "human readable time since commit",
        humanReadableTotal = "human readable total time",
        humanReadableTotalWithSeconds = "human readable total time with seconds",
        totalSeconds = 100f,
        url = "a url to the commit on the provider",
        isAuthorFound = true,
        author = Entity(
            name = "author name",
            username = "authors username",
            email = "author email",
            avatarUrl = "author avatar url",
            profileUrl = "author avatar profile url",
            apiUrl = "a link to the authors profile on provider"
        ),
        committer = Entity(
            name = "committer name",
            username = "committer username",
            email = "committer email",
            avatarUrl = "committer avatar url",
            profileUrl = "committer profile url",
            apiUrl = "a link to the committer profile on provider"
        )
    )

    val serializedCommit = buildJsonObject {
        put("id", commit.id)
        put("author_id", commit.authorId)
        put("ref", commit.ref)
        put("hash", commit.hash)
        put("truncated_hash", commit.truncatedHash)
        put("branch", commit.branch)
        put("message", commit.message)
        put("html_url", commit.htmlUrl)
        put("created_at", commit.createdAt)
        put("author_date", commit.authoringDate)
        put("committer_date", commit.commitDate)
        put("human_readable_date", commit.humanReadableDate)
        put("human_readable_natural_date", commit.humanReadableTimeSinceCommit)
        put("human_readable_total", commit.humanReadableTotal)
        put("human_readable_total_with_seconds", commit.humanReadableTotalWithSeconds)
        put("total_seconds", commit.totalSeconds)
        put("url", commit.url)
        put("is_author_found", commit.isAuthorFound)
        put("author", json.encodeToJsonElement(commit.author))
        put("committer", json.encodeToJsonElement(commit.committer))
    }

    describe("serialization") {
        val serialized = json.encodeToJsonElement(commit)

        it("matches expected serialization") {
            serialized shouldBe serializedCommit
        }
    }

    describe("deserialization") {
        describe("of remote service payload") {
            val receivedPayload = buildJsonObject {
                put("id", commit.id)
                put("author_id", commit.authorId)
                put("ref", commit.ref)
                put("hash", commit.hash)
                put("truncated_hash", commit.truncatedHash)
                put("branch", commit.branch)
                put("message", commit.message)
                put("html_url", commit.htmlUrl)
                put("created_at", commit.createdAt)
                put("author_date", commit.authoringDate)
                put("committer_date", commit.commitDate)
                put("human_readable_date", commit.humanReadableDate)
                put("human_readable_natural_date", commit.humanReadableTimeSinceCommit)
                put("human_readable_total", commit.humanReadableTotal)
                put("human_readable_total_with_seconds", commit.humanReadableTotalWithSeconds)
                put("total_seconds", commit.totalSeconds)
                put("url", commit.url)
                put("is_author_found", commit.isAuthorFound)
                put("author_avatar_url", commit.author.avatarUrl)
                put("author_email", commit.author.email)
                put("author_html_url", commit.author.profileUrl)
                put("author_name", commit.author.name)
                put("author_url", commit.author.apiUrl)
                put("author_username", commit.author.username)
                put("committer_avatar_url", commit.committer.avatarUrl)
                put("committer_email", commit.committer.email)
                put("committer_html_url", commit.committer.profileUrl)
                put("committer_name", commit.committer.name)
                put("committer_url", commit.committer.apiUrl)
                put("committer_username", commit.committer.username)
            }

            val deserialized = json.decodeFromJsonElement<Commit>(receivedPayload)

            it("matches expected deserialization") {
                deserialized shouldBe commit
            }
        }

        describe("of local serialized payload") {
            val deserialized = json.decodeFromJsonElement<Commit>(serializedCommit)

            it("matches expected deserialization") {
                deserialized shouldBe commit
            }
        }
    }
})