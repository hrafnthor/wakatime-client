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

        inline fun request(
            date: Calendar,
            construct: Request.Builder.() -> Unit = {}
        ) = Request.Builder(date).also(construct).build()

        inline fun send(
            externalId: String,
            entity: String,
            type: Type,
            category: Category,
            startTime: Float,
            endTime: Float,
            construct: Builder.() -> Unit = {}
        ) = Builder(
            externalId = externalId,
            entity = entity,
            type = type,
            category = category,
            startTime = startTime,
            endTime = endTime,
        ).also(construct).build()
    }

    @RequestDsl
    @Suppress("unused")
    class Builder(
        private var externalId: String,
        private var entity: String,
        private var type: Type,
        private var category: Category,
        private var startTime: Float,
        private var endTime: Float,
        private var language: String? = null,
        private var projectFilter: ProjectFilter? = null,
    ) {
        fun externalId(externalId: String) = apply { this.externalId = externalId }
        fun entity(entity: String) = apply { this.entity = entity }
        fun type(type: Type) = apply { this.type = type }
        fun category(category: Category) = apply { this.category = category }
        fun startTime(startTime: Float) = apply { this.startTime = startTime }
        fun endTime(endTime: Float) = apply { this.endTime = endTime }
        fun language(language: String?) = apply { this.language = language }
        fun projectFilter(projectFilter: ProjectFilter?) =
            apply { this.projectFilter = projectFilter }

        fun build() = ExternalDuration(
            externalId = externalId,
            entity = entity,
            type = type,
            category = category,
            startTime = startTime,
            endTime = endTime,
            language = language ?: "",
            project = projectFilter?.projectName ?: "",
            branch = projectFilter?.branches ?: ""
        )
    }

    @Suppress("unused")
    class Request(
        val date: Calendar,
        val timezone: String?,
        val projectFilter: ProjectFilter?
    ) {
        @RequestDsl
        class Builder(
            private var date: Calendar,
            private var timezone: String? = null,
            private var projectFilter: ProjectFilter? = null,
        ) {
            fun timezone(timezone: String?) = apply { this.timezone = timezone }
            fun date(date: Calendar) = apply { this.date = date }
            fun projectFilter(projectFilter: ProjectFilter?) =
                apply { this.projectFilter = projectFilter }

            fun build() = Request(
                date = date,
                timezone = timezone,
                projectFilter = projectFilter
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
    projectFilter(builder.build())
}

@Suppress("unused")
inline fun ExternalDuration.Request.Builder.project(
    filter: ProjectFilter.Builder.() -> Unit
) {
    val builder = ProjectFilter.Builder()
    builder.filter()
    projectFilter(builder.build())
}