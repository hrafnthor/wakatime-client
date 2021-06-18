package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A heartbeat representing some activity for a user
 */
@Serializable
public data class Heartbeat internal constructor(
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
     * The entity that the beat is logging time against, such as an absolute file path or domain
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
    public data class Beat internal constructor(
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
         * The total number of lines in the entity (when type is [Type.File])
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

        /**
         * @param entity the entity that the beat is logging time against, such as an absolute file path or domain
         * @param time UNIX epoch timestamp; numbers after decimal point are fractions of a second
         * @param type the type of entity that the beat is being sent for
         * @param category of work that the beat is for
         * @param project (optional) the name of the project being worked on
         * @param branch (optional) the name of the branch being worked on
         * @param language (optional) the language name being used
         * @param lines (optional) the total number of lines in the entity (when the type is [Type.File]])
         * @param lineno (optional) the current cursor column position, if applicable.
         * @param cursorpos (optional) the current line row number of cursor, if applicable.
         * @param isWrite (optional)
         */
        @Suppress("unused")
        public class Builder(
            public var entity: String,
            public var time: Float,
            public var type: Type,
            public var category: Category,
            public var project: String? = null,
            public var branch: String? = null,
            public var language: String? = null,
            public var lines: Int? = null,
            public var lineno: Int? = null,
            public var cursorpos: Int? = null,
            public var isWrite: Boolean? = null,
            private var dependencies: String? = null,
        ) {

            /**
             * A list of dependencies detected from the entity file, if applicable.
             */
            public fun dependencies(vararg dependencies: String?): Builder = apply {
                this.dependencies = dependencies.filterNotNull().joinToString(separator = ",") { it }
            }

            public fun build(): Beat = Beat(
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
    public companion object {

        /**
         * Creates a new [Beat] for sending a heartbeat to the server
         *
         * @param entity The entity that the beat is logging time against, such as an absolute file path or domain
         * @param time epoch timestamp; numbers after decimal point are fractions of a second
         * @param type The type for this beat
         * @param category The category for this beat
         */
        public inline fun send(
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
public data class Confirmation(
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