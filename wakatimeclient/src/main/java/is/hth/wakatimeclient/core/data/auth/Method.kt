package `is`.hth.wakatimeclient.core.data.auth

public enum class Method(public val key: String) {

    /**
     * No authentication method is being used, i.e no authentication data
     * was locally found
     */
    None("none"),

    /**
     * OAuth 2.0 based authentication method
     */
    OAuth("oauth"),

    /**
     * API key based authentication
     */
    ApiKey("api_key");

    public companion object {
        private val map = values().associateBy(Method::key)

        public fun convert(key: String): Method =
            map[key] ?: throw IllegalArgumentException("Unknown method key $key")
    }
}