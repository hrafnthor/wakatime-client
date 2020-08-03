package `is`.hth.wakatimeclient.core.data

/**
 * Performs orchestration between the local caching and the remote network layers
 */
class Loader<T> {

    private var expired: () -> Boolean = { true }
    private var local: suspend () -> Results<T> = { Results.Failure(Error.Unknown) }
    private var remote: suspend () -> Results<T> = { Results.Failure(Error.Unknown) }
    private var update: suspend (T) -> Unit = {}

    /**
     * Determines if the local values should be considered expired, kicking of a remote fetch
     */
    fun expired(action: () -> Boolean) = apply { expired = action }

    /**
     * Loads the locally cached values
     */
    fun local(action: suspend () -> Results<T>) = apply { local = action }

    /**
     * Fetches the remote values over the network
     */
    fun remote(action: suspend () -> Results<T>) = apply { remote = action }

    /**
     * Updates the locally cached values after a successful remote fetch operation
     */
    fun update(action: suspend (T) -> Unit) = apply { update = action }

    /**
     *
     */
    suspend fun execute(): Results<T> {
        return when (val localData = local()) {
            is Results.Values -> if (expired()) {
                fetchRemote {
                    Results.Values(data = localData.data, error = it)
                }
            } else localData
            else -> fetchRemote {
                Results.Failure(it)
            }
        }
    }

    private suspend fun fetchRemote(onFailure: (Error) -> Results<T>): Results<T> {
        return when (val remoteData = remote()) {
            is Results.Failure -> onFailure(remoteData.error)
            is Results.Empty -> remoteData
            is Results.Values -> remoteData.also {
                update(it.data)
            }
        }
    }
}