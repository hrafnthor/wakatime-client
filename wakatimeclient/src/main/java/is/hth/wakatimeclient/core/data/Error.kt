package `is`.hth.wakatimeclient.core.data

@Suppress("unused")
sealed class Error(val message: String) {

    /**
     * Contains any extra details that might have been passed on with this error
     */
    val extra: MutableSet<String> = mutableSetOf()

    /**
     * A authentication layer error occurred. This category of errors only happen during
     * initial authentication, token refresh and remote token revoke operations.
     */
    sealed class Authentication(message: String) : Error(message) {

        /**
         * User is not authenticated and so the operation was not possible
         */
        class Unauthorized(message: String) : Authentication(message)

        /**
         * A error occurred during token refresh operation
         */
        class TokenRefresh(message: String) : Authentication(message)
    }

    /**
     * A network layer error occurred. This category of errors relate to interactions with the
     * public api surface of the network service
     */
    sealed class Network(message: String) : Error(message) {

        /**
         * You are authenticated, but do not have permission to access the resource.
         */
        class Forbidden(message: String) : Network(message)

        /**
         * No network access was found
         */
        class NoNetwork(message: String) : Network(message)

        /**
         * Authentication is not present or has expired
         */
        class Unauthorized(message: String) : Network(message)

        /**
         * A 404 Not Found error occurred
         */
        class NotFound(message: String) : Network(message)

        /**
         * The host's IP address could not be determined
         */
        class UnknownHost(message: String) : Network(message)

        /**
         * A 500/503 Service Unavailable error occurred, try again later.
         */
        class Unavailable(message: String) : Network(message)

        /**
         * Either a 504 Gateway Timeout, a 408 Client Timeout or a
         * socket timeout exception error occurred
         */
        class Timeout(message: String) : Network(message)

        /**
         * A 400 Bad Request error occurred
         */
        class BadRequest(message: String) : Network(message)

        /**
         * Serialization of what ever payload was being processed failed
         */
        class Serialization(message: String) : Network(message)

        /**
         * You are being rate limited, try making fewer than 5 requests per second.
         */
        class TooManyRequests(message: String) : Network(message)

        /**
         * The error originates internally
         */
        class Internal(message: String) : Network(message)

        /**
         * An unknown network error occurred that couldn't be matched with a specific case
         */
        class Unknown(val code: Int, message: String) : Network(message)
    }

    /**
     * A database layer error occurred.
     */
    sealed class Database(message: String = "") : Error(message) {

        /**
         * An empty database result was returned where it was not allowed to
         */
        class Empty(message: String) : Database(message)

        /**
         * A database insert operation failed with message
         */
        class Insert(message: String): Database(message)

        /**
         * An unknown database error occurred that couldn't be matched with a specific case
         */
        class Unknown(val code: Int, message: String) : Database(message)
    }

    /**
     * An unknown error occurred that couldn't be matched with a layer case
     */
    class Unknown(val code: Int, message: String) : Error(message)
}


interface ErrorProcessor {

    /**
     * Converts the supplied [code] to a corresponding [Error]
     */
    fun onError(code: Int, message: String): Error

    /**
     * Converts the supplied [throwable] to a corresponding [Error]
     */
    fun onError(throwable: Throwable): Error
}