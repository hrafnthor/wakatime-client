package `is`.hth.wakatimeclient.core.util

import `is`.hth.wakatimeclient.core.data.Error
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
 * Returns `this` value wrapped in a [Results.Success.Values] unless it is null,
 * then the supplied [error] wrapped in a [Results.Failure] is returned
 */
fun <T : Any> T?.valueOrFailure(error: Error): Results<T> = if (this == null) {
    Results.Failure(error)
} else Results.Success.Values(this)