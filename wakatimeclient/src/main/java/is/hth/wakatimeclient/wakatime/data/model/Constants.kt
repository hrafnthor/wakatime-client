package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines the default visibility that projects can have when shared
 */
@Serializable
@Suppress("unused")
public enum class Visibility {
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
public enum class GoalStatus {

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
public enum class Delta {
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
public enum class Frequency(private val value: String) {
    @SerialName("Daily")
    Daily("Daily"),

    @SerialName("Every other day")
    EveryOtherDay("Every other day"),

    @SerialName("Once per week")
    OncePerWeek("Once per week"),

    @SerialName("Once per month")
    OncePerMonth("Once per month"),

    @SerialName("None")
    None("None");

    override fun toString(): String = value
}

/**
 * Predefined chronological range constants
 */
@Suppress("unused")
@Serializable
public enum class HumanRange(private val value: String) {

    /**
     * The last 7 days from today
     */
    @SerialName("last_7_days")
    WEEK("last_7_days"),

    /**
     * The last 30 days from today
     */
    @SerialName("last_30_days")
    MONTH("last_30_days"),

    /**
     * The last 6 months since today
     */
    @SerialName("last_6_months")
    HALF_YEAR("last_6_months"),

    /**
     * The last year from today
     */
    @SerialName("last_year")
    YEAR("last_year"),

    /**
     * All time on record
     */
    @SerialName("all_time")
    All("all_time");

    override fun toString(): String = value
}

@Serializable
@Suppress("unused")
public enum class DefaultRange(private val value: String) {
    @SerialName("Today")
    TODAY("Today"),

    @SerialName("Yesterday")
    YESTERDAY("Yesterday"),

    @SerialName("Last 7 Days")
    LAST_SEVEN_DAYS("Last 7 Days"),

    @SerialName("This Week")
    THIS_WEEK("This Week"),

    @SerialName("Last Week")
    LAST_WEEK("Last Week"),

    @SerialName("Last 7 Days from Yesterday")
    LAST_SEVEN_DAYS_FROM_YESTERDAY("Last 7 Days from Yesterday"),

    @SerialName("Last 14 Days")
    LAST_FOURTEEN_DAYS("Last 14 Days"),

    @SerialName("Last 30 Days")
    LAST_THIRTY_DAYS("Last 30 Days"),

    @SerialName("This Month")
    THIS_MONTH("This Month"),

    @SerialName("Last Month")
    LAST_MONTH("Last Month");

    override fun toString(): String = value
}

/**
 * Defines the statuses that an invitation to observe a goal can take
 */
@Serializable
@Suppress("unused")
public enum class InvitationStatus {

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
public enum class Type {
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
public enum class Category {
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
public enum class ExportStatus {
    @SerialName("Pending???")
    Pending,

    @SerialName("Processing coding activity???")
    Processing,

    @SerialName("Uploading???")
    Uploading,

    @SerialName("Completed")
    Completed
}

@Serializable
@Suppress("unused")
public enum class ProcessingStatus(private val value: String) {
    /**
     * There is processing pending server side, which hasn't yet started
     */
    @SerialName("pending_update")
    Pending("pending_update"),

    /**
     * There is processing happening server side
     */
    @SerialName("updating")
    Processing("updating"),

    /**
     * All processing has finished
     */
    @SerialName("ok")
    Done("ok");

    override fun toString(): String = value
}