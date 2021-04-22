package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.wakatime.data.model.filters.MetaFilter
import `is`.hth.wakatimeclient.wakatime.data.model.filters.ProjectFilter
import `is`.hth.wakatimeclient.wakatime.data.model.filters.RequestDsl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

@Serializable
@Suppress("unused")
data class GrandTotal(
    /**
     * Hours portion of the coding activity
     */
    val hours: Int,
    /**
     * Minutes portion of the coding activity
     */
    val minutes: Int,
    /**
     * Total coding activity in as seconds
     */
    @SerialName("total_seconds")
    val totalSeconds: Double,
    /**
     *  total coding activity in digital clock format
     */
    val digital: String,
    /**
     * Total coding activity in human readable format
     */
    val text: String
)

@Serializable
@Suppress("unused")
open class Summary(
    /**
     * Hours portion of coding activity for this summary
     */
    val hours: Int,
    /**
     * Minutes portion of coding activity for this summary
     */
    val minutes: Int,
    /**
     * Seconds portion of coding activity for this summary
     */
    val seconds: Int,
    /**
     * Percent of total time that this summary represents
     */
    val percent: Double,
    /**
     * Total seconds that this summary represents
     */
    @SerialName("total_seconds")
    val totalSeconds: Double,
    /**
     * Total coding activity that this summary represents in digital clock format
     */
    val digital: String = "",
    /**
     * The name of the entity that this summary represents
     */
    val name: String = "",
    /**
     * Total coding activity that this summary represents in human readable format
     */
    val text: String = "",
)

@Serializable
@Suppress("unused")
data class MachineSummary(
    /**
     * Hours portion of coding activity for this summary
     */
    val hours: Int,
    /**
     * Minutes portion of coding activity for this summary
     */
    val minutes: Int,
    /**
     * Seconds portion of coding activity for this summary
     */
    val seconds: Int,
    /**
     * Percent of total time that this summary represents
     */
    val percent: Double,
    /**
     * Total seconds that this summary represents
     */
    @SerialName("total_seconds")
    val totalSeconds: Double,
    /**
     * Total coding activity that this summary represents in digital clock format
     */
    val digital: String = "",
    /**
     * The name of the entity that this summary represents
     */
    val name: String = "",
    /**
     * Total coding activity that this summary represents in human readable format
     */
    val text: String = "",
    /**
     * The unique id of this machine
     */
    @SerialName("machine_name_id")
    val machineNameId: String = ""
)


/**
 * Contains a user's coding activity for the given time range as an array
 * of summaries segmented by day.
 */
@Serializable
@Suppress("unused")
data class DailySummary(
    /**
     * Summary grand total information for the user's
     * activity over the requested range
     */
    @SerialName("grand_total")
    val grandTotal: GrandTotal,
    /**
     * Summary information by activity categories
     */
    val categories: List<Summary>,
    /**
     * Summary information by dependencies
     */
    val dependencies: List<Summary>,
    /**
     * Summary information by editors used
     */
    val editors: List<Summary>,
    /**
     * Summary information by programming languages
     */
    val languages: List<Summary>,
    /**
     * Summary information by unique machines
     */
    val machines: List<MachineSummary>,
    /**
     * Summary information by operating systems
     */
    @SerialName("operating_systems")
    val operatingSystems: List<Summary>,
    /**
     * Summary information by projects. Will be empty if querying by a specific project
     */
    @Transient
    val projects: List<Summary> = emptyList(),
    /**
     * Summary information for project branches. Will always be empty when not querying
     * for a specific project.
     */
    @Transient
    val branches: List<Summary> = emptyList(),
    /**
     * Summary information for each individual entity within a project. Will always be
     * empty when not querying for a specific project.
     */
    @Transient
    val entities: List<Summary> = emptyList(),
    /**
     * Calendar range for these summaries
     */
    val range: Range
)

@Serializable
@Suppress("unused")
data class Summaries(
    /**
     * The available branches for the request project, if any. Will always be
     * empty if no project filtering is made.
     */
    @Transient
    @SerialName("available_branches")
    val availableBranches: List<String> = emptyList(),
    /**
     * The currently selected branches for the requested project, if any. Will
     * always be empty if no project filtering is made.
     */
    @Transient
    @SerialName("branches")
    val selectedBranches: List<String> = emptyList(),
    /**
     * The summaries for the request made, segmented by days
     */
    @SerialName("data")
    val summaries: List<DailySummary>,
    /**
     * The start of the summary period returned
     */
    val start: String,
    /**
     * The end of the summary period returned
     */
    val end: String
) {

    companion object {

        /**
         * Make a request for [Summaries] for the currently authenticated user
         */
        inline fun request(
            startDate: Calendar,
            endDate: Calendar,
            construct: Request.Builder.() -> Unit = {}
        ) = Request.Builder(
            startDate = startDate,
            endDate = endDate
        ).also(construct).build()

        /**
         * Make a request for [Summaries] for a specific user on a specific dashboard
         */
        inline fun requestDashboard(
            organizationId: String,
            dashboardId: String,
            userId: String,
            startDate: Calendar,
            endDate: Calendar,
            construct: DashboardRequest.Builder.() -> Unit = {}
        ) = DashboardRequest.Builder(
            organizationId = organizationId,
            dashboardId = dashboardId,
            userId = userId,
            startDate = startDate,
            endDate = endDate
        ).also(construct).build()
    }

    /**
     * Utility class for network request making for [Summaries].
     *
     * @param startDate the start date of the range used in the request
     * @param endDate the end date of the range used in the request
     * @param timezone (optional) The timezone for the given start and end dates. Defaults to the user's timezone.
     * @param meta (optional) meta related filtering options
     * @param project (optional) project related filtering options
     */
    class Request(
        val startDate: Calendar,
        val endDate: Calendar,
        val timezone: String?,
        val meta: MetaFilter?,
        val project: ProjectFilter?,
    ) {

        /**
         * @param startDate the start date of the range used in the request
         * @param endDate the end date of the range used in the request
         * @param timezone (optional) The timezone for the given start and end dates. Defaults to the user's timezone.
         * @param meta (optional) meta related filtering options
         * @param project (optional) project related filtering options
         */
        @RequestDsl
        @Suppress("unused")
        class Builder(
            var startDate: Calendar,
            var endDate: Calendar,
            var timezone: String? = null,
            var meta: MetaFilter? = null,
            var project: ProjectFilter? = null,
        ) {
            fun build(): Request = Request(
                startDate = startDate,
                endDate = endDate,
                timezone = timezone,
                meta = meta,
                project = project
            )
        }
    }

    /**
     * Utility class for network request making for [Summaries].
     *
     * @param organizationId the unique id of the organization that owns the dashboard
     * @param dashboardId the unique id of the dashboard being requested
     * @param userId the unique id of the user who's dashboard activity is being requested
     * @param startDate the start date of the range used in the request
     * @param endDate the end date of the range used in the request
     * @param project (optional) project related filtering options
     */
    class DashboardRequest(
        val organizationId: String,
        val dashboardId: String,
        val userId: String,
        val startDate: Calendar,
        val endDate: Calendar,
        val project: ProjectFilter?,
    ) {
        /**
         * @param organizationId the unique id of the organization that owns the dashboard
         * @param dashboardId the unique id of the dashboard being requested
         * @param userId the unique id of the user who's dashboard activity is being requested
         * @param startDate the start date of the range used in the request
         * @param endDate the end date of the range used in the request
         * @param project (optional) project related filtering options
         */
        @RequestDsl
        @Suppress("unused")
        class Builder(
            var organizationId: String,
            var dashboardId: String,
            var userId: String,
            var startDate: Calendar,
            var endDate: Calendar,
            var project: ProjectFilter? = null
        ) {
            fun build() = DashboardRequest(
                organizationId,
                dashboardId,
                userId,
                startDate,
                endDate,
                project
            )
        }
    }
}

@Suppress("unused")
inline fun Summaries.Request.Builder.meta(
    filter: MetaFilter.Builder.() -> Unit
) {
    val builder = MetaFilter.Builder()
    builder.filter()
    meta = builder.build()
}

@Suppress("unused")
inline fun Summaries.Request.Builder.project(
    filter: ProjectFilter.Builder.() -> Unit
) {
    val builder = ProjectFilter.Builder()
    builder.filter()
    project = builder.build()
}

@Suppress("unused")
inline fun Summaries.DashboardRequest.Builder.project(
    filter: ProjectFilter.Builder.() -> Unit
) {
    val builder = ProjectFilter.Builder()
    builder.filter()
    project = builder.build()
}