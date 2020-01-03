package `is`.hth.wakatimeclient.auth

/**
 * The available OAuth access scopes for Wakatime
 */
enum class Scopes {
    /**
     * Access user’s email and identity information
     */
    EMAIL,
    /**
     * Access user’s coding activity and other stats.
     */
    READ_LOGGED_TIME,
    /**
     * Modify user’s coding activity
     */
    WRITE_LOGGED_TIME,
    /**
     * Access user’s languages, editors, and operating systems used
     */
    READ_STATS,
    /**
     * Access user’s organizations, and coding activity for dashboard members.
     */
    READ_ORGS,
    /**
     * Access user’s private leaderboards.
     */
    READ_PRIVATE_LEADERBOARDS,
    /**
     * Modify user’s private leaderboards, including adding/removing members when
     * current user had Admin or Owner role.
     */
    WRITE_PRIVATE_LEADERBOARDS
}