package `is`.hth.wakatimeclient.core.data.net

internal sealed class Header(private val value: String) {

    object CacheControl : Header("Cache-Control")

    object Pragma : Header("Pragma")

    override fun toString(): String = value
}