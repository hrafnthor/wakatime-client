package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.*

private class GoalTests : DescribeSpec({

    val json = WakatimeJsonFactory.makeJson()

    describe("deserialization") {
        describe("of list of InvitedUser") {
            it("as received from server") {
                val expected = InvitedUser(
                    id = "123",
                    status = InvitationStatus.Accepted,
                    // No all values used in a user object are received from the
                    // server, so only configure those which are.
                    user = User(
                        id = "some id",
                        photoUrl = "some url",
                        email = "some email",
                        username = "some username",
                        fullName = "some name",
                        displayName = "some display name"
                    )
                )

                // json transform is applied to invited user list in Goal
                // so specifically test list serialization of the received
                // response from the server as it exists inside of Goal.
                val received = buildJsonArray {
                    add(buildJsonObject {
                        put("display_name", expected.user.displayName)
                        put("email", expected.user.email)
                        put("full_name", expected.user.fullName)
                        put("id", expected.id)
                        put("photo", expected.user.photoUrl)
                        put("status", json.encodeToJsonElement(expected.status))
                        put("user_id", expected.user.id)
                        put("username", expected.user.username)
                    })
                }

                json.decodeFromJsonElement(
                    deserializer = InvitedUserListTransformer,
                    element = received
                ) shouldBe listOf(expected)
            }
        }

        describe("of list of Subscriber") {
            it("as received from server") {
                val expected = Subscriber(
                    // No all values used in a user object are received from the
                    // server, so only configure those which are.
                    user = User(
                        id = "some id",
                        email = "some email",
                        username = "some username",
                        fullName = "some name",
                        displayName = "some display name"
                    ),
                    frequency = Frequency.EveryOtherDay
                )

                val received = buildJsonArray {
                    add(buildJsonObject {
                        put("display_name", expected.user.displayName)
                        put("email", expected.user.email)
                        put("email_frequency", json.encodeToJsonElement(expected.frequency))
                        put("full_name", expected.user.fullName)
                        put("user_id", expected.user.id)
                        put("username", expected.user.username)
                    })
                }

                json.decodeFromJsonElement(
                    deserializer = SubscriberListTransformer,
                    element = received
                ) shouldBe listOf(expected)
            }
        }
    }
})