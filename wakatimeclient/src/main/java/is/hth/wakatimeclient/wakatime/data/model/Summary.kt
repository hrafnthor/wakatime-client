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
    // TODO: 13.11.2020 Figure out what the dependencies field revolves around
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

        inline fun makeRequest(
            startDate: Calendar,
            endDate: Calendar,
            construct: Request.Builder.() -> Unit = {}
        ) = Request.Builder(
            startDate = startDate,
            endDate = endDate
        ).also(construct).build()

        inline fun makeDashboardRequest(
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
     * @param startDate Required. The requested start date for the request encoded as a Calendar
     * @param endDate Required. The end date of the time range in 'yyyy-MM-dd' format.
     * @param timezone Optional. The timezone for the given start and end dates. Defaults to the user's timezone.
     * @param metaFilter Optional.
     * @param projectFilter Optiona.
     */
    class Request(
        val startDate: Calendar,
        val endDate: Calendar,
        val timezone: String?,
        val metaFilter: MetaFilter?,
        val projectFilter: ProjectFilter?,
    ) {
        @RequestDsl
        @Suppress("unused")
        class Builder(
            private var startDate: Calendar,
            private var endDate: Calendar,
            private var timezone: String? = null,
            private var metaFilter: MetaFilter? = null,
            private var projectFilter: ProjectFilter? = null,
        ) {
            fun timezone(timezone: String?) = apply { this.timezone = timezone }
            fun startDate(startDate: Calendar) = apply { this.startDate = startDate }
            fun endDate(endDate: Calendar) = apply { this.endDate = endDate }
            fun metaFilter(metaFilter: MetaFilter?) = apply { this.metaFilter = metaFilter }

            fun projectFilter(projectFilter: ProjectFilter) = apply {
                this.projectFilter = projectFilter
            }

            fun build(): Request = Request(
                startDate = startDate,
                endDate = endDate,
                timezone = timezone,
                metaFilter = metaFilter,
                projectFilter = projectFilter
            )
        }
    }

    /**
     * Utility class for network request making for [Summaries].
     *
     * @param startDate Required. The requested start date for the request encoded as a Calendar
     * @param endDate Required. The end date of the time range in 'yyyy-MM-dd' format.
     * @param projectFilter Optional.
     */
    class DashboardRequest(
        val organizationId: String,
        val dashboardId: String,
        val userId: String,
        val startDate: Calendar,
        val endDate: Calendar,
        val projectFilter: ProjectFilter? = null
    ) {
        @RequestDsl
        @Suppress("unused")
        class Builder(
            private var organizationId: String,
            private var dashboardId: String,
            private var userId: String,
            private var startDate: Calendar,
            private var endDate: Calendar,
            private var projectFilter: ProjectFilter? = null
        ) {
            fun organizationId(organizationId: String) =
                apply { this.organizationId = organizationId }

            fun dashboardId(dashboardId: String) = apply { this.dashboardId = dashboardId }
            fun userId(userId: String) = apply { this.userId = userId }
            fun startDate(startDate: Calendar) = apply { this.startDate = startDate }
            fun endDate(endDate: Calendar) = apply { this.endDate = endDate }
            fun projectFilter(projectFilter: ProjectFilter?) = apply {
                this.projectFilter = projectFilter
            }

            fun build() = DashboardRequest(
                organizationId,
                dashboardId,
                userId,
                startDate,
                endDate,
                projectFilter
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
    metaFilter(builder.build())
}

@Suppress("unused")
inline fun Summaries.Request.Builder.project(
    filter: ProjectFilter.Builder.() -> Unit
) {
    val builder = ProjectFilter.Builder()
    builder.filter()
    projectFilter(builder.build())
}

@Suppress("unused")
inline fun Summaries.DashboardRequest.Builder.project(
    filter: ProjectFilter.Builder.() -> Unit
) {
    val builder = ProjectFilter.Builder()
    builder.filter()
    projectFilter(builder.build())
}