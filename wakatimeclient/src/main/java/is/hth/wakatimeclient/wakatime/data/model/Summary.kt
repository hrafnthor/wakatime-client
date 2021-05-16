package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.findValue
import `is`.hth.wakatimeclient.wakatime.data.model.filters.MetaFilter
import `is`.hth.wakatimeclient.wakatime.data.model.filters.ProjectFilter
import `is`.hth.wakatimeclient.wakatime.data.model.filters.RequestDsl
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*
import java.util.*

@Serializable
@Suppress("unused")
public data class GrandTotal internal constructor(
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
public data class Summary internal constructor(
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
public data class MachineSummary internal constructor(
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
public data class DailySummary internal constructor(
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

@Suppress("unused")
@Serializable(SummariesJsonTransformer::class)
public data class Summaries internal constructor(
    /**
     * The available branches for the request project, if any. Will always be
     * empty if no project filtering is made.
     */
    @Transient
    @SerialName(AVAILABLE_BRANCHES)
    val availableBranches: List<String> = emptyList(),
    /**
     * The currently selected branches for the requested project, if any. Will
     * always be empty if no project filtering is made.
     */
    @Transient
    @SerialName(SELECTED_BRANCHES)
    val selectedBranches: List<String> = emptyList(),
    /**
     * The summaries for the request made, segmented by days
     */
    @SerialName(SUMMARIES)
    val summaries: List<DailySummary>,
    /**
     * The range over which the summaries go
     */
    @SerialName(RANGE)
    val range: Range
) {
    public companion object {
        internal const val AVAILABLE_BRANCHES = "available_branches"
        internal const val SELECTED_BRANCHES = "branches"
        internal const val END = "end"
        internal const val START = "start"
        internal const val SUMMARIES = "data"
        internal const val RANGE = "range"

        /**
         * Make a request for [Summaries] for the currently authenticated user
         */
        public inline fun request(
            startDate: Calendar,
            endDate: Calendar,
            construct: Request.Builder.() -> Unit = {}
        ): Request = Request.Builder(
            startDate = startDate,
            endDate = endDate
        ).also(construct).build()

        /**
         * Make a request for [Summaries] for a specific user on a specific dashboard
         */
        public inline fun requestDashboard(
            organizationId: String,
            dashboardId: String,
            userId: String,
            startDate: Calendar,
            endDate: Calendar,
            construct: DashboardRequest.Builder.() -> Unit = {}
        ): DashboardRequest = DashboardRequest.Builder(
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
    public class Request(
        public val startDate: Calendar,
        public val endDate: Calendar,
        public val timezone: String?,
        public val meta: MetaFilter?,
        public val project: ProjectFilter?,
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
        public class Builder(
            public var startDate: Calendar,
            public var endDate: Calendar,
            public var timezone: String? = null,
            public var meta: MetaFilter? = null,
            public var project: ProjectFilter? = null,
        ) {
            public fun build(): Request = Request(
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
    public class DashboardRequest(
        public val organizationId: String,
        public val dashboardId: String,
        public val userId: String,
        public val startDate: Calendar,
        public val endDate: Calendar,
        public val project: ProjectFilter?,
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
        public class Builder(
            public var organizationId: String,
            public var dashboardId: String,
            public var userId: String,
            public var startDate: Calendar,
            public var endDate: Calendar,
            public var project: ProjectFilter? = null
        ) {
            public fun build(): DashboardRequest = DashboardRequest(
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

/**
 * Modifies the incoming payload by moving date range values into a [Range]
 * object for more comfortable consumption
 */
internal object SummariesJsonTransformer : JsonTransformingSerializer<Summaries>(
    SummariesSerializer
) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return when {
            element is JsonObject && element.containsKey(Summaries.RANGE).not() -> buildJsonObject {
                findValue(this, element, Summaries.AVAILABLE_BRANCHES) { key ->
                    put(key, JsonArray(emptyList()))
                }
                findValue(this, element, Summaries.SELECTED_BRANCHES) { key ->
                    put(key, JsonArray(emptyList()))
                }
                findValue(this, element, Summaries.SUMMARIES) { key ->
                    put(key, JsonArray(emptyList()))
                }
                val start = element[Summaries.START] ?: JsonPrimitive("")
                val end = element[Summaries.END] ?: JsonPrimitive("")
                put(Summaries.RANGE, buildJsonObject {
                    put(Range.START, start)
                    put(Range.END, end)
                })
            }
            element is JsonObject -> element
            else -> throw IllegalArgumentException(
                "Incorrect JsonElement received for Summaries deserialization!"
            )
        }
    }
}

internal object SummariesSerializer : KSerializer<Summaries> {

    private val rangeSerializer = Range.serializer()
    private val listStringSerializer = ListSerializer(String.serializer())
    private val listDailySummarySerializer = ListSerializer(DailySummary.serializer())

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("summaries") {
            element(Summaries.AVAILABLE_BRANCHES, listStringSerializer.descriptor)
            element(Summaries.SELECTED_BRANCHES, listStringSerializer.descriptor)
            element(Summaries.SUMMARIES, listDailySummarySerializer.descriptor)
            element(Summaries.RANGE, rangeSerializer.descriptor)
        }

    @ExperimentalSerializationApi
    override fun deserialize(decoder: Decoder): Summaries {
        return decoder.decodeStructure(descriptor) {
            val available = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Summaries.AVAILABLE_BRANCHES),
                listStringSerializer
            )
            val selected = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Summaries.SELECTED_BRANCHES),
                listStringSerializer
            )
            val summaries = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Summaries.SUMMARIES),
                listDailySummarySerializer
            )
            val range = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Summaries.RANGE),
                rangeSerializer
            )
            Summaries(
                availableBranches = available,
                selectedBranches = selected,
                summaries = summaries,
                range = range
            )
        }
    }

    @ExperimentalSerializationApi
    override fun serialize(encoder: Encoder, value: Summaries) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Summaries.AVAILABLE_BRANCHES),
                listStringSerializer,
                value.availableBranches
            )
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Summaries.SELECTED_BRANCHES),
                listStringSerializer,
                value.selectedBranches
            )
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Summaries.SUMMARIES),
                listDailySummarySerializer,
                value.summaries
            )
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(Summaries.RANGE),
                rangeSerializer,
                value.range
            )
        }
    }
}

/**
 * Meta filtering dsl for [Summaries.Request] building
 */
@Suppress("unused")
public inline fun Summaries.Request.Builder.meta(
    filter: MetaFilter.Builder.() -> Unit
) {
    val builder = MetaFilter.Builder()
    builder.filter()
    meta = builder.build()
}

/**
 * Project filtering dsl for [Summaries.Request] building
 */
@Suppress("unused")
public inline fun Summaries.Request.Builder.project(
    filter: ProjectFilter.Builder.() -> Unit
) {
    val builder = ProjectFilter.Builder()
    builder.filter()
    project = builder.build()
}

/**
 * Project filtering dsl for [Summaries.DashboardRequest] building
 */
@Suppress("unused")
public inline fun Summaries.DashboardRequest.Builder.project(
    filter: ProjectFilter.Builder.() -> Unit
) {
    val builder = ProjectFilter.Builder()
    builder.filter()
    project = builder.build()
}