package `is`.hth.wakatimeclient.core.data.net

sealed class Mime(private val name: String) {

    object ApplicationJson : Mime("application/json")

    override fun toString(): String = name
}