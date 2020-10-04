package `is`.hth.wakatimeclient.wakatime.data.db.entities

import `is`.hth.wakatimeclient.wakatime.model.Config
import `is`.hth.wakatimeclient.wakatime.model.CurrentUser
import `is`.hth.wakatimeclient.wakatime.model.FullUser
import `is`.hth.wakatimeclient.wakatime.model.User
import androidx.room.*

/**
 * Stores publicly available user information
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "display_name")
    val displayName: String = "",
    @ColumnInfo(name = "username")
    val username: String = "",
    @ColumnInfo(name = "full_name")
    val fullName: String = "",
    @ColumnInfo(name = "email_public")
    val email: String = "",
    @ColumnInfo(name = "website")
    val website: String = "",
    @ColumnInfo(name = "website_human")
    val websiteHumanReadable: String = "",
    @ColumnInfo(name = "location")
    val location: String = "",
    @ColumnInfo(name = "photo_url")
    val photoUrl: String = "",
    @ColumnInfo(name = "is_hireable")
    val isHireable: Boolean = false
)

/**
 * Stores the current user's wakatime configurations
 */
@Entity(tableName = "config")
data class ConfigEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "email_is_public")
    val emailIsPublic: Boolean = false,
    @ColumnInfo(name = "has_premium_features")
    val hasPremiumFeatures: Boolean = false,
    @ColumnInfo(name = "email_is_confirmed")
    val emailIsConfirmed: Boolean = false,
    @ColumnInfo(name = "photo_is_public")
    val photoIsPublic: Boolean = false,
    @ColumnInfo(name = "logged_time_is_public")
    val loggedTimeIsPublic: Boolean = false,
    @ColumnInfo(name = "languages_are_public")
    val languagesArePublic: Boolean = false,
    @ColumnInfo(name = "color_scheme")
    val colorScheme: String = "",
    @ColumnInfo(name = "timezone")
    val timezone: String = "",
    /*
       This value should potentially be moved into a separate entity related to heartbeats
     */
    @ColumnInfo(name = "last_heartbeat")
    val lastHeartbeat: String = "",
    /*
      This value should potentially be moved into a separate entity related to heartbeats
    */
    @ColumnInfo(name = "last_plugin")
    val lastPlugin: String = "",
    @ColumnInfo(name = "last_project")
    val lastProject: String = "",
    @ColumnInfo(name = "plan")
    val plan: String = "",
    @ColumnInfo(name = "date_format")
    val dateFormat: String = "",
    @ColumnInfo(name = "bio")
    val bio: String = "",
    /**
     * The primary email that is used in communication with the user
     */
    @ColumnInfo(name = "email_primary")
    val emailPrimary: String = "",
    /**
     * Is this potentially when the current payment method has expired?
     */
    @ColumnInfo(name = "needs_payment_method")
    val needsPaymentMethod: Boolean = false,
    @ColumnInfo(name = "show_machine_name_ip")
    val showMachineNameIp: Boolean = false,
    @ColumnInfo(name = "dashboard_default_range")
    val dashboardDefaultRange: String = "",
    @ColumnInfo(name = "using_24hr_format")
    val using24hrFormat: Boolean = false,
    @ColumnInfo(name = "timeout")
    val timeout: Int = 0,
    @ColumnInfo(name = "weekday_start")
    val weekdayStart: Int = 0,
    @ColumnInfo(name = "writes_only")
    val writesOnly: Boolean = false,
    /**
     * time when user was created in ISO 8601 format
     */
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    /**
     * time when user was last modified in ISO 8601 format
     */
    @ColumnInfo(name = "modified_at")
    val modifiedAt: String = ""
)

/**
 * A view combining the publicly visible information for the current user with its configuration
 */
@DatabaseView("SELECT * FROM users u, config c WHERE c.user_id == u.id")
data class CurrentUserView(
    @Embedded
    val user: UserEntity,
    @Embedded
    val config: ConfigEntity
)

/**
 * Maps this [CurrentUserView] to a [CurrentUser]
 */
internal fun CurrentUserView.toCurrentUser(): CurrentUser = CurrentUser(
    user = user.toUser(),
    config = config.toConfig()
)

/**
 * Maps this [UserEntity] to a [User]
 */
internal fun UserEntity.toUser(): User = User(
    id = id,
    displayName = displayName,
    username = username,
    fullName = fullName,
    email = email,
    website = website,
    websiteHumanReadable = websiteHumanReadable,
    location = location,
    photoUrl = photoUrl,
    isHireable = isHireable
)

/**
 * Maps this [User] to a [UserEntity]
 */
internal fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    displayName = displayName,
    username = username,
    fullName = fullName,
    email = email,
    website = website,
    websiteHumanReadable = websiteHumanReadable,
    location = location,
    photoUrl = photoUrl,
    isHireable = isHireable
)

/**
 * Maps this [ConfigEntity] to a [Config]
 */
internal fun ConfigEntity.toConfig(): Config = Config(
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
    emailPrimary = emailPrimary,
    needsPaymentMethod = needsPaymentMethod,
    showMachineNameIp = showMachineNameIp,
    using24hrFormat = using24hrFormat,
    timeout = timeout,
    weekdayStart = weekdayStart,
    writesOnly = writesOnly,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

/**
 * Maps the [FullUser] to a [CurrentUserView]
 */
@Suppress("unused")
internal fun FullUser.toCurrentUserView(): CurrentUserView = CurrentUserView(
    user = UserEntity(
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
    config = ConfigEntity(
        userId = id,
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