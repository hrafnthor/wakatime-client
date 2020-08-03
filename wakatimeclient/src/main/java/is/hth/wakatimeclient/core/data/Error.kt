package `is`.hth.wakatimeclient.core.data

sealed class Error(val message: String) {

    /**
     * A network layer error occurred.
     */
    sealed class Network(message: String = "") : Error(message) {
        /**
         * No network access was found
         */
        object NoAccess : Network()

        /**
         * Authentication is not present or has expired
         */
        object AccessDenied : Network()

        /**
         * A 404 Not Found error occurred
         */
        object NotFound : Network()

        /**
         * The host's IP address could not be determined
         */
        object UnknownHost : Network()

        /**
         * A 503 Service Unavailable error occurred
         */
        object Unavailable : Network()

        /**
         * Either a 504 Gateway Timeout, a 408 Client Timeout or a
         * socket timeout exception error occurred
         */
        object Timeout : Network()

        /**
         * A 400 Bad Request error occurred
         */
        object BadRequest : Network()

        /**
         * Serialization of what ever payload was being processed failed
         */
        object Serialization : Network()

        /**
         * An unknown network error occurred that couldn't be matched with a specific case
         */
        object Unknown : Network()
    }

    /**
     * A database layer error occurred.
     */
    sealed class Database(message: String = "") : Error(message) {

        /**
         * An unknown database error occurred that couldn't be matched with a specific case
         */
        class Unknown(message: String) : Database(message)
    }

    /**
     * An unknown error occurred that couldn't be matched with a layer case
     */
    object Unknown : Error("")
}


interface ErrorFactory {

    /**
     * Converts the supplied [code] to a corresponding [Error]
     */
    fun onCode(code: Int): Error

    /**
     * Converts the supplied [throwable] to a corresponding [Error]
     */
    fun onThrowable(throwable: Throwable): Error
}