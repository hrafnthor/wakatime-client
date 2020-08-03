package `is`.hth.wakatimeclient.core.data.auth

/**
 * The available OAuth access scopes for Wakatime
 */
@Suppress("unused")
sealed class Scope(val name: String) {

    companion object {

        fun valueOf(name: String): Scope {
            return when(name){
                Email.name -> Email
                ReadLoggedTime.name -> ReadLoggedTime
                WriteLoggedTime.name -> WriteLoggedTime
                ReadStats.name -> ReadStats
                ReadOrgs.name -> ReadOrgs
                ReadPrivateLeaderboards.name -> ReadPrivateLeaderboards
                WritePrivateLeaderboards.name -> WritePrivateLeaderboards
                else -> throw IllegalArgumentException("Unknown scope name $name")
            }
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
    object ReadOrgs: Scope("read_orgs")
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