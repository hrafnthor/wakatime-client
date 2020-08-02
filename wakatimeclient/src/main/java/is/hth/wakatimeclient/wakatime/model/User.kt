package `is`.hth.wakatimeclient.wakatime.model

data class User(
    val id: String,
    val displayName: String,
    val userName: String,
    val fullName: String,
    val emailPublic: String,
    val website: String,
    val websiteHumanReadable: String,
    val location: String,
    val photoUrl: String,
    val isHireable: Boolean
)

