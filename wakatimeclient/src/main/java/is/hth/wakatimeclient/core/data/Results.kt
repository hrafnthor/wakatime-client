package `is`.hth.wakatimeclient.core.data

/**
 * A discriminating wrapper that encapsulates operational results
 */
sealed class Results<out T> {

    /**
     * The operation was deemed successful
     */
    sealed class Success<T> : Results<T>() {

        /**
         * No resulting values were produced
         */
        object Empty : Success<Nothing>()

        /**
         * Resulting values were produced
         */
        class Values<T>(val data: T) : Success<T>()
    }

    /**
     * A complete failure happened due to the indicated [Error]
     */
    class Failure(val error: Error) : Results<Nothing>()
}
