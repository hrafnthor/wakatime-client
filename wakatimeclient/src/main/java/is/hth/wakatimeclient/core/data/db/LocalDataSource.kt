package `is`.hth.wakatimeclient.core.data.db

import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.util.safeOperation
import `is`.hth.wakatimeclient.core.util.valueOrEmpty

internal open class LocalDataSource(
    private val processor: DbErrorProcessor
) {

    suspend fun <T : Any> operate(
        call: suspend () -> T?
    ): Results<T> = safeOperation(processor) {
        call.invoke().valueOrEmpty()
    }
}