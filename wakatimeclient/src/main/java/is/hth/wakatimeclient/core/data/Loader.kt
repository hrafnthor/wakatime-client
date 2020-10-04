package `is`.hth.wakatimeclient.core.data

import timber.log.Timber

/**
 * Performs orchestration between the local caching and the remote network layers.
 */
internal open class Loader<R, T> {

    private var expired: () -> Boolean = { true }
    private var cache: suspend () -> Results<T> = {
        print("No local data source defined. Returning empty")
        Results.Success.Empty
    }
    private var remote: suspend () -> Results<R> = {
        print("No remote data source defined. Returning empty")
        Results.Success.Empty
    }
    private var update: suspend (R) -> Results<Unit> = {
        print("No update mechanism defined")
        Results.Success.Empty
    }
    private var clear: suspend () -> Results<Boolean> = {
        print("No clear mechanism defined")
        Results.Success.Empty
    }

    /**
     * Determines if the local values should be considered expired, kicking of a remote fetch
     */
    fun expired(action: () -> Boolean) = apply { expired = action }

    /**
     * Loads the locally cached values. Receives the recently fetched remote values if
     */
    fun cache(action: suspend () -> Results<T>) = apply { cache = action }

    /**
     * Fetches the remote values over the network
     */
    fun remote(action: suspend () -> Results<R>) = apply { remote = action }

    /**
     * Updates the local cache after a successful remote fetch operation
     */
    fun update(action: suspend (R) -> Results<Unit>) = apply { update = action }

    /**
     * Clears the local cache after a successful remote fetch operation returns empty
     */
    fun clear(action: suspend () -> Results<Boolean>) = apply { clear = action }

    /**
     *
     */
    suspend fun execute(): Results<T> {
        return when (val localData: Results<T> = cache()) {
            is Results.Success.Values -> if (expired()) {
                fetchRemote {
                    // Return the previously cached values as well as the remote load error
                    Results.Success.Values(data = localData.data, error = it)
                }
            } else localData
            else -> fetchRemote {
                Results.Failure(it)
            }
        }
    }

    private suspend fun fetchRemote(onFailure: (Error) -> Results<T>): Results<T> {
        return when (val remoteData: Results<R> = remote()) {
            is Results.Failure -> onFailure(remoteData.error)
            is Results.Success.Empty -> when (val cleared: Results<Boolean> = clear()) {
                is Results.Success.Values -> {
                    if (!cleared.data) print("Local cache was not cleared!")
                    Results.Success.Empty
                }
                is Results.Success.Empty -> cleared
                is Results.Failure -> cleared
            }
            is Results.Success.Values -> updateLocal(remoteData.data, onFailure)
        }
    }

    private suspend fun updateLocal(data: R, onFailure: (Error) -> Results<T>): Results<T> {
        return when (val result = update(data)) {
            is Results.Failure -> onFailure(result.error)
            is Results.Success -> cache()
        }
    }

    private fun print(message: String): Unit = Timber.tag("Loader").w(message)
}

internal class SingleLoader<T> : Loader<T, T>()