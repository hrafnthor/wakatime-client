package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

public class OrganizationTests : DescribeSpec({

    val json = WakatimeJsonFactory.json

    val member = Member(
        canViewDashboard = true,
        isOnlyViewingDashboard = true,
        user = User(
            id = "some id",
            photoUrl = "some url",
            email = "some email",
            username = "some username",
            fullName = "some name"
        )
    )

    val serializedMember: JsonObject = buildJsonObject {
        put(Member.IS_ONLY_VIEWING_DASHBOARD, member.isOnlyViewingDashboard)
        put(Member.CAN_VIEW_DASHBOARD, member.canViewDashboard)
        put(Member.USER, json.encodeToJsonElement(member.user))
    }

    describe("serialization") {
        describe("of Member") {
            it("from local value") {
                json.encodeToString(
                    serializer = MemberSerializer,
                    value = member
                ).shouldEqualJson(serializedMember.toString())
            }
        }
    }

    describe("deserialization") {
        describe("of Member") {
            it("as received from server") {
                val received = buildJsonObject {
                    put("can_view_dashboard", member.canViewDashboard)
                    put("email", member.user.email)
                    put("full_name", member.user.fullName)
                    put("id", member.user.id)
                    put("is_view_only", member.isOnlyViewingDashboard)
                    put("photo", member.user.photoUrl)
                    put("username", member.user.username)
                }

                json.decodeFromJsonElement(
                    deserializer = MemberTransformSerializer,
                    element = received
                ) shouldBe member
            }

            it("as received locally") {
                json.decodeFromJsonElement<Member>(serializedMember) shouldBe member
            }
        }
    }
})