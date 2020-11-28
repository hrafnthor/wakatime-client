package `is`.hth.wakatimeclient.core.util

import `is`.hth.wakatimeclient.core.data.Results

/**
 * Helper to force a when statement to assert all options are matched in a when statement.
 *
 * By default, Kotlin doesn't care if all branches are handled in a when statement. However, if you
 * use the when statement as an expression (with a value) it will force all cases to be handled.
 *
 * This helper is to make a lightweight way to say you meant to match all of them.
 *
 * Usage:
 *
 * ```
 * when(sealedObject) {
 *     is OneType -> //
 *     is AnotherType -> //
 * }.exhaustive
 */
val <T> T.exhaustive: T
    get() = this

/**
 * Returns `this` value wrapped in a [Results.Success.Values] unless it is null,
 * then a [Results.Success.Empty] is returned
 */
fun <T : Any> T?.valueOrEmpty(): Results<T> = if (this == null) {
    Results.Success.Empty
} else Results.Success.Values(this)

/**
 * Returns `this` value unless it is empty, then it returns [alternative]
 */
fun String.ifNotEmpty(alternative: String): String = if (isNotEmpty()) this else alternative

/**
 * Returns `this` value unless it is empty, then it returns `null`
 */
fun String.nullIfEmpty(): String? = if (isNotEmpty()) this else null

/**
 * Returns the first value
 */
fun <T> List<T>.firstOr(default: T): T = firstOrNull() ?: default

/**
 * Returns the first element matching the given [predicate], or [default] if
 * element was not found.
 */
inline fun <T> Iterable<T>.firstOr(default: T, predicate: (T) -> Boolean): T {
    return firstOrNull(predicate) ?: default
}

/**
 * Returns the first element matching the given [predicate] after it has been sent
 * through [transform], or [default] if none was found.
 * @param default will be returned if no item fulfills the predicate or the Iterable is empty
 * @param predicate function that evaluates each element and returns the result of predicate evaluation
 * @param transform function that transforms each element filtered by [predicate]
 */
internal inline fun <T, R> Iterable<T>.firstOr(
    default: R,
    predicate: (T) -> Boolean,
    transform: (T) -> R
): R = firstOrNull(predicate)?.let(transform) ?: default