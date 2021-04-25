package `is`.hth.wakatimeclient.wakatime.data.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

class StatsTests : DescribeSpec({

    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    describe("serialization") {
        json.encodeToString(Stats())
    }

    describe("deserialization of Stats") {
        // Reduced payload is received during a pending processing step
        describe("as pending response") {
            val pending = Stats(
                data = StatsData(
                    codingActivityPubliclyVisible = true,
                    includesToday = false,
                    otherActivityPubliclyVisible = true,
                    username = "user"
                ),
                status = Status(
                    percentCalculated = 0,
                    status = ProcessingStatus.Pending,
                )
            )

            val pendingPayload: String = buildJsonObject {
                put("is_coding_activity_visible", pending.data.codingActivityPubliclyVisible)
                put("is_including_today", pending.data.includesToday)
                put("is_other_usage_visible", pending.data.otherActivityPubliclyVisible)
                put("percent_calculated", pending.status.percentCalculated)
                put("status", "pending_update")
                put("username", pending.data.username)
            }.toString()


            val deserialized = json.decodeFromString<Stats>(pendingPayload)

            it("matches expected deserialization") {
                deserialized shouldBe pending
            }
        }

        // Reduced payload is received during an ongoing processing step
        describe("as processing response") {

            val processing = Stats(
                data = StatsData(
                    createdAt = "date",
                    end = "date",
                    id = "123456789",
                    codingActivityPubliclyVisible = true,
                    includesToday = false,
                    otherActivityPubliclyVisible = true,
                    range = HumanRange.HALF_YEAR,
                    timeout = 15,
                    timezone = "zone",
                    userId = "123456789",
                    username = "user",
                    writesOnly = true
                ),
                status = Status(
                    isAlreadyUpdating = true,
                    isStuck = false,
                    isUpToDate = false,
                    percentCalculated = 13,
                    status = ProcessingStatus.Processing,
                )
            )

            val processingPayload: String = buildJsonObject {
                put("created_at", processing.data.createdAt)
                put("end", processing.data.end)
                put("human_readable_range", "last 6 months")
                put("id", processing.data.id)
                put("is_already_updating", processing.status.isAlreadyUpdating)
                put("is_coding_activity_visible", processing.data.codingActivityPubliclyVisible)
                put("is_including_today", processing.data.includesToday)
                put("is_other_usage_visible", processing.data.otherActivityPubliclyVisible)
                put("is_stuck", processing.status.isStuck)
                put("is_up_to_date", processing.status.isUpToDate)
                put("modified_at", processing.data.modifiedAt)
                put("percent_calculated", processing.status.percentCalculated)
                put("project", JsonNull)
                put("range", "last_6_months")
                put("start", processing.data.start)
                put("status", "updating")
                put("timeout", processing.data.timeout)
                put("timezone", processing.data.timezone)
                put("user_id", processing.data.userId)
                put("username", processing.data.username)
                put("writes_only", processing.data.writesOnly)
            }.toString()

            val deserialized = json.decodeFromString<Stats>(processingPayload)

            it("matches expected deserialization") {
                deserialized shouldBe processing
            }
        }

        // Verifies that the custom serializer works both on
        // remote values as well as locally modified ones
        describe("after serialization") {
            val serialized = json.encodeToString(Stats())
            json.decodeFromString<Stats>(serialized)
        }
    }
})