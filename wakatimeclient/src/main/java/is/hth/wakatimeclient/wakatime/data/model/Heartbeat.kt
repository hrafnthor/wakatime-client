package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A heartbeat representing some activity for a user
 */
@Serializable
data class Heartbeat internal constructor(
    /**
     * The current line row number of cursor, if applicable. If no value is received from the service,
     * then this value will be -1. If this value should not be sent to the service then
     * it should be set as -1.
     *
     * Defaults to -1.
     */
    val cursorpos: Int = -1,
    /**
     * The current cursor column position, if applicable. If no value is received from the service,
     * then this value will be -1. If this value should not be sent to the service then
     * it should be set as -1
     *
     * Defaults to -1.
     */
    val lineno: Int = -1,
    /**
     * The total number of lines in the entity (when entity type is file)
     */
    val lines: Int = 0,
    /**
     * UNIX epoch timestamp; numbers after decimal point are fractions of a second
     */
    val time: Float,
    /**
     *
     */
    @SerialName("is_write")
    val isWrite: Boolean,
    /**
     * The unique id of this heartbeat
     */
    val id: String = "",
    /**
     * The unique id of the machine that this heartbeat came from
     */
    @SerialName("machine_name_id")
    val machineId: String = "",
    /**
     * The project name, if any. If no value is received from the service,
     * then this value will be the empty string. If this value should not be sent to the service then
     * it should be set to the empty string.
     *
     * Defaults to the empty string.
     */
    val project: String = "",
    /**
     * The language used, if any. If no value is received from the service,
     * then this value will be the empty string. If this value should not be sent to the service then
     * it should be set to the empty string.
     *
     * Defaults to the empty string.
     */
    val language: String = "",
    /**
     * The branch used, if any. If no value is received from the service,
     * then this value will be the empty string. If this value should not be sent to the service then
     * it should be set to the empty string.
     *
     * Defaults to the empty string.
     */
    val branch: String = "",
    /**
     * Entity heartbeat is logging time against, such as an absolute file path or domain
     */
    val entity: String = "",
    /**
     * Time of the creation of the heartbeat
     */
    @SerialName("created_at")
    val createdAt: String = "",
    /**
     * The user's unique id
     */
    @SerialName("user_id")
    val userId: String = "",
    /**
     * The originating agent's unique id
     */
    @SerialName("user_agent_id")
    val userAgentId: String = "",
    /**
     * The category for this activity
     */
    val category: Category,
    /**
     * The type of the entity
     */
    val type: Type,
    /**
     * A list of dependencies detected from the entity file, if applicable
     */
    val dependencies: List<String> = emptyList()
) {

    /**
     * A network model for sending heartbeats to the server
     */
    @Serializable
    data class Beat internal constructor(
        /**
         * The entity that the beat is logging time against, such as an absolute file path or domain
         */
        val entity: String,
        /**
         * UNIX epoch timestamp; numbers after decimal point are fractions of a second
         */
        val time: Float,
        /**
         * (Optional) The project name
         */
        val project: String?,
        /**
         * (Optional) The branch name
         */
        val branch: String?,
        /**
         * (Optional) The language name.
         */
        val language: String?,
        /**
         * (Optional) Comma separated list of dependencies detected from entity file.
         */
        val dependencies: String?,
        /**
         * The total number of lines in the entity (when entity type is file)
         */
        val lines: Int?,
        /**
         * The current cursor column position, if applicable. If no value is received from the service,
         * then this value will be -1. If this value should not be sent to the service then
         * it should be set as -1
         *
         * Defaults to -1.
         */
        val lineno: Int?,
        /**
         * The current line row number of cursor, if applicable. If no value is received from the service,
         * then this value will be -1. If this value should not be sent to the service then
         * it should be set as -1.
         *
         * Defaults to -1.
         */
        val cursorpos: Int?,
        /**
         *
         */
        @SerialName("is_write")
        val isWrite: Boolean?,
        /**
         * The category for this beat
         */
        val category: Category,
        /**
         * The type of the beat
         */
        val type: Type,
    ) {

        @Suppress("unused")
        class Builder(
            private var entity: String,
            private var time: Float,
            private var type: Type,
            private var category: Category,
            private var project: String? = null,
            private var branch: String? = null,
            private var language: String? = null,
            private var dependencies: String? = null,
            private var lines: Int? = null,
            private var lineno: Int? = null,
            private var cursorpos: Int? = null,
            private var isWrite: Boolean? = null,
        ) {

            fun setProject(project: String?): Builder = apply { this.project = project }

            fun setBranch(branch: String?): Builder = apply { this.branch = branch }

            fun setLanguage(language: String?): Builder = apply { this.language = language }

            fun setDependencies(dependencies: Set<String>?): Builder = apply {
                this.dependencies = dependencies?.joinToString(separator = ",") { it }
            }

            fun setLines(lines: Int?): Builder = apply { this.lines = lines }

            fun setLineNo(lineno: Int?): Builder = apply { this.lineno = lineno }

            fun setCursorPosition(cursorpos: Int?): Builder = apply { this.cursorpos = cursorpos }

            fun setWrite(isWrite: Boolean?): Builder = apply { this.isWrite = isWrite }

            fun build(): Beat = Beat(
                entity = entity,
                time = time,
                project = project,
                branch = branch,
                language = language,
                dependencies = dependencies,
                lines = lines,
                lineno = lineno,
                cursorpos = cursorpos,
                isWrite = isWrite,
                type = type,
                category = category
            )
        }
    }

    @Suppress("unused")
    companion object {

        /**
         * Creates a new [Beat.Builder] for sending a heartbeat to the server
         *
         * @param entity The entity that the beat is logging time against, such as an absolute file path or domain
         * @param time epoch timestamp; numbers after decimal point are fractions of a second
         * @param type The type for this beat
         * @param category The category for this beat
         */
        inline fun makeABeat(
            entity: String,
            time: Float,
            type: Type,
            category: Category,
            construct: Beat.Builder.() -> Unit = {}
        ): Beat = Beat.Builder(entity, time, type, category).also(construct).build()
    }
}

/**
 * Confirmation of [Heartbeat] creation server side
 */
@Serializable
data class Confirmation(
    /**
     * The unique id of the newly created heartbeat
     */
    val id: String,
    /**
     * The entity that the heartbeat is logging time against, such as absolute file path or domain
     */
    val entity: String,
    /**
     * UNIX epoch timestamp. Numbers after decimal point are fractions of a second
     */
    val time: Float,
    /**
     * The type of the entity
     */
    val type: Type
)