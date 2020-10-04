package `is`.hth.wakatimeclient.core.util

sealed class Mime(private val name: String) {

    object ApplicationJson : Mime("application/json")

    override fun toString(): String = name
}