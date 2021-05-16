package `is`.hth.wakatimeclient.core.data.auth

/**
 * The available OAuth access scopes for Wakatime
 */
@Suppress("unused")
public enum class Scope(
    /**
     * The scope's unique descriptive value
     */
    private val value: String
) {

    /**
     * Access user’s email and identity information
     */
    Email("email"),

    /**
     * Access user’s coding activity and other stats.
     */
    ReadLoggedTime("read_logged_time"),

    /**
     * Modify user’s coding activity
     */
    WriteLoggedTime("write_logged_time"),

    /**
     * Access user’s languages, editors, and operating systems used
     */
    ReadStats("read_stats"),

    /**
     * Access user’s organizations, and coding activity for dashboard members.
     */
    ReadOrganization("read_orgs"),

    /**
     * Access user’s private leaderboards.
     */
    ReadPrivateLeaderboards("read_private_leaderboards"),

    /**
     * Modify user’s private leaderboards, including adding/removing members when
     * current user had Admin or Owner role.
     */
    WritePrivateLeaderboards("write_private_leaderboards");

    override fun toString(): String = value

    public companion object {
        private val map = values().associateBy(Scope::value)

        public fun convert(description: String): Scope =
            map[description] ?: throw IllegalArgumentException("Unknown scope $description")

        /**
         * Attempts to parse any scope values that might be in the input and convert them to [Scope]s
         */
        public fun extractScopes(value: String): Set<Scope> =
            map.values.filter { value.contains(it.value, ignoreCase = true) }.toSet()

    }
}