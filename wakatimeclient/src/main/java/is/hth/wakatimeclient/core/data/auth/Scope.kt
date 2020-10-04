package `is`.hth.wakatimeclient.core.data.auth

/**
 * The available OAuth access scopes for Wakatime
 */
@Suppress("unused")
sealed class Scope(val name: String) {

    companion object {

        private val scopes: Set<Scope> = setOf(
            Email,
            ReadLoggedTime,
            WriteLoggedTime,
            ReadStats,
            ReadOrganization,
            ReadPrivateLeaderboards,
            WritePrivateLeaderboards
        )

        /**
         * Attempts to parse any scope values that might be in the input and convert them to [Scope]s
         */
        fun extractScopes(value: String): Set<Scope> {
            return mutableSetOf<Scope>().apply {
                scopes.forEach { scope ->
                    if (value.contains(other = scope.name, ignoreCase = true)) {
                        add(scope)
                    }
                }
            }
        }
    }

    /**
     * Access user’s email and identity information
     */
    object Email : Scope("email")

    /**
     * Access user’s coding activity and other stats.
     */
    object ReadLoggedTime : Scope("read_logged_time")

    /**
     * Modify user’s coding activity
     */
    object WriteLoggedTime : Scope("write_logged_time")

    /**
     * Access user’s languages, editors, and operating systems used
     */
    object ReadStats : Scope("read_stats")

    /**
     * Access user’s organizations, and coding activity for dashboard members.
     */
    object ReadOrganization : Scope("read_orgs")

    /**
     * Access user’s private leaderboards.
     */
    object ReadPrivateLeaderboards : Scope("read_private_leaderboards")

    /**
     * Modify user’s private leaderboards, including adding/removing members when
     * current user had Admin or Owner role.
     */
    object WritePrivateLeaderboards : Scope("write_private_leaderboards")
}