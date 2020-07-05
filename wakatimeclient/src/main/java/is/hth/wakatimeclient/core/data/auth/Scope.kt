package `is`.hth.wakatimeclient.core.data.auth

/**
 * The available OAuth access scopes for Wakatime
 */
@Suppress("unused")
sealed class Scope(val name: String) {

    companion object {

        fun valueOf(name: String): Scope {
            return when(name){
                EMAIL.name -> EMAIL
                READ_LOGGED_TIME.name -> READ_LOGGED_TIME
                WRITE_LOGGED_TIME.name -> WRITE_LOGGED_TIME
                READ_STATS.name -> READ_STATS
                READ_ORGS.name -> READ_ORGS
                READ_PRIVATE_LEADERBOARDS.name -> READ_PRIVATE_LEADERBOARDS
                WRITE_PRIVATE_LEADERBOARDS.name -> WRITE_PRIVATE_LEADERBOARDS
                else -> throw IllegalArgumentException("Unknown scope name $name")
            }
        }
    }

    /**
     * Access user’s email and identity information
     */
    object EMAIL: Scope("email")
    /**
     * Access user’s coding activity and other stats.
     */
    object READ_LOGGED_TIME : Scope("read_logged_time")
    /**
     * Modify user’s coding activity
     */
    object WRITE_LOGGED_TIME: Scope("write_logged_time")
    /**
     * Access user’s languages, editors, and operating systems used
     */
    object READ_STATS: Scope("read_stats")
    /**
     * Access user’s organizations, and coding activity for dashboard members.
     */
    object READ_ORGS: Scope("read_orgs")
    /**
     * Access user’s private leaderboards.
     */
    object READ_PRIVATE_LEADERBOARDS: Scope("read_private_leaderboards")
    /**
     * Modify user’s private leaderboards, including adding/removing members when
     * current user had Admin or Owner role.
     */
    object WRITE_PRIVATE_LEADERBOARDS: Scope("write_private_leaderboards")
}