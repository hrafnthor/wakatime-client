package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.wakatime.data.model.filters.ProjectFilter
import `is`.hth.wakatimeclient.wakatime.data.model.filters.RequestDsl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

@Serializable
data class ExternalDuration internal constructor(
    /**
     * A unique id of this external duration
     */
    @Transient
    val id: String = "",
    /**
     * A unique identifier for this duration on the external provider
     */
    @SerialName("external_id")
    val externalId: String = "",
    /**
     * The entity that this duration is logging time against, such as an absolute file path or domain
     */
    val entity: String = "",
    /**
     *  The external app which created this activity
     */
    val provider: String = "",
    /**
     * The project name this duration was logged against, if any was given
     */
    @Transient
    val project: String = "",
    /**
     * The branch this duration was logged against, if any was given
     */
    @Transient
    val branch: String = "",
    /**
     *  The language this duration was logged against, if any was given
     */
    @Transient
    val language: String = "",
    /**
     * The type for this activity
     */
    val type: Type,
    /**
     * The category for this activity
     */
    val category: Category,
    /**
     * UNIX epoch timestamp; numbers after decimal point are fractions of a second
     */
    @SerialName("start_time")
    val startTime: Float,
    /**
     * UNIX epoch timestamp; numbers after decimal point are fractions of a second
     */
    @SerialName("end_time")
    val endTime: Float
) {

    @Suppress("unused")
    companion object {

        /**
         * Make an [ExternalDuration] request for the authenticated user
         *
         * @param date for which to query durations for
         * @param construct for further configuration of the request's optional values
         */
        inline fun request(
            date: Calendar,
            construct: Request.Builder.() -> Unit = {}
        ): Request = Request.Builder(date).also(construct).build()

        /**
         * Make an [ExternalDuration] payload for delivering new information to Wakatime
         *
         * @param externalId a unique identifier for this duration on the external provider
         * @param entity that this duration is logging time against, such as an absolute file path or domain
         * @param type for the logged activity
         * @param category for the logged activity
         * @param startTime UNIX epoch timestamp; numbers after decimal point are fractions of a second
         * @param endTime UNIX epoch timestamp; numbers after decimal point are fractions of a second
         * @param construct for further configuration of the duration's optional values
         */
        inline fun send(
            externalId: String,
            entity: String,
            type: Type,
            category: Category,
            startTime: Float,
            endTime: Float,
            construct: Builder.() -> Unit = {}
        ): ExternalDuration = Builder(
            externalId = externalId,
            entity = entity,
            type = type,
            category = category,
            startTime = startTime,
            endTime = endTime,
        ).also(construct).build()
    }

    /**
     * @param externalId a unique identifier for this duration on the external provider
     * @param entity that this duration is logging time against, such as an absolute file path or domain
     * @param type for the logged activity
     * @param category for the logged activity
     * @param startTime UNIX epoch timestamp; numbers after decimal point are fractions of a second
     * @param endTime UNIX epoch timestamp; numbers after decimal point are fractions of a second
     * @param language (optional) the language this duration was logged against, if any
     * @param project (optional) defines the project specific filtering for the duration, if any
     */
    @RequestDsl
    @Suppress("unused")
    class Builder(
        var externalId: String,
        var entity: String,
        var type: Type,
        var category: Category,
        var startTime: Float,
        var endTime: Float,
        var language: String? = null,
        var project: ProjectFilter? = null,
    ) {
        fun build() = ExternalDuration(
            externalId = externalId,
            entity = entity,
            type = type,
            category = category,
            startTime = startTime,
            endTime = endTime,
            language = language ?: "",
            project = project?.projectName ?: "",
            branch = project?.branches ?: ""
        )
    }

    /**
     * Utility class for creation requests for [ExternalDuration]s
     *
     * @param date for which to query durations for
     * @param timezone (optional) for the given date. If none, defaults to the user's default timezone
     * @param project (optional) defines the project specific filtering for the duration, if any
     */
    @Suppress("unused")
    class Request(
        val date: Calendar,
        val timezone: String?,
        val project: ProjectFilter?
    ) {

        /**
         * @param date for which to query durations for
         * @param timezone (optional) for the given date. If none, defaults to the user's default timezone
         * @param project (optional) defines the project specific filtering for the duration, if any
         */
        @RequestDsl
        class Builder(
            var date: Calendar,
            var timezone: String? = null,
            var project: ProjectFilter? = null,
        ) {
            fun build() = Request(
                date = date,
                timezone = timezone,
                project = project
            )
        }
    }
}

@Suppress("unused")
inline fun ExternalDuration.Builder.project(
    filter: ProjectFilter.Builder.() -> Unit
) {
    val builder = ProjectFilter.Builder()
    builder.filter()
    project = builder.build()
}

@Suppress("unused")
inline fun ExternalDuration.Request.Builder.project(
    filter: ProjectFilter.Builder.() -> Unit
) {
    val builder = ProjectFilter.Builder()
    builder.filter()
    project = builder.build()
}