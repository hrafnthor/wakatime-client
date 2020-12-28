package `is`.hth.wakatimeclient.wakatime.data.model

data class Config(
    val timeout: Int,
    val weekdayStart: Int,
    val emailIsPublic: Boolean,
    val hasPremiumFeatures: Boolean,
    val emailIsConfirmed: Boolean,
    val photoIsPublic: Boolean,
    val loggedTimeIsPublic: Boolean,
    val languagesArePublic: Boolean,
    val colorScheme: String,
    val timezone: String,
    val lastHeartbeat: String,
    val lastPlugin: String,
    val lastProject: String,
    val plan: String,
    val dateFormat: String,
    val bio: String,
    val emailPrimary: String,
    val dashboardDefaultRange: String,
    val needsPaymentMethod: Boolean,
    val showMachineNameIp: Boolean,
    val using24hrFormat: Boolean,
    val writesOnly: Boolean,
    val createdAt: String,
    val modifiedAt: String
)