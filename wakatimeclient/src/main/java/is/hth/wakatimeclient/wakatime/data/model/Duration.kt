package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class ExternalDuration internal constructor(
    /**
     * A unique id of this external duration
     */
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
    val project: String = "",
    /**
     * The branch this duration was logged against, if any was given
     */
    val branch: String = "",
    /**
     *  The language this duration was logged against, if any was given
     */
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
    class Request(date: Calendar) {

        private val format: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        val date: String = format.format(date.time)
        var testing: String? = null
            private set
        var projectName: String? = null
            private set
        var branches: String? = null
            private set
        var timezone: String? = null
            private set

        fun setProjectName(projectName: String?): Request = apply {
            this.projectName = projectName
        }

        fun setTimezone(timezone: String?): Request = apply {
            this.timezone = timezone
        }

        /**
         * Assigns the list of branches to filter summaries for. Branch filtering will
         * only work if a [projectName] has also been assigned to the request.
         */
        fun setBranches(projectName: String?, vararg branches: String?): Request = apply {
            this.projectName = projectName
            if (projectName != null) {
                this.branches = branches
                    .filterNotNull()
                    .joinToString(separator = ",") { it }
            }
        }
    }

    @Serializable
    @Suppress("unused")
    class Payload(
        /**
         * @see ExternalDuration.externalId
         */
        @SerialName("external_id")
        val externalId: String,
        /**
         * @see ExternalDuration.entity
         */
        val entity: String,
        /**
         * @see ExternalDuration.type
         */
        val type: Type,
        /**
         * @see ExternalDuration.category
         */
        val category: Category,
        /**
         * @see ExternalDuration.startTime
         */
        @SerialName("start_time")
        val startTime: Float,
        /**
         * @see ExternalDuration.endTime
         */
        @SerialName("end_time")
        val endTime: Float
    ) {

        /**
         * @see ExternalDuration.project
         */
        var projectName: String? = null
            private set

        /**
         * @see ExternalDuration.branch
         */
        var branch: String? = null
            private set

        /**
         *  @see ExternalDuration.language
         */
        var language: String? = null
            private set

        fun setProjectName(projectName: String?): Payload = apply {
            this.projectName = projectName
        }

        fun setBranch(projectName: String?, branch: String?): Payload = apply {
            this.projectName = projectName
            if (projectName != null) {
                this.branch = branch
            }
        }

        fun setLanguage(language: String?): Payload = apply {
            this.language = language
        }
    }
}