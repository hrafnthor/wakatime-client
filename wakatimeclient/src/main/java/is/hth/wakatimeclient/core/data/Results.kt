package `is`.hth.wakatimeclient.core.data

sealed class Results<out T : Any> {

    object Loading : Results<Nothing>()

    object Finished : Results<Nothing>()

    object Empty : Results<Nothing>()

    class Success<out T : Any>(val code: Int, val data: T) : Results<T>()

    class Cache<out T : Any>(val data: T) : Results<T>()

    class Error(val code: Int, val exception: Exception) : Results<Nothing>()

}