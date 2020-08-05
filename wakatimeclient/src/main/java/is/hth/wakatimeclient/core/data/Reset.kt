package `is`.hth.wakatimeclient.core.data

/**
 * Indicates that the implementer can be reset to some initial state
 */
interface Reset {

    /**
     * Performs the resetting operation
     */
    suspend fun reset()
}

/**
 * The implementer is capable of delayed resetting
 */
interface Resettable {

    /**
     * The resetting implementation for a delayed resetting
     */
    fun getReset(): Reset
}