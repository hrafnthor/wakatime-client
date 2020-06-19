package `is`.hth.wakatimeclient.core

sealed class ProcessResult<out T> {

    data class Success<out T>(val data: T): ProcessResult<T>()

    data class Error(val exception: Exception): ProcessResult<Nothing>()
}