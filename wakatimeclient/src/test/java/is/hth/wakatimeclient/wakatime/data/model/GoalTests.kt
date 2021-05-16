package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.*

private class GoalTests : DescribeSpec({

    val json = WakatimeJsonFactory.makeJson()


    describe("serialization") {

    }
    describe("deserialization") {
        describe("of InvitedUser") {
            it("as received from server") {

                val invitedUser = InvitedUser(
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
                        put("display_name", invitedUser.user.displayName)
                        put("email", invitedUser.user.email)
                        put("full_name", invitedUser.user.fullName)
                        put("id", invitedUser.id)
                        put("photo", invitedUser.user.photoUrl)
                        put("status", json.encodeToJsonElement(invitedUser.status))
                        put("user_id", invitedUser.user.id)
                        put("username", invitedUser.user.username)
                    })
                }

                json.decodeFromJsonElement(
                    deserializer = InvitedUserListTransformer,
                    element = received
                ) shouldBe listOf (invitedUser)
            }
            it("as serialized locally") {

            }
        }
    }
})