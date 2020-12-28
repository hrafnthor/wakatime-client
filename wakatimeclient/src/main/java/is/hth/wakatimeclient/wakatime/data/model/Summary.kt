package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.text.DateFormat
import java.text.SimpleDateFormat
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

        fun makeRequest(start: Calendar, end: Calendar): Request.Builder {
            return Request.Builder(start, end)
        }
    }

    /**
     * Utility class for network request making for [Summaries]. Call [Summaries.makeRequest]
     * to make a new request
     *
     * @param start Required. The start date of the time range in 'yyyy-MM-dd' format.
     * @param end Required. The end date of the time range in 'yyyy-MM-dd' format.
     * @param writesOnly Optional. If only writes should be returned. Defaults to user's 'writes only' preference
     * @param timeout Optional. The timeout preference used when joining heartbeats into durations. Defaults
     * to the user's timeout value.
     * @param projectName Optional. Filter the summaries to only those related to this project.
     * @param timezone Optional. The timezone for the given start and end dates. Defaults to the user's timezone.
     * @param branches Optional. Filter the summaries to only those related to these branch names.
     */
    class Request private constructor(
        val start: String,
        val end: String,
        val writesOnly: Boolean?,
        val timezone: String?,
        val timeout: Int?,
        val projectName: String?,
        val branches: String?
    ) {

        @Suppress("unused")
        class Builder internal constructor(start: Calendar, end: Calendar) {

            private var start: String = format(start)
            private var end: String = format(end)
            private var writesOnly: Boolean? = null
            private var timeout: Int? = null
            private var projectName: String? = null
            private var timezone: String? = null
            private var branches: String? = null

            fun setStart(start: Calendar): Builder = apply {
                this.start = format(start)
            }

            fun setEnd(end: Calendar): Builder = apply {
                this.end = format(end)
            }

            /**
             * Assigns the list of branches to filter summaries for. Branch filtering will
             * only work if a [projectName] has also been assigned to the request.
             */
            fun setBranches(vararg branches: String?): Builder = apply {
                this.branches = branches
                    .filterNotNull()
                    .joinToString(separator = ",") { it }
            }

            fun setWritesOnly(writesOnly: Boolean?): Builder = apply {
                this.writesOnly = writesOnly
            }

            fun setTimeout(timeout: Int?): Builder = apply {
                this.timeout = timeout
            }

            fun setProjectName(projectName: String?): Builder = apply {
                this.projectName = projectName
            }

            fun setTimezone(timezone: String?): Builder = apply {
                this.timezone = timezone
            }

            /**
             * Constructs a new [Request]
             */
            fun build(): Request = Request(
                start = start,
                end = end,
                writesOnly = writesOnly,
                timezone = timezone,
                timeout = timeout,
                projectName = projectName,
                branches = branches
            )

            private fun format(cal: Calendar): String = format.format(cal.time)

            private companion object {
                private val format: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            }
        }
    }
}
