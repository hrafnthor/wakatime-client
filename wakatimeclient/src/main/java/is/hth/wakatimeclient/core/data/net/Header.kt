package `is`.hth.wakatimeclient.core.data.net

sealed class Header(private val name: String) {

    object CacheControl : Header("Cache-Control")

    object Pragma : Header("Pragma")

    override fun toString(): String = name
}