package `is`.hth.wakatimeclient.core.data.auth

enum class Method(val key: String) {

    /**
     * No authentication method is being used, i.e no authentication data
     * was locally found
     */
    None(""),

    /**
     * OAuth 2.0 based authentication method
     */
    OAuth("oauth"),

    /**
     * API key based authentication
     */
    ApiKey("key");

    companion object {
        private val map = values().associateBy(Method::key)

        fun convert(key: String): Method = map[key] ?: throw IllegalArgumentException("Unknown method key $key")
    }
}