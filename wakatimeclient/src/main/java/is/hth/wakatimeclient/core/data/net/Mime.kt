package `is`.hth.wakatimeclient.core.data.net

internal sealed class Mime(private val value: String) {

    object ApplicationJson : Mime("application/json")

    override fun toString(): String = value
}