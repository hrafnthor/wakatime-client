package `is`.hth.wakatimeclient.wakatime.model

// TODO: 2.8.2020 Still some fields left to be added
data class Config(
    val emailIsPublic: Boolean,
    val hasPremiumFeatures: Boolean,
    val emailIsConfirmed: Boolean,
    val photoIsPublic: Boolean,
    val loggedTimeIsPublic: Boolean,
    val languagesArePublic: Boolean,
    val colorScheme: String,
    val timezone: String
)