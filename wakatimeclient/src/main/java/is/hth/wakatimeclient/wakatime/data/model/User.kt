package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class User internal constructor(
    /**
     * unique id of user
     */
    val id: String = "",
    /**
     * The user's photo url if any is set and it is public
     */
    @SerialName("photo")
    val photoUrl: String = "",
    /**
     * represents the “hireable” badge on user profiles
     */
    @SerialName("is_hireable")
    val isHireable: Boolean = false,
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
