package `is`.hth.wakatimeclient.core.data

/**
 * Contains information regarding a error.
 *
 * See sub classes for detailed breakdown.
 */
@Suppress("unused")
public sealed class Error(
    /**
     * The error type's unique numerical code
     */
    public val code: Int,
    /**
     * A human readable message describing the cause of the error
     */
    public val message: String
) {

    /**
     * Contains any extra details that might have been passed on with this error
     */
    public val extra: MutableSet<String> = mutableSetOf()

    /**
     * A authentication layer error occurred. This category of errors only happen during
     * initial authentication, token refresh and remote token revoke operations.
     */
    public sealed class Authentication(code: Int, message: String) : Error(code, message) {

        /**
         * An unknown error occurred during the authentication processes
         */
        public class Unknown(code: Int, message: String) : Authentication(code, message)

        /**
         * An error occurred during initial authorization flow
         */
        public class Authorization(
            /**
             * A unique error code given by AppAuth describing the cause of the error.
             *
             * See [net.openid.appauth.AuthorizationException] for all available codes.
             */
            public val appauthCode: Int,
            message: String
        ) : Authentication(CODE, message) {
            public companion object {
                public const val CODE: Int = 100
            }
        }

        /**
         * User is not authenticated and so the operation was not possible
         */
        public class Unauthorized(message: String) : Authentication(CODE, message) {
            public companion object {
                public const val CODE: Int = 101
            }
        }

        /**
         * An error occurred during token refresh operation
         */
        public class TokenRefresh(
            public val appauthCode: Int,
            message: String
        ) : Authentication(CODE, message) {
            public companion object {
                public const val CODE: Int = 102
            }
        }

        /**
         * An error occurred during initial token fetch operation
         */
        public class TokenFetch(
            public val appauthCode: Int,
            message: String
        ) : Authentication(CODE, message) {
            public companion object {
                public const val CODE: Int = 103
            }
        }
    }

    /**
     * A network layer error occurred. This category of errors relate to interactions with the
     * public api surface of the network service.
     *
     * Each error type uses their respective HTTP error code when applicable
     */
    public sealed class Network(code: Int, message: String) : Error(code, message) {

        /**
         * A 400 Bad Request error occurred
         */
        public class BadRequest(message: String) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 400
            }
        }

        /**
         * Authentication is not present or has expired
         */
        public class Unauthorized(message: String) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 401
            }
        }

        /**
         * You are authenticated, but do not have permission to access the resource.
         */
        public class Forbidden(message: String) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 403
            }
        }

        /**
         * A 404 Not Found error occurred
         */
        public class NotFound(message: String) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 404
            }
        }

        /**
         * A 500/503 Service Unavailable error occurred, try again later.
         */
        public class Unavailable(message: String) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 503
            }
        }

        /**
         * Either a 504 Gateway Timeout, a 408 Client Timeout or a
         * socket timeout exception error occurred
         */
        public class Timeout(message: String) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 408
            }
        }

        /**
         * You are being rate limited, try making fewer than 5 requests per second.
         */
        public class TooManyRequests(message: String) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 429
            }
        }

        /**
         * The error originates internally
         */
        public class Internal(message: String) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 470
            }
        }

        /**
         * No network access was found
         */
        public class NoNetwork(message: String) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 471
            }
        }

        /**
         * The host's IP address could not be determined
         */
        public class UnknownHost(message: String) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 472
            }
        }

        /**
         * Serialization of what ever payload was being processed failed
         */
        public class Serialization(message: String) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 473
            }
        }

        /**
         * An unknown network error occurred that couldn't be matched with a specific case
         */
        public class Unknown(code: Int, message: String) : Network(code, message)
    }

    /**
     * An unknown error occurred that couldn't be matched with a layer case
     */
    public class Unknown(code: Int, message: String) : Error(code, message)
}

public interface ErrorProcessor {

    /**
     * Converts the supplied [code] to a corresponding [Error]
     */
    public fun onError(code: Int, message: String): Error

    /**
     * Converts the supplied [throwable] to a corresponding [Error]
     */
    public fun onError(throwable: Throwable): Error
}