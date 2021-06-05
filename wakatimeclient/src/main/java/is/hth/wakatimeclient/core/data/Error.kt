package `is`.hth.wakatimeclient.core.data

import kotlinx.serialization.SerializationException
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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
        public class Unknown internal constructor(
            code: Int,
            message: String
        ) : Authentication(code, message)

        /**
         * An error occurred during initial authorization flow
         */
        public class Authorization internal constructor(
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
        public class Unauthorized internal constructor(
            message: String
        ) : Authentication(CODE, message) {
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
        public class BadRequest internal constructor(
            message: String
        ) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 400
            }
        }

        /**
         * Authentication is not present or has expired
         */
        public class Unauthorized internal constructor(
            message: String
        ) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 401
            }
        }

        /**
         * You are authenticated, but do not have permission to access the resource.
         */
        public class Forbidden internal constructor(
            message: String
        ) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 403
            }
        }

        /**
         * A 404 Not Found error occurred
         */
        public class NotFound internal constructor(
            message: String
        ) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 404
            }
        }

        /**
         * A network operation timeout occurred
         */
        public sealed class Timeout(code: Int, message: String) : Network(code, message) {

            /**
             * A 408 Client Timeout error occurred
             */
            public class Client internal constructor(
                message: String
            ) : Timeout(CODE, message) {
                public companion object {
                    public const val CODE: Int = 408
                }
            }

            /**
             * A 504 Gateway Timeout error occurred
             */
            public class Gateway internal constructor(
                message: String
            ) : Timeout(CODE, message) {
                public companion object {
                    public const val CODE: Int = 504
                }
            }
        }

        /**
         * You are being rate limited, try making fewer than 5 requests per second.
         */
        public class TooManyRequests internal constructor(
            message: String
        ) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 429
            }
        }

        /**
         * A 500 Internal Server error occurred, try again later
         */
        public class InternalServer internal constructor(
            message: String
        ) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 500
            }
        }

        /**
         * A 503 Service Unavailable error occurred, try again later.
         */
        public class Unavailable internal constructor(
            message: String
        ) : Network(CODE, message) {
            public companion object {
                public const val CODE: Int = 503
            }
        }

        /**
         * The error originates internally
         */
        public sealed class Internal(code: Int, message: String) : Network(code, message) {
            private companion object {
                const val CODE: Int = 1000
            }

            /**
             * No network access was found
             */
            public class NoNetwork internal constructor(
                message: String
            ) : Internal(CODE, message) {
                public companion object {
                    public const val CODE: Int = Internal.CODE + 1
                }
            }

            /**
             * A [UnknownHostException] was thrown during operation
             */
            public class UnknownHost internal constructor(
                message: String
            ) : Internal(CODE, message) {
                public companion object {
                    public const val CODE: Int = Internal.CODE + 2
                }
            }

            /**
             * A [SerializationException] was thrown during operation
             */
            public class Serialization internal constructor(
                message: String
            ) : Internal(CODE, message) {
                public companion object {
                    public const val CODE: Int = Internal.CODE + 3
                }
            }

            /**
             * A [SocketTimeoutException] was thrown
             */
            public class SocketTimeout internal constructor(
                message: String
            ) : Internal(CODE, message) {
                public companion object {
                    public const val CODE: Int = Internal.CODE + 4
                }
            }

            /**
             * A [ProtocolException] was thrown
             */
            public class Protocol internal constructor(
                message: String
            ) : Internal(CODE, message) {
                public companion object {
                    public const val CODE: Int = Internal.CODE + 5
                }
            }

        }

        /**
         * An unknown network error occurred that couldn't be matched with a specific case
         */
        public class Unknown internal constructor(
            code: Int,
            message: String
        ) : Network(code, message)
    }

    /**
     * An unknown error occurred that couldn't be matched with a layer case
     */
    public class Unknown internal constructor(
        code: Int,
        message: String
    ) : Error(code, message)
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