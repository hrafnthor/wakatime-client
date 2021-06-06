package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

public class CurrentUserTests : DescribeSpec({

    val json = WakatimeJsonFactory.json

    val current = CurrentUser(
        user = User(
            id = "123",
            photoUrl = "some url",
            isHireable = true,
            email = "some email",
            username = "some username",
            fullName = "some name",
            displayName = "some name",
            website = "some url",
            websiteHumanReadable = "some url",
            location = "some location"
        ),
        config = Config(
            timeout = 1,
            weekdayStart = 1,
            emailIsPublic = true,
            hasPremiumFeatures = false,
            emailIsConfirmed = true,
            photoIsPublic = false,
            loggedTimeIsPublic = true,
            languagesArePublic = false,
            colorScheme = "Dark",
            timezone = "some zone",
            plan = "premium",
            dateFormat = "YYYY-MM-DD",
            durationSliceBy = "Languages",
            dashboardDefaultRange = DefaultRange.LAST_SEVEN_DAYS,
            needsPaymentMethod = false,
            showMachineNameIp = true,
            using24hrFormat = true,
            writesOnly = false
        ),
        bio = "some bio information",
        privateEmail = "some private email value",
        lastHeartbeat = "some value",
        lastPlugin = "some value",
        lastPluginName = "some name",
        lastProject = "some project",
        createdAt = "2021-09-17T18:27:40Z",
        modifiedAt = "2021-09-17T18:27:40Z"
    )

    describe("deserialization") {
        it("of CurrentUser") {
            val payload = buildJsonObject {
                put("bio", current.bio)
                put("color_scheme", current.config.colorScheme)
                put("created_at", current.createdAt)
                put("date_format", current.config.dateFormat)
                put("is_onboarding_finished", current.onboardingFinished)
                put("default_dashboard_range", current.config.dashboardDefaultRange.toString())
                put("display_name", current.user.displayName)
                put("durations_slice_by", current.config.durationSliceBy)
                put("email", current.privateEmail)
                put("full_name", current.user.fullName)
                put("has_premium_features", current.config.hasPremiumFeatures)
                put("human_readable_website", current.user.websiteHumanReadable)
                put("id", current.user.id)
                put("is_email_confirmed", current.config.emailIsConfirmed)
                put("is_email_public", current.config.emailIsPublic)
                put("is_hireable", current.user.isHireable)
                put("languages_used_public", current.config.languagesArePublic)
                put("last_heartbeat_at", current.lastHeartbeat)
                put("last_plugin", current.lastPlugin)
                put("last_plugin_name", current.lastPluginName)
                put("last_project", current.lastProject)
                put("location", current.user.location)
                put("logged_time_public", current.config.loggedTimeIsPublic)
                put("modified_at", current.modifiedAt)
                put("needs_payment_method", current.config.needsPaymentMethod)
                put("photo", current.user.photoUrl)
                put("photo_public", current.config.photoIsPublic)
                put("plan", current.config.plan)
                put("public_email", current.user.email)
                put("show_machine_name_ip", current.config.showMachineNameIp)
                put("time_format_24hr", current.config.using24hrFormat)
                put("timeout", current.config.timeout)
                put("timezone", current.config.timezone)
                put("username", current.user.username)
                put("website", current.user.website)
                put("weekday_start", current.config.weekdayStart)
                put("writes_only", current.config.writesOnly)
            }
            json.decodeFromJsonElement<CurrentUser>(payload) shouldBe current
        }
    }

    describe("serialization") {
        it("of CurrentUser") {
            val expected = buildJsonObject {
                put("is_onboarding_finished", current.onboardingFinished)
                put("bio", current.bio)
                put("email", current.privateEmail)
                put("last_heartbeat_at", current.lastHeartbeat)
                put("last_plugin", current.lastPlugin)
                put("last_plugin_name", current.lastPluginName)
                put("last_project", current.lastProject)
                put("created_at", current.createdAt)
                put("modified_at", current.modifiedAt)
                putJsonObject("config") {
                    put("timeout", current.config.timeout)
                    put("weekday_start", current.config.weekdayStart)
                    put("is_email_public", current.config.emailIsPublic)
                    put("has_premium_features", current.config.hasPremiumFeatures)
                    put("is_email_confirmed", current.config.emailIsConfirmed)
                    put("photo_public", current.config.photoIsPublic)
                    put("logged_time_public", current.config.loggedTimeIsPublic)
                    put("languages_used_public", current.config.languagesArePublic)
                    put("color_scheme", current.config.colorScheme)
                    put("timezone", current.config.timezone)
                    put("plan", current.config.plan)
                    put("date_format", current.config.dateFormat)
                    put("durations_slice_by", current.config.durationSliceBy)
                    put("default_dashboard_range", current.config.dashboardDefaultRange.toString())
                    put("needs_payment_method", current.config.needsPaymentMethod)
                    put("show_machine_name_ip", current.config.showMachineNameIp)
                    put("time_format_24hr", current.config.using24hrFormat)
                    put("writes_only", current.config.writesOnly)
                }
                putJsonObject("user") {
                    put("id", current.user.id)
                    put("photo", current.user.photoUrl)
                    put("is_hireable", current.user.isHireable)
                    put("email", current.user.email)
                    put("username", current.user.username)
                    put("full_name", current.user.fullName)
                    put("display_name", current.user.displayName)
                    put("website", current.user.website)
                    put("human_readable_website", current.user.websiteHumanReadable)
                    put("location", current.user.location)
                }
            }
            json.encodeToString(current).shouldEqualJson(expected.toString())
        }
    }

})