package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines the default visibility that projects can have when shared
 */
@Serializable
@Suppress("unused")
enum class Visibility {
    @SerialName("visible")
    Visible,

    @SerialName("hidden")
    Hidden
}

/**
 * Defines the statuses that goals can have given their progress at each point in time.
 */
@Serializable
@Suppress("unused")
enum class GoalStatus {

    @SerialName("success")
    Success,

    @SerialName("fail")
    Failure,

    @SerialName("pending")
    Pending,

    @SerialName("ignored")
    Ignored
}


/**
 * Defines the duration deltas that Wakatime offers for goals
 */
@Serializable
@Suppress("unused")
enum class Delta {
    @SerialName("day")
    Day,

    @SerialName("week")
    Week,

    @SerialName("all time")
    AllTime
}

/**
 * Defines the email delivery frequency that Wakatime offers for goals
 */
@Serializable
@Suppress("unused")
enum class Frequency {
    @SerialName("Daily")
    Daily,

    @SerialName("Every other day")
    EveryOtherDay,

    @SerialName("Once per week")
    OncePerWeek,

    @SerialName("Once per month")
    OncePerMonth,

    @SerialName("")
    None
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
    Accepted,

    /**
     * The user has been invited to observe the goal but has yet to accept it
     */
    @SerialName("Invitation sent")
    Invited,

    /**
     * The user has declined the invitation
     */
    @SerialName("Declined")
    Declined
}

/**
 * Defines a set of types that a entity can take
 */
@Suppress("unused")
@Serializable
enum class Type {
    @SerialName("file")
    File,

    @SerialName("app")
    App,

    @SerialName("domain")
    Domain
}

/**
 * Defines a set of categories that a heartbeat can have
 */
@Suppress("unused")
@Serializable
enum class Category {
    @SerialName("coding")
    Coding,

    @SerialName("building")
    Building,

    @SerialName("indexing")
    Indexing,

    @SerialName("debugging")
    Debugging,

    @SerialName("browsing")
    Browsing,

    @SerialName("writing docs")
    Documentation,

    @SerialName("code reviewing")
    CodeReview,

    @SerialName("researching")
    Researching,

    @SerialName("learning")
    Learning,

    @SerialName("designing")
    Designing,

    @SerialName("running tests")
    TestsRunning,

    @SerialName("writing tests")
    TestsWriting,

    @SerialName("manual testing")
    TestsManual
}

@Serializable
@Suppress("unused")
enum class ProcessingStatus {
    @SerialName("Pending…")
    Pending,

    @SerialName("Processing coding activity…")
    Processing,

    @SerialName("Uploading…")
    Uploading,

    @SerialName("Completed")
    Completed
}