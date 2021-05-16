package `is`.hth.wakatimeclient.wakatime.data.model.filters

@DslMarker
public annotation class RequestDsl

/**
 * Defines project information specific filter for the associated request
 * @param projectName Optional. Filter the summaries to only those related to this project.
 * @param branches Optional. Filter the summaries to only those related to these branch names. Branch names are only used if the request also comes with a valid project name
 */
public class ProjectFilter(
    public val projectName: String? = null,
    public val branches: String? = null,
) {

    @RequestDsl
    @Suppress("unused")
    public class Builder(
        public var projectName: String? = null,
        private var branches: String? = null,
    ) {
        public fun branches(vararg branches: String?) {
            this.branches = branches.filterNotNull().joinToString(separator = ",") { it }
        }

        public fun build(): ProjectFilter = ProjectFilter(projectName, branches)
    }
}

/**
 * Defines meta filters for the associated request
 * @param writesOnly Optional. If only writes should be returned. Defaults to user's 'writes only' preference
 * @param timeout Optional. The timeout preference used when joining heartbeats into durations. Defaults the user's timeout value
 */
public class MetaFilter(
    public val timeout: Int? = null,
    public val writesOnly: Boolean? = null,
) {
    @RequestDsl
    @Suppress("unused")
    public class Builder(
        public var timeout: Int? = null,
        public var writesOnly: Boolean? = null,
    ) {
        public fun build(): MetaFilter = MetaFilter(timeout, writesOnly)
    }
}