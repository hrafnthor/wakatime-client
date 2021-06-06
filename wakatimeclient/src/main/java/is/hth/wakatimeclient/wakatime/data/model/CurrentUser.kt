package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.wakatime.data.findValue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*

@Serializable
public data class Config(
    /**
     * The user's configured timeout value
     */
    @SerialName(TIMEOUT)
    val timeout: Int,
    /**
     * The day at which the week starts at
     */
    @SerialName(WEEKDAY_START)
    val weekdayStart: Int,
    /**
     * whether this user's email should be shown on the public leader board
     */
    @SerialName(IS_EMAIL_PUBLIC)
    val emailIsPublic: Boolean,
    /**
     * true if user has access to premium features
     */
    @SerialName(HAS_PREMIUM_FEATURES)
    val hasPremiumFeatures: Boolean,
    /**
     * whether this user's email address has been verified with a confirmation email
     */
    @SerialName(IS_EMAIL_CONFIRMED)
    val emailIsConfirmed: Boolean,
    /**
     *  whether this user's photo should be shown on the public leader board
     */
    @SerialName(PHOTO_IS_PUBLIC)
    val photoIsPublic: Boolean,
    /**
     * coding activity should be shown on the public leader board
     */
    @SerialName(LOGGED_TIME_PUBLIC)
    val loggedTimeIsPublic: Boolean,
    /**
     * languages used should be shown on the public leader board
     */
    @SerialName(LANGUAGES_PUBLIC)
    val languagesArePublic: Boolean,
    /**
     * Preferred color scheme
     */
    @SerialName(COLOR_SCHEME)
    val colorScheme: String = "",
    /**
     * user's timezone in Olson Country/Region format
     */
    @SerialName(TIMEZONE)
    val timezone: String = "",
    /**
     * users subscription plan
     */
    @SerialName(PLAN)
    val plan: String = "",
    /**
     * The user's preferred date format in ISO 8601
     */
    @SerialName(DATE_FORMAT)
    val dateFormat: String = "",
    /**
     *
     */
    @SerialName(DURATIONS_SLICE_BY)
    val durationSliceBy: String = "",
    /**
     * The default initial range to display on the dashboard
     */
    @SerialName(DASHBOARD_DEFAULT_RANGE)
    val dashboardDefaultRange: DefaultRange,
    /**
     * The user doesn't have and needs a payment method
     */
    @SerialName(NEEDS_PAYMENT_METHOD)
    val needsPaymentMethod: Boolean,
    /**
     * IP numbers of used machines are shown
     */
    @SerialName(SHOW_MACHINE_NAME_IP)
    val showMachineNameIp: Boolean,
    /**
     * Indicates if the user is using 24 hour time format
     */
    @SerialName(USING_24H_FORMAT)
    val using24hrFormat: Boolean,
    /**
     *
     */
    @SerialName(WRITES_ONLY)
    val writesOnly: Boolean
) {
    internal companion object {
        const val IS_EMAIL_PUBLIC = "is_email_public"
        const val HAS_PREMIUM_FEATURES = "has_premium_features"
        const val IS_EMAIL_CONFIRMED = "is_email_confirmed"
        const val PHOTO_IS_PUBLIC = "photo_public"
        const val LOGGED_TIME_PUBLIC = "logged_time_public"
        const val LANGUAGES_PUBLIC = "languages_used_public"
        const val TIMEOUT = "timeout"
        const val TIMEZONE = "timezone"
        const val WEEKDAY_START = "weekday_start"
        const val PLAN = "plan"
        const val DURATIONS_SLICE_BY = "durations_slice_by"
        const val WRITES_ONLY = "writes_only"
        const val USING_24H_FORMAT = "time_format_24hr"
        const val DASHBOARD_DEFAULT_RANGE = "default_dashboard_range"
        const val NEEDS_PAYMENT_METHOD = "needs_payment_method"
        const val COLOR_SCHEME = "color_scheme"
        const val SHOW_MACHINE_NAME_IP = "show_machine_name_ip"
        const val DATE_FORMAT = "date_format"
    }
}

@Serializable(CurrentUserJsonTransformer::class)
public data class CurrentUser(
    /**
     *
     */
    @SerialName(HAS_FINISHED_ONBOARDING)
    val onboardingFinished: Boolean = false,
    /**
     * The user's written bio
     */
    @SerialName(BIO)
    val bio: String = "",
    /**
     * The user's private email if any is configured
     */
    @SerialName(PRIVATE_EMAIL)
    val privateEmail: String = "",
    /**
     *  time of most recent heartbeat received in ISO 8601 format
     */
    @SerialName(LAST_HEARTBEAT)
    val lastHeartbeat: String = "",
    /**
     * user-agent string from the last plugin used>
     */
    @SerialName(LAST_PLUGIN)
    val lastPlugin: String = "",
    /**
     * name of editor last used
     */
    @SerialName(LAST_PLUGIN_NAME)
    val lastPluginName: String = "",
    /**
     * name of last project coded in
     */
    @SerialName(LAST_PROJECT)
    val lastProject: String = "",
    /**
     *  time when user was created in ISO 8601 format
     */
    @SerialName(CREATED_AT)
    val createdAt: String = "",
    /**
     * time when user was last modified in ISO 8601 format
     */
    @SerialName(MODIFIED_AT)
    val modifiedAt: String = "",
    /**
     * The user's details
     */
    @SerialName(USER)
    val user: User,
    /**
     * The configuration for this user
     */
    @SerialName(CONFIG)
    val config: Config
) {
    internal companion object {
        const val USER = "user"
        const val CONFIG = "config"
        const val BIO = "bio"
        const val PUBLIC_EMAIL = "public_email"
        const val PRIVATE_EMAIL = "email"
        const val HAS_FINISHED_ONBOARDING = "is_onboarding_finished"
        const val LAST_HEARTBEAT = "last_heartbeat_at"
        const val LAST_PLUGIN = "last_plugin"
        const val LAST_PLUGIN_NAME = "last_plugin_name"
        const val LAST_PROJECT = "last_project"
        const val MODIFIED_AT = "modified_at"
        const val CREATED_AT = "created_at"
    }
}

internal object CurrentUserJsonTransformer : JsonTransformingSerializer<CurrentUser>(
    CurrentUserSerializer
) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return when {
            element is JsonObject && element.size > 3 -> buildJsonObject {
                findValue(element, CurrentUser.HAS_FINISHED_ONBOARDING, false)
                findValue(element, CurrentUser.BIO, "")
                findValue(element, CurrentUser.PRIVATE_EMAIL, "")
                findValue(element, CurrentUser.LAST_HEARTBEAT, "")
                findValue(element, CurrentUser.LAST_PLUGIN, "")
                findValue(element, CurrentUser.LAST_PLUGIN_NAME, "")
                findValue(element, CurrentUser.LAST_PROJECT, "")
                findValue(element, CurrentUser.CREATED_AT, "")
                findValue(element, CurrentUser.MODIFIED_AT, "")
                putJsonObject(CurrentUser.USER) {
                    findValue(element, User.ID, "")
                    findValue(element, User.PHOTO_URL, "")
                    findValue(element, User.IS_HIREABLE, false)
                    // Set public email as user object email
                    findValue(element, CurrentUser.PUBLIC_EMAIL, User.EMAIL) {
                        JsonPrimitive("")
                    }
                    findValue(element, User.USERNAME, "")
                    findValue(element, User.FULL_NAME, "")
                    findValue(element, User.DISPLAY_NAME, "")
                    findValue(element, User.WEBSITE, "")
                    findValue(element, User.WEBSITE_HUMAN, "")
                    findValue(element, User.LOCATION, "")
                }
                putJsonObject(CurrentUser.CONFIG) {
                    findValue(element, Config.TIMEOUT, 0)
                    findValue(element, Config.WEEKDAY_START, 0)
                    findValue(element, Config.IS_EMAIL_PUBLIC, false)
                    findValue(element, Config.HAS_PREMIUM_FEATURES, false)
                    findValue(element, Config.IS_EMAIL_CONFIRMED, false)
                    findValue(element, Config.PHOTO_IS_PUBLIC, false)
                    findValue(element, Config.LOGGED_TIME_PUBLIC, false)
                    findValue(element, Config.LANGUAGES_PUBLIC, false)
                    findValue(element, Config.NEEDS_PAYMENT_METHOD, false)
                    findValue(element, Config.SHOW_MACHINE_NAME_IP, false)
                    findValue(element, Config.USING_24H_FORMAT, false)
                    findValue(element, Config.WRITES_ONLY, false)
                    findValue(element, Config.COLOR_SCHEME, "")
                    findValue(element, Config.TIMEZONE, "")
                    findValue(element, Config.PLAN, "")
                    findValue(element, Config.DATE_FORMAT, "")
                    findValue(element, Config.DURATIONS_SLICE_BY, "")
                    findValue(element, Config.DASHBOARD_DEFAULT_RANGE, "")
                }
            }
            element is JsonObject -> element
            else -> throw IllegalArgumentException(
                "Incorrect JsonElement received during deserialization!"
            )
        }
    }
}

internal object CurrentUserSerializer : KSerializer<CurrentUser> {

    private val userSerializer: KSerializer<User> = User.serializer()
    private val configSerializer: KSerializer<Config> = Config.serializer()

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("current_user") {
            element<Boolean>(CurrentUser.HAS_FINISHED_ONBOARDING)
            element<String>(CurrentUser.BIO)
            element<String>(CurrentUser.PRIVATE_EMAIL)
            element<String>(CurrentUser.LAST_HEARTBEAT)
            element<String>(CurrentUser.LAST_PLUGIN)
            element<String>(CurrentUser.LAST_PLUGIN_NAME)
            element<String>(CurrentUser.LAST_PROJECT)
            element<String>(CurrentUser.CREATED_AT)
            element<String>(CurrentUser.MODIFIED_AT)
            element(CurrentUser.USER, userSerializer.descriptor)
            element(CurrentUser.CONFIG, configSerializer.descriptor)
        }

    @ExperimentalSerializationApi
    override fun deserialize(decoder: Decoder): CurrentUser {
        return decoder.decodeStructure(descriptor) {
            val onboardingFinished = decodeBooleanElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.HAS_FINISHED_ONBOARDING)
            )
            val bio = decodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.BIO)
            )
            val publicEmail = decodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.PRIVATE_EMAIL)
            )
            val lastHeartbeat = decodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.LAST_HEARTBEAT)
            )
            val lastPlugin = decodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.LAST_PLUGIN)
            )
            val lastPluginName = decodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.LAST_PLUGIN_NAME)
            )
            val lastProject = decodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.LAST_PROJECT)
            )
            val createdAt = decodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.CREATED_AT)
            )
            val modifiedAt = decodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.MODIFIED_AT)
            )
            val user = decodeSerializableElement(
                descriptor = descriptor,
                deserializer = userSerializer,
                index = descriptor.getElementIndex(CurrentUser.USER)
            )
            val config = decodeSerializableElement(
                descriptor = descriptor,
                deserializer = configSerializer,
                index = descriptor.getElementIndex(CurrentUser.CONFIG)
            )
            CurrentUser(
                onboardingFinished = onboardingFinished,
                bio = bio,
                privateEmail = publicEmail,
                lastHeartbeat = lastHeartbeat,
                lastPlugin = lastPlugin,
                lastPluginName = lastPluginName,
                lastProject = lastProject,
                createdAt = createdAt,
                modifiedAt = modifiedAt,
                user = user,
                config = config,
            )
        }
    }

    @ExperimentalSerializationApi
    override fun serialize(encoder: Encoder, value: CurrentUser) {
        encoder.encodeStructure(descriptor) {
            encodeBooleanElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.HAS_FINISHED_ONBOARDING),
                value = value.onboardingFinished
            )
            encodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.BIO),
                value = value.bio
            )
            encodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.PRIVATE_EMAIL),
                value = value.privateEmail
            )
            encodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.LAST_HEARTBEAT),
                value = value.lastHeartbeat
            )
            encodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.LAST_PLUGIN),
                value = value.lastPlugin
            )
            encodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.LAST_PLUGIN_NAME),
                value = value.lastPluginName
            )
            encodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.LAST_PROJECT),
                value = value.lastProject
            )
            encodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.CREATED_AT),
                value = value.createdAt
            )
            encodeStringElement(
                descriptor = descriptor,
                index = descriptor.getElementIndex(CurrentUser.MODIFIED_AT),
                value = value.modifiedAt
            )
            encodeSerializableElement(
                descriptor = descriptor,
                serializer = configSerializer,
                index = descriptor.getElementIndex(CurrentUser.CONFIG),
                value = value.config
            )
            encodeSerializableElement(
                descriptor = descriptor,
                serializer = userSerializer,
                index = descriptor.getElementIndex(CurrentUser.USER),
                value = value.user
            )
        }
    }
}