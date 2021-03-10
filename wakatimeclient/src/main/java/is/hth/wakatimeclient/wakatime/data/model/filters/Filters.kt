package `is`.hth.wakatimeclient.wakatime.data.model.filters

@DslMarker
annotation class RequestDsl

/**
 * Defines project information specific filter for the associated request
 * @param projectName Optional. Filter the summaries to only those related to this project.
 * @param branches Optional. Filter the summaries to only those related to these branch names. Branch names are only used if the request also comes with a valid project name
 */
class ProjectFilter(
    val projectName: String? = null,
    val branches: String? = null,
) {

    @RequestDsl
    @Suppress("unused")
    class Builder(
        var projectName: String? = null,
        private var branches: String? = null,
    ) {
        fun branches(vararg branches: String?) {
            this.branches = branches.filterNotNull().joinToString(separator = ",") { it }
        }

        fun build() = ProjectFilter(projectName, branches)
    }
}

/**
 * Defines meta filters for the associated request
 * @param writesOnly Optional. If only writes should be returned. Defaults to user's 'writes only' preference
 * @param timeout Optional. The timeout preference used when joining heartbeats into durations. Defaults the user's timeout value
 */
class MetaFilter(
    val timeout: Int? = null,
    val writesOnly: Boolean? = null,
) {
    @RequestDsl
    @Suppress("unused")
    class Builder(
        var timeout: Int? = null,
        var writesOnly: Boolean? = null,
    ) {
        fun build() = MetaFilter(timeout, writesOnly)
    }
}