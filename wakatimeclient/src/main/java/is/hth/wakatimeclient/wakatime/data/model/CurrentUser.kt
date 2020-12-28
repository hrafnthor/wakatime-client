package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The raw detailed user information payload received from the api
 */
@Serializable
data class NetworkUser(
    /**
     * whether this user's email should be shown on the public leader board
     */
    @SerialName("is_email_public")
    val emailIsPublic: Boolean,
    /**
     * true if user has access to premium features
     */
    @SerialName("has_premium_features")
    val hasPremiumFeatures: Boolean,
    /**
     * whether this user's email address has been verified with a confirmation email
     */
    @SerialName("is_email_confirmed")
    val emailIsConfirmed: Boolean,
    /**
     *  whether this user's photo should be shown on the public leader board
     */
    @SerialName("photo_public")
    val photoIsPublic: Boolean,
    /**
     * coding activity should be shown on the public leader board
     */
    @SerialName("logged_time_public")
    val loggedTimeIsPublic: Boolean,
    /**
     * languages used should be shown on the public leader board
     */
    @SerialName("languages_used_public")
    val languagesArePublic: Boolean,
    /**
     * user preference showing hireable badge on public profile
     */
    @SerialName("is_hireable")
    val isHireable: Boolean,
    /**
     * The user written bio
     */
    val bio: String = "",
    /**
     * unique id of user
     */
    val id: String = "",
    /**
     * display name of this user taken from full_name or @username. Defaults to 'Anonymous User'
     */
    @SerialName("display_name")
    val displayName: String = "",
    /**
     * full name of the user
     */
    @SerialName("full_name")
    val fullName: String = "",
    /**
     * email address of the user
     */
    val email: String = "",
    /**
     * url of photo for this user
     */
    @SerialName("photo")
    val photoUrl: String = "",
    /**
     * email address for public profile. Nullable.
     */
    @SerialName("public_email")
    val emailPublic: String = "",
    /**
     * user's timezone in Olson Country/Region format
     */
    val timezone: String = "",
    /**
     *  time of most recent heartbeat received in ISO 8601 format
     */
    @SerialName("last_heartbeat_at")
    val lastHeartbeat: String = "",
    /**
     * user-agent string from the last plugin used>
     */
    @SerialName("last_plugin")
    val lastPlugin: String = "",
    /**
     * name of editor last used
     */
    @SerialName("last_plugin_name")
    val lastPluginName: String = "",
    /**
     * name of last project coded in
     */
    @SerialName("last_project")
    val lastProject: String = "",
    /**
     * users subscription plan
     */
    val plan: String = "",
    /**
     * users public username
     */
    @SerialName("username")
    val username: String = "",
    /**
     * website of user
     */
    val website: String = "",
    /**
     *  website of user without protocol part
     */
    @SerialName("human_readable_website")
    val websiteHumanReadable: String = "",
    /**
     * location of user
     */
    val location: String = "",
    /**
     * time when user was created in ISO 8601 format
     */
    @SerialName("created_at")
    val createdAt: String = "",
    /**
     *  time when user was last modified in ISO 8601 format
     */
    @SerialName("modified_at")
    val modifiedAt: String = "",
    /**
     * The user's preferred date format in ISO 8601
     */
    @SerialName("date_format")
    val dateFormat: String = "",
    /**
     * Unknown
     */
    @SerialName("show_machine_name_ip")
    val showMachineNameIp: Boolean = false,
    /**
     * Preferred color scheme
     */
    @SerialName("color_scheme")
    val colorScheme: String = "",
    /**
     * Unknown
     */
    @SerialName("needs_payment_method")
    val needsPaymentMethod: Boolean = false,
    /**
     * The default initial range to display on the dashboard
     */
    @SerialName("default_dashboard_range")
    val dashboardDefaultRange: String = "",
    /**
     * Indicates if the user is using 24 hour time format
     */
    @SerialName("time_format_24hr")
    val using24hrFormat: Boolean = false,
    /**
     *
     */
    val timeout: Int = 0,
    /**
     *
     */
    @SerialName("weekday_start")
    val weekdayStart: Int = 0,
    /**
     *
     */
    @SerialName("writes_only")
    val writesOnly: Boolean = false
)

/**
 * Maps the [NetworkUser] to a [CurrentUser]
 */
@Suppress("unused")
fun NetworkUser.toCurrentUser(): CurrentUser = CurrentUser(
    user = User(
        id = id,
        displayName = displayName,
        username = username,
        fullName = fullName,
        email = emailPublic,
        website = website,
        websiteHumanReadable = websiteHumanReadable,
        location = location,
        photoUrl = photoUrl,
        isHireable = isHireable
    ),
    config = Config(
        emailIsPublic = emailIsPublic,
        hasPremiumFeatures = hasPremiumFeatures,
        emailIsConfirmed = emailIsConfirmed,
        photoIsPublic = photoIsPublic,
        loggedTimeIsPublic = loggedTimeIsPublic,
        languagesArePublic = languagesArePublic,
        colorScheme = colorScheme,
        timezone = timezone,
        lastHeartbeat = lastHeartbeat,
        lastPlugin = lastPlugin,
        lastProject = lastProject,
        plan = plan,
        dateFormat = dateFormat,
        bio = bio,
        dashboardDefaultRange = dashboardDefaultRange,
        emailPrimary = email,
        needsPaymentMethod = needsPaymentMethod,
        showMachineNameIp = showMachineNameIp,
        using24hrFormat = using24hrFormat,
        timeout = timeout,
        weekdayStart = weekdayStart,
        writesOnly = writesOnly,
        createdAt = createdAt,
        modifiedAt = modifiedAt
    )
)

data class CurrentUser(
    val user: User,
    val config: Config
)