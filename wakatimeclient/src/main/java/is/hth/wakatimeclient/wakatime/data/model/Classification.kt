package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Defines a set of types that a entity can take
 */
@Suppress("unused")
@Serializable
enum class Type {
    @SerialName("file")
    FILE,

    @SerialName("app")
    APP,

    @SerialName("domain")
    DOMAIN
}

/**
 * Defines a set of categories that a heartbeat can have
 */
@Suppress("unused")
@Serializable
enum class Category {
    @SerialName("coding")
    CODING,

    @SerialName("building")
    BUILDING,

    @SerialName("indexing")
    INDEXING,

    @SerialName("debugging")
    DEBUGGING,

    @SerialName("browsing")
    BROWSING,

    @SerialName("writing docs")
    DOCUMENTATION,

    @SerialName("code reviewing")
    CODEREVIEW,

    @SerialName("researching")
    RESEARCHING,

    @SerialName("learning")
    LEARNING,

    @SerialName("designing")
    DESIGNING,

    @SerialName("running tests")
    TESTS_RUNNING,

    @SerialName("writing tests")
    TESTS_WRITING,

    @SerialName("manual testing")
    TESTS_MANUAL
}