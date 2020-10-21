package `is`.hth.wakatimeclient.wakatime.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    /**
     * unique id of user
     */
    val id: String = "",
    /**
     * The user's photo url if any is set and it is public
     */
    val photoUrl: String = "",
    /**
     * represents the “hireable” badge on user profiles
     */
    @SerialName("is_hireable")
    val isHireable: Boolean,
    /**
     * email address of user, if public
     */
    val email: String = "",
    /**
     *  users public username
     */
    val username: String = "",
    /**
     * full name of user
     */
    @SerialName("full_name")
    val fullName: String = "",
    /**
     * display name of this user taken from full_name or @username. Defaults to 'Anonymous User'
     */
    @SerialName("display_name")
    val displayName: String = "",
    /**
     *  website of user
     */
    val website: String = "",
    /**
     *  website of user without url scheme
     */
    @SerialName("human_readable_website")
    val websiteHumanReadable: String = "",
    /**
     * Geographical location of the user
     */
    val location: String = "",
)
