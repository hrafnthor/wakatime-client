package `is`.hth.wakatimeclient.core.data.auth

/**
 * The available OAuth access scopes for Wakatime
 */
@Suppress("unused")
sealed class Scope(val name: String) {

    companion object {

        val scopes: HashMap<String, Scope> by lazy {
            hashMapOf(
                Email.name to Email,
                ReadLoggedTime.name to ReadLoggedTime,
                WriteLoggedTime.name to WriteLoggedTime,
                ReadStats.name to ReadStats,
                ReadOrganization.name to ReadOrganization,
                ReadPrivateLeaderboards.name to ReadPrivateLeaderboards,
                WritePrivateLeaderboards.name to WritePrivateLeaderboards
            )
        }
    }

    /**
     * Access user’s email and identity information
     */
    object Email: Scope("email")
    /**
     * Access user’s coding activity and other stats.
     */
    object ReadLoggedTime : Scope("read_logged_time")
    /**
     * Modify user’s coding activity
     */
    object WriteLoggedTime: Scope("write_logged_time")
    /**
     * Access user’s languages, editors, and operating systems used
     */
    object ReadStats: Scope("read_stats")
    /**
     * Access user’s organizations, and coding activity for dashboard members.
     */
    object ReadOrganization: Scope("read_orgs")
    /**
     * Access user’s private leaderboards.
     */
    object ReadPrivateLeaderboards: Scope("read_private_leaderboards")
    /**
     * Modify user’s private leaderboards, including adding/removing members when
     * current user had Admin or Owner role.
     */
    object WritePrivateLeaderboards: Scope("write_private_leaderboards")
}