package `is`.hth.wakatimeclient.core.util

import `is`.hth.wakatimeclient.core.ProcessResult
import java.io.IOException


/**
 * Wrap a suspending API [call] in try/catch. In case an exception is thrown, a [Result.Error] is
 * created based on the [errorMessage].
 */
suspend fun <T : Any> safeApiCall(
    call: suspend () -> ProcessResult<T>,
    errorMessage: String
): ProcessResult<T> {
    return try {
        call()
    } catch (e: Exception) {
        ProcessResult.Error(IOException(errorMessage, e))
    }
}