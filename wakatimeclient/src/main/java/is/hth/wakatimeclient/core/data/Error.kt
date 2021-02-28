package `is`.hth.wakatimeclient.core.data

@Suppress("unused")
sealed class Error(val code: Int, val message: String) {

    /**
     * Contains any extra details that might have been passed on with this error
     */
    val extra: MutableSet<String> = mutableSetOf()

    /**
     * A authentication layer error occurred. This category of errors only happen during
     * initial authentication, token refresh and remote token revoke operations.
     */
    sealed class Auth(code: Int, message: String) : Error(code, message) {

        /**
         * An unknown error occurred during the authentication processes
         */
        class Unknown(code: Int, message: String) : Auth(code, message)

        /**
         * An error occurred during initial authentication flow
         */
        class Authentication(val appauthCode: Int, message: String) : Auth(CODE, message) {
            companion object {
                const val CODE = 100
            }
        }

        /**
         * User is not authenticated and so the operation was not possible
         */
        class Unauthorized(message: String) : Auth(CODE, message) {
            companion object {
                const val CODE = 101
            }
        }

        /**
         * An error occurred during token refresh operation
         */
        class TokenRefresh(val appauthCode: Int, message: String) : Auth(CODE, message) {
            companion object {
                const val CODE = 102
            }
        }

        /**
         * An error occurred during initial token fetch operation
         */
        class TokenFetch(val appauthCode: Int, message: String) : Auth(CODE, message) {
            companion object {
                const val CODE = 103
            }
        }
    }

    /**
     * A network layer error occurred. This category of errors relate to interactions with the
     * public api surface of the network service
     */
    sealed class Network(code: Int, message: String) : Error(code, message) {

        /**
         * You are authenticated, but do not have permission to access the resource.
         */
        class Forbidden(message: String) : Network(CODE, message) {
            companion object {
                const val CODE = 201
            }
        }

        /**
         * No network access was found
         */
        class NoNetwork(message: String) : Network(CODE, message) {
            companion object {
                const val CODE = 202
            }
        }

        /**
         * Authentication is not present or has expired
         */
        class Unauthorized(message: String) : Network(CODE, message) {
            companion object {
                const val CODE = 203
            }
        }

        /**
         * A 404 Not Found error occurred
         */
        class NotFound(message: String) : Network(CODE, message) {
            companion object {
                const val CODE = 204
            }
        }

        /**
         * The host's IP address could not be determined
         */
        class UnknownHost(message: String) : Network(CODE, message) {
            companion object {
                const val CODE = 205
            }
        }

        /**
         * A 500/503 Service Unavailable error occurred, try again later.
         */
        class Unavailable(message: String) : Network(CODE, message) {
            companion object {
                const val CODE = 206
            }
        }

        /**
         * Either a 504 Gateway Timeout, a 408 Client Timeout or a
         * socket timeout exception error occurred
         */
        class Timeout(message: String) : Network(CODE, message) {
            companion object {
                const val CODE = 207
            }
        }

        /**
         * A 400 Bad Request error occurred
         */
        class BadRequest(message: String) : Network(CODE, message) {
            companion object {
                const val CODE = 208
            }
        }

        /**
         * Serialization of what ever payload was being processed failed
         */
        class Serialization(message: String) : Network(CODE, message) {
            companion object {
                const val CODE = 209
            }
        }

        /**
         * You are being rate limited, try making fewer than 5 requests per second.
         */
        class TooManyRequests(message: String) : Network(CODE, message) {
            companion object {
                const val CODE = 210
            }
        }

        /**
         * The error originates internally
         */
        class Internal(message: String) : Network(CODE, message) {
            companion object {
                const val CODE = 211
            }
        }

        /**
         * An unknown network error occurred that couldn't be matched with a specific case
         */
        class Unknown(code: Int, message: String) : Network(code, message)
    }

    /**
     * An unknown error occurred that couldn't be matched with a layer case
     */
    class Unknown(code: Int, message: String) : Error(code, message)
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