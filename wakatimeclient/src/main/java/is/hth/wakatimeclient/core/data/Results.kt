package `is`.hth.wakatimeclient.core.data

/**
 * A discriminating wrapper that encapsulates operational results
 */
public sealed class Results<out T> {

    /**
     * The operation was deemed successful
     */
    public sealed class Success<T> : Results<T>() {

        /**
         * No resulting values were produced
         */
        public object Empty : Success<Nothing>()

        /**
         * Resulting values were produced
         */
        public class Value<T>(
            /**
             * The value resulting from the operation
             */
            public val value: T
        ) : Success<T>()
    }

    /**
     * A complete failure happened due to the indicated [Error]
     */
    public class Failure(
        /**
         * A description of what failed
         */
        public val error: Error
    ) : Results<Nothing>()
}
