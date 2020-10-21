package `is`.hth.wakatimeclient.core

/**
 * Returns `this` value unless it is empty, then it returns null
 */
fun String.takeIfNotEmpty(): String? = takeIf { it.isNotEmpty() }