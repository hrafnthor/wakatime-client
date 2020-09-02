package `is`.hth.wakatimeclient.core.data.auth

sealed class Method(val name: String) {

    companion object {
        fun convert(name: String): Method {
            return when (name) {
                OAuth.name -> OAuth
                ApiKey.name -> ApiKey
                else -> throw IllegalArgumentException("Unknown method name $name")
            }
        }
    }

    /**
     * OAuth 2.0 based authentication method
     */
    object OAuth : Method("oauth")

    /**
     * API key based authentication
     */
    object ApiKey : Method("key")
}