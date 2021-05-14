package `is`.hth.wakatimeclient.core.data.auth

/**
 * Defines the authentication methods available
 */
public enum class Method(
    /**
     * The method's unique descriptive value
     */
    public val value: String
) {

    /**
     * OAuth 2.0 based authentication method
     */
    OAuth("oauth"),

    /**
     * API key based authentication
     */
    ApiKey("api_key");

    public companion object {
        private val map = values().associateBy(Method::value)

        public fun convert(key: String): Method =
            map[key] ?: throw IllegalArgumentException("Unknown method key $key")
    }
}