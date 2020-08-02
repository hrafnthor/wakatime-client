package `is`.hth.wakatimeclient.core.data

/**
 * A discriminating wrapper that encapsulates operational results
 */
sealed class Results<out T > {

    /**
     * No resulting values were produced, but potentially an error was
     */
    class Empty(val error: Error? = null) : Results<Nothing>()

    /**
     * Values were produced, but potentially an error was as well
     */
    class Values<out T>(val data: T, val error: Error? = null) : Results<T>()

    /**
     * A complete failure happened due to the indicated [Error]
     */
    class Failure(val error: Error) : Results<Nothing>()
}

