package `is`.hth.wakatimeclient.wakatime.data.api.dto

import `is`.hth.wakatimeclient.wakatime.model.Config
import `is`.hth.wakatimeclient.wakatime.model.CurrentUser
import `is`.hth.wakatimeclient.wakatime.model.User
import com.google.gson.annotations.SerializedName

/**
 * A detailed  user information payload
 */
data class FullUserDto(
    /**
     * whether this user's email should be shown on the public leader board
     */
    @SerializedName("is_email_public")
    val emailIsPublic: Boolean,
    /**
     * true if user has access to premium features
     */
    @SerializedName("has_premium_features")
    val hasPremiumFeatures: Boolean,
    /**
     * whether this user's email address has been verified with a confirmation email
     */
    @SerializedName("is_email_confirmed")
    val emailIsConfirmed: Boolean,
    /**
     *  whether this user's photo should be shown on the public leader board
     */
    @SerializedName("photo_public")
    val photoIsPublic: Boolean,
    /**
     * coding activity should be shown on the public leader board
     */
    @SerializedName("logged_time_public")
    val loggedTimeIsPublic: Boolean,
    /**
     * languages used should be shown on the public leader board
     */
    @SerializedName("languages_used_public")
    val languagesArePublic: Boolean,
    /**
     * user preference showing hireable badge on public profile
     */
    @SerializedName("is_hireable")
    val isHireable: Boolean,
    /**
     * The user written bio
     */
    @SerializedName("bio")
    val bio: String = "",
    /**
     * unique id of user
     */
    @SerializedName("id")
    val id: String = "",
    /**
     * display name of this user taken from full_name or @username. Defaults to 'Anonymous User'
     */
    @SerializedName("display_name")
    val displayName: String = "",
    /**
     * full name of the user
     */
    @SerializedName("full_name")
    val fullName: String = "",
    /**
     * email address of the user
     */
    @SerializedName("email")
    val email: String = "",
    /**
     * url of photo for this user
     */
    @SerializedName("photo")
    val photoUrl: String = "",
    /**
     * email address for public profile. Nullable.
     */
    @SerializedName("public_email")
    val emailPublic: String = "",
    /**
     * user's timezone in Olson Country/Region format
     */
    @SerializedName("timezone")
    val timezone: String = "",
    /**
     *  time of most recent heartbeat received in ISO 8601 format
     */
    @SerializedName("last_heartbeat_at")
    val lastHeartbeat: String = "",
    /**
     * user-agent string from the last plugin used>
     */
    @SerializedName("last_plugin")
    val lastPlugin: String = "",
    /**
     * name of editor last used
     */
    @SerializedName("last_plugin_name")
    val lastPluginName: String = "",
    /**
     * name of last project coded in
     */
    @SerializedName("last_project")
    val lastProject: String = "",
    /**
     * users subscription plan
     */
    @SerializedName("plan")
    val plan: String = "",
    /**
     * users public username
     */
    @SerializedName("username")
    val userName: String = "",
    /**
     * website of user
     */
    @SerializedName("website")
    val website: String = "",
    /**
     *  website of user without protocol part
     */
    @SerializedName("human_readable_website")
    val websiteHumanReadable: String = "",
    /**
     * location of user
     */
    @SerializedName("location")
    val location: String = "",
    /**
     * time when user was created in ISO 8601 format
     */
    @SerializedName("created_at")
    val createdAt: String = "",
    /**
     *  time when user was last modified in ISO 8601 format
     */
    @SerializedName("modified_at")
    val modifiedAt: String = "",
    /**
     * The user's preferred date format in ISO 8601
     */
    @SerializedName("date_format")
    val dateFormat: String = "",
    /**
     * Unknown
     */
    @SerializedName("show_machine_name_ip")
    val showMachineNameIp: Boolean = false,
    /**
     * Preferred color scheme
     */
    @SerializedName("color_scheme")
    val colorScheme: String = "",
    /**
     * Unknown
     */
    @SerializedName("needs_payment_method")
    val needsPaymentMethod: Boolean = false,
    /**
     * The default initial range to display on the dashboard
     */
    @SerializedName("default_dashboard_range")
    val dashboardDefaultLRange: String = "",
    /**
     * Indicates if the user is using 24 hour time format
     */
    @SerializedName("time_format_24hr")
    val using24hrFormat: Boolean = false,
    /**
     *
     */
    @SerializedName("timeout")
    val timeout: Int = 0,
    /**
     *
     */
    @SerializedName("weekday_start")
    val weekdayStart: Int = 0,
    /**
     *
     */
    @SerializedName("writes_only")
    val writesOnly: Boolean = false
)

/**
 * Maps the [FullUserDto] to a [CurrentUser]
 */
fun FullUserDto.toCurrentUser(): CurrentUser = CurrentUser(
    user = User(
        id = id,
        displayName = displayName,
        userName = userName,
        fullName = fullName,
        emailPublic = emailPublic,
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
        timezone = timezone
    )
)