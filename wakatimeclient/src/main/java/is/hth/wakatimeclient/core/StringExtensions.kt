package `is`.hth.wakatimeclient.core

/**
 * Returns `this` value unless it is null, then [block] is invoked and returns its result.
 */
inline fun String?.unlessNull(block: () -> String): String = this ?: block()

/**
 * Returns `this` value unless it is empty, then [block] is invoked and returns its result.
 */
inline fun String.unlessEmpty(block: () -> String): String = if (this.isEmpty()) block() else this