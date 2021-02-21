package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines the default visibility that projects can have when shared
 */
@Serializable
enum class Visibility {
    @SerialName("visible")
    VISIBLE,

    @SerialName("hidden")
    HIDDEN
}

/**
 * Defines the statuses that goals can have given their progress at each point in time.
 */
@Serializable
@Suppress("unused")
enum class GoalStatus {

    @SerialName("success")
    SUCCESS,

    @SerialName("fail")
    FAILURE,

    @SerialName("pending")
    PENDING,

    @SerialName("ignored")
    IGNORED
}


/**
 * Defines the duration deltas that Wakatime offers for goals
 */
@Serializable
@Suppress("unused")
enum class Delta {
    @SerialName("day")
    DAY,

    @SerialName("week")
    WEEK
}

/**
 * Defines the email delivery frequency that Wakatime offers for goals
 */
@Serializable
@Suppress("unused")
enum class Frequency {
    @SerialName("Daily")
    DAILY,

    @SerialName("Every other day")
    EVERY_OTHER_DAY,

    @SerialName("Once per week")
    ONCE_PER_WEEK,

    @SerialName("Once per month")
    ONCE_PER_MONTH,

    @SerialName("")
    NONE
}

/**
 * Defines the statuses that an invitation to observe a goal can take
 */
@Serializable
@Suppress("unused")
enum class InvitationStatus {

    /**
     * The user has accepted to observe the goal
     */
    @SerialName("Accepted")
    ACCEPTED,

    /**
     * The user has been invited to observe the goal but has yet to accept it
     */
    @SerialName("Invitation sent")
    INVITED,

    /**
     * The user has declined the invitation
     */
    @SerialName("Declined")
    DECLINED
}

/**
 * Defines a set of types that a entity can take
 */
@Suppress("unused")
@Serializable
enum class Type {
    @SerialName("file")
    FILE,

    @SerialName("app")
    APP,

    @SerialName("domain")
    DOMAIN
}

/**
 * Defines a set of categories that a heartbeat can have
 */
@Suppress("unused")
@Serializable
enum class Category {
    @SerialName("coding")
    CODING,

    @SerialName("building")
    BUILDING,

    @SerialName("indexing")
    INDEXING,

    @SerialName("debugging")
    DEBUGGING,

    @SerialName("browsing")
    BROWSING,

    @SerialName("writing docs")
    DOCUMENTATION,

    @SerialName("code reviewing")
    CODEREVIEW,

    @SerialName("researching")
    RESEARCHING,

    @SerialName("learning")
    LEARNING,

    @SerialName("designing")
    DESIGNING,

    @SerialName("running tests")
    TESTS_RUNNING,

    @SerialName("writing tests")
    TESTS_WRITING,

    @SerialName("manual testing")
    TESTS_MANUAL
}