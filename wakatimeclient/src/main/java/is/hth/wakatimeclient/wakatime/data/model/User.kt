package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class User internal constructor(
    /**
     * unique id of user
     */
    @SerialName(ID)
    val id: String = "",
    /**
     * The user's photo url if any is set and it is public
     */
    @SerialName(PHOTO_URL)
    val photoUrl: String = "",
    /**
     * Represents the “hireable” badge on user profiles.
     * Defaults to false if not shared.
     */
    @SerialName(IS_HIREABLE)
    val isHireable: Boolean = false,
    /**
     * The email address of the user, if public
     */
    @SerialName(EMAIL)
    val email: String = "",
    /**
     * The user's public username
     */
    @SerialName(USERNAME)
    val username: String = "",
    /**
     * The full name of the user
     */
    @SerialName(FULL_NAME)
    val fullName: String = "",
    /**
     * display name of this user taken from full_name or @username. Defaults to 'Anonymous User'
     */
    @SerialName(DISPLAY_NAME)
    val displayName: String = "",
    /**
     *  The website for this user if any has been set.
     */
    @SerialName(WEBSITE)
    val website: String = "",
    /**
     *  The website of the user without url scheme, if available
     */
    @SerialName(WEBSITE_HUMAN)
    val websiteHumanReadable: String = "",
    /**
     * Geographical location of the user, if available
     */
    @SerialName(LOCATION)
    val location: String = "",
) {
    internal companion object {
        const val ID = "id"
        const val PHOTO_URL = "photo"
        const val IS_HIREABLE = "is_hireable"
        const val EMAIL = "email"
        const val USERNAME = "username"
        const val FULL_NAME =  "full_name"
        const val DISPLAY_NAME = "display_name"
        const val WEBSITE = "website"
        const val WEBSITE_HUMAN = "human_readable_website"
        const val LOCATION = "location"
    }
}
