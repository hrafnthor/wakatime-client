package `is`.hth.wakatimeclient.core.data.net

import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.auth.AuthClient
import `is`.hth.wakatimeclient.core.util.safeOperation
import retrofit2.Response

/**
 * Exposes utility functionality for conditional remote data calling and response handling
 */
internal open class RemoteDataSource(
    private val session: AuthClient.Session,
    private val processor: NetworkErrorProcessor
) {

    /**
     * Structured execution and result handling for network operations. Conducts authentication
     * and network state checks before executing the network operation.
     */
    suspend fun <T : Any> makeCall(
        /**
         * Should execute a network call and return the unmodified response
         */
        networkCall: suspend () -> Response<T>
    ): Results<T> = makeCall(networkCall) {
        it
    }

    /**
     * Structured execution and result handling for network operations. Conducts authentication
     * and network state checks before executing the network operation.
     */
    suspend fun <T : Any, R : Any> makeCall(
        /**
         * Should execute a network call and return the unmodified response
         */
        networkCall: suspend () -> Response<T>,
        /**
         * Performs any type conversion on the received network value that might be required
         */
        transform: (T) -> R,
    ): Results<R> = safeOperation(processor) {
        checkPreconditions {
            with(networkCall()) {
                val body: T? = body()
                when {
                    isSuccessful && body != null -> Results.Success.Values(transform(body))
                    isSuccessful -> Results.Success.Empty
                    else -> Results.Failure(processor.onError(this))
                }
            }
        }
    }

    // TODO: 20.8.2020 Add a check for network connectivity as a first step
    private suspend fun <R> checkPreconditions(passed: suspend () -> Results<R>): Results<R> {
        return when (val results = session.update(false)) {
            is Results.Success -> passed.invoke()
            is Results.Failure -> results
        }
    }
}