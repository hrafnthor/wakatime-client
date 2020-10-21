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
         * Values were produced, but potentially an error was as well such as
         * in the case when network fetching has failed and only cached values
         * were returned.
         */
        class Values<T>(val data: T, val error: Error? = null) : Success<T>()
    }

    /**
     * A complete failure happened due to the indicated [Error]
     */
    class Failure(val error: Error) : Results<Nothing>()
}
