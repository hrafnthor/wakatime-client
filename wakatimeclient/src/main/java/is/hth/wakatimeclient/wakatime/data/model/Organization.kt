package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.wakatime.data.findValue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*

@Serializable
public data class Organization internal constructor(
    /**
     * The unique id of the organization
     */
    val id: String,
    /**
     * The name given to the organization
     */
    val name: String,
    /**
     * Number of members in the organization
     */
    @SerialName("people_count")
    val memberCount: Int,
    /**
     * Number of pending invites to the organization
     */
    @SerialName("invited_people_count")
    val pendingInvites: Int,
    /**
     * This organization's timeout preference
     */
    val timeout: Int,
    /**
     *
     */
    @SerialName("needs_payment_method")
    val needsPaymentMethod: Boolean,
    /**
     * Indicates if people in this organization can see each other's duration activity
     */
    @SerialName("is_duration_visible")
    val isDurationVisible: Boolean,
    /**
     *  Indicates if only aggregate total member stats are displayed
     */
    @SerialName("is_anonymous")
    val isAnonymous: Boolean,
    /**
     * Indicates whether the currently authenticated user can add people to dashboards
     */
    @SerialName("can_current_user_add_people_to_dashboards")
    val userCanAddPeopleToDashboard: Boolean,
    /**
     * Indicates whether the currently authenticated user can add people to this org
     */
    @SerialName("can_current_user_add_people_to_org")
    val userCanAddPeopleToOrganization: Boolean,
    /**
     * Indicates whether the currently authenticated user can create dashboards
     */
    @SerialName("can_current_user_create_dashboards")
    val userCanCreateDashboards: Boolean,
    /**
     * Indicates whether the currently authenticated user can delete this org
     */
    @SerialName("can_current_user_delete_org")
    val userCanDeleteOrganization: Boolean,
    /**
     * Indicates whether the currently authenticated user can display their coding activity on dashboards
     */
    @SerialName("can_current_user_display_coding_on_dashboards")
    val userCanDisplayCodingOnDashboards: Boolean,
    /**
     * Indicates whether the currently authenticated user can edit and delete dashboards
     */
    @SerialName("can_current_user_edit_and_delete_dashboards")
    val userCanEditDashboards: Boolean,
    /**
     * Indicates whether the currently authenticated user can edit this org’s preferences
     */
    @SerialName("can_current_user_edit_org")
    val userCanEditOrganization: Boolean,
    /**
     * Indicates whether the currently authenticated user can list dashboards
     */
    @SerialName("can_current_user_list_dashboards")
    val userCanListDashboards: Boolean,
    /**
     * Indicates whether the currently authenticated user can manage this org’s billing
     */
    @SerialName("can_current_user_manage_billing")
    val userCanManageBilling: Boolean,
    /**
     * Indicates whether the currently authenticated user can add, manage, and delete groups and permissions
     */
    @SerialName("can_current_user_manage_groups")
    val userCanManageGroups: Boolean,
    /**
     * Indicates whether the currently authenticated user can remove people from dashboards
     */
    @SerialName("can_current_user_remove_people_from_dashboards")
    val userCanRemovePeopleFromDashboards: Boolean,
    /**
     * Indicates whether the currently authenticated user can remove people from this org
     */
    @SerialName("can_current_user_remove_people_from_org")
    val userCanRemovePeopleFromOrganization: Boolean,
    /**
     * Indicates whether the currently authenticated user can view dashboards in this org without first being invited
     */
    @SerialName("can_current_user_view_all_dashboards")
    val userCanViewAllDashboards: Boolean,
    /**
     * Indicates whether the currently authenticated user can view the org’s audit log
     */
    @SerialName("can_current_user_view_audit_log")
    val userCanViewAuditLog: Boolean,
    /**
     * This organization's writes-only preference
     */
    @SerialName("writes_only")
    val writesOnly: Boolean,
    /**
     * The configured default visibility for projects on dashboards
     */
    @SerialName("default_project_privacy")
    val defaultProjectPrivacy: Visibility,
    /**
     * This organization's timezone preference
     */
    val timezone: String,
    /**
     * The time when the organization was created in ISO 8601 format
     */
    @SerialName("created_at")
    val createdAt: String,
    /**
     * The time when the organization was last modified in ISO 8601 format.
     * Will be empty if no modifications have been made.
     */
    @SerialName("modified_at")
    val modifiedAt: String = ""
)

@Serializable
public data class Dashboard internal constructor(
    /**
     * unique id of this dashboard
     */
    val id: String = "",
    /**
     * The name given to the dashboard
     */
    @SerialName("full_name")
    val name: String = "",
    /**
     * The number of members whose coding activity is being tracked on this dashboard
     */
    @SerialName("members_count")
    val trackedMemberCount: Int = -1,
    /**
     * The number of members in addition to those who only view the dashboard
     */
    @SerialName("members_including_view_only_count")
    val totalMemberCount: Int = -1,
    /**
     * Number of unrestricted viewers for the dashboard. If viewing is unrestricted
     * (see [hasRestrictedViewing]) then this will count all viewers.
     */
    @SerialName("users_without_restricted_viewing_count")
    val unrestrictedViewerCount: Int = -1,
    /**
     * The timeout as configured for this dashboard
     */
    val timeout: Int,
    /**
     * Indicates whether this dashboard's timezone is different from the
     * organization's timezone preference
     */
    @SerialName("has_changed_timezone")
    val hasChangedTimezone: Boolean = false,
    /**
     *
     */
    @SerialName("has_changed_timezone_from_current_user")
    val hasDifferentTimezoneThanUser: Boolean = false,
    /**
     * Indicates if only aggregate total member stats are displayed
     */
    @SerialName("is_anonymous")
    val isAnonymous: Boolean,
    /**
     * The current user is a member of this dashboard
     */
    @SerialName("is_current_user_member")
    val userIsMember: Boolean = false,
    /**
     * Indicates whether the currently authenticated user can view this dashboard
     */
    @SerialName("can_current_user_view")
    val userCanView: Boolean = false,
    /**
     * Indicates whether the currently authenticated user can request to view this dashboard.
     * This will return false if the user already is viewing the dashboard.
     */
    @SerialName("can_current_user_request_to_view")
    val userCanRequestView: Boolean = false,
    /**
     * Indicates whether the currently authenticated user can request to join this dashboard.
     * This will return false if the user is already in the dashboard.
     */
    @SerialName("can_current_user_request_to_join")
    val userCanRequestJoining: Boolean = false,
    /**
     * Indicates whether the currently authenticated user can add members to this dashboard
     */
    @SerialName("can_current_user_add_members")
    val userCanAddMembers: Boolean = false,
    /**
     * Indicates whether the currently authenticated user can remove members from this dashboard
     */
    @SerialName("can_current_user_remove_members")
    val userCanRemoveMembers: Boolean = false,
    /**
     * Indicates whether the currently authenticated user can delete this dashboard
     */
    @SerialName("can_current_user_delete")
    val userCanDeleteDashboard: Boolean = false,
    /**
     *
     */
    @SerialName("has_changed_timeout_from_current_user")
    val hasDifferentTimeoutThanUser: Boolean = false,
    /**
     * Indicates if restricted viewing has been turned on. During restricted viewing, only
     * specifically invited members can view this dashboard.
     */
    @SerialName("is_viewing_restricted")
    val hasRestrictedViewing: Boolean = false,
    /**
     *
     */
    @SerialName("use_single_timezone")
    val usingSingleTimezone: Boolean = false,
    /**
     *
     */
    @SerialName("writes_only")
    val writesOnly: Boolean = false,
    /**
     * Name of the user who created this dashboard
     */
    @SerialName("created_by")
    val createdBy: String = "",
    /**
     * the dashboard's timezone in Olson Country/Region format; defaults to
     * the organization’s timezone
     */
    val timezone: String = "",
    /**
     * The time when the dashboard was last modified in ISO 8601 format
     */
    @SerialName("modified_at")
    val modifiedAt: String = "",
    /**
     * The time when the dashboard was created in ISO 8601 format
     */
    @SerialName("created_at")
    val createdAt: String = "",
    /**
     * The configured default visibility for projects on dashboards
     */
    @SerialName("default_project_privacy")
    val visibility: Visibility,
    /**
     * The email update frequency of the board
     */
    @SerialName("email_frequency")
    val emailFrequency: Frequency = Frequency.None,
    /**
     * A list of unrestricted viewers. If restricted viewing is disabled
     * (see [hasRestrictedViewing]) the all viewers will be returned.
     */
    @SerialName("users_without_restricted_viewing")
    val viewers: List<User> = emptyList()
)

@Serializable(MemberTransformSerializer::class)
public data class Member internal constructor(
    /**
     * Indicates whether the user can view the dashboard
     */
    @SerialName(CAN_VIEW_DASHBOARD)
    val canViewDashboard: Boolean = CAN_VIEW_DASHBOARD_DEFAULT,
    /**
     * Indicates whether the user is only viewing the dashboard, or participating
     * in supplying activity
     */
    @SerialName(IS_ONLY_VIEWING_DASHBOARD)
    val isOnlyViewingDashboard: Boolean = IS_ONLY_VIEWING_DASHBOARD_DEFAULT,
    /**
     * The user backing up this member
     */
    @SerialName(USER)
    val user: User
) {
    internal companion object {
        const val CAN_VIEW_DASHBOARD = "can_view_dashboard"
        const val IS_ONLY_VIEWING_DASHBOARD = "is_view_only"
        const val USER = "user"

        const val CAN_VIEW_DASHBOARD_DEFAULT = false
        const val IS_ONLY_VIEWING_DASHBOARD_DEFAULT = false
    }
}

/**
 * Transforms the incoming JSON payload to simplify the resulting structure and reuse
 * [User] objects for portion of the payload
 */
internal object MemberTransformSerializer : JsonTransformingSerializer<Member>(MemberSerializer) {

    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element is JsonObject) {
            return if (element.size != 3) {
                // The inner element is of the correct type and does not seem to
                // be already processed payload
                buildJsonObject {
                    findValue(
                        element = element,
                        key = Member.CAN_VIEW_DASHBOARD,
                        default = Member.CAN_VIEW_DASHBOARD_DEFAULT
                    )

                    findValue(
                        element = element,
                        key = Member.IS_ONLY_VIEWING_DASHBOARD,
                        default = Member.IS_ONLY_VIEWING_DASHBOARD_DEFAULT
                    )

                    putJsonObject(Member.USER) {
                        findValue(element, User.ID, "")
                        findValue(element, User.PHOTO_URL, "")
                        findValue(element, User.IS_HIREABLE, false)
                        findValue(element, User.EMAIL, "")
                        findValue(element, User.USERNAME, "")
                        findValue(element, User.FULL_NAME, "")
                        findValue(element, User.DISPLAY_NAME, "")
                        findValue(element, User.WEBSITE, "")
                        findValue(element, User.WEBSITE_HUMAN, "")
                        findValue(element, User.LOCATION, "")
                    }
                }

            } else element
        }
        throw IllegalArgumentException("Incorrect JsonElement type received for Member deserialization!")
    }
}

/**
 * Handles the custom deserialization of the [Member] payload after having been pre processed
 * in the [MemberTransformSerializer]. Ordering can thus be relied upon.
 */
internal object MemberSerializer : KSerializer<Member> {

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("member") {
            element<Boolean>(elementName = Member.CAN_VIEW_DASHBOARD)
            element<Boolean>(elementName = Member.IS_ONLY_VIEWING_DASHBOARD)
            element<User>(elementName = Member.USER)
        }

    @ExperimentalSerializationApi
    override fun serialize(encoder: Encoder, value: Member) {
        return encoder.encodeStructure(descriptor) {
            encodeBooleanElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(Member.CAN_VIEW_DASHBOARD),
                value = value.canViewDashboard
            )
            encodeBooleanElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(Member.IS_ONLY_VIEWING_DASHBOARD),
                value = value.isOnlyViewingDashboard
            )
            encodeSerializableElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(Member.USER),
                serializer = User.serializer(),
                value = value.user
            )
        }
    }

    @ExperimentalSerializationApi
    override fun deserialize(decoder: Decoder): Member {
        return decoder.decodeStructure(descriptor) {
            val canView = decodeBooleanElement(
                descriptor = descriptor,
                index = getIndex(Member.CAN_VIEW_DASHBOARD)
            )
            val isViewOnly = decodeBooleanElement(
                descriptor = descriptor,
                index = getIndex(Member.IS_ONLY_VIEWING_DASHBOARD)
            )
            val user = decodeSerializableElement(
                descriptor = descriptor,
                index = getIndex(Member.USER),
                deserializer = User.serializer()
            )
            Member(
                canViewDashboard = canView,
                isOnlyViewingDashboard = isViewOnly,
                user = user
            )
        }
    }

    @ExperimentalSerializationApi
    private fun getIndex(key: String): Int = descriptor.getElementIndex(key)
}
