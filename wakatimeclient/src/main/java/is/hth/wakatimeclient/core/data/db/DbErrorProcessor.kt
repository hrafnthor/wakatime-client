package `is`.hth.wakatimeclient.core.data.db

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.ErrorProcessor

/**
 * Produces database related [Error]s based on the given inputs
 */
class DbErrorProcessor : ErrorProcessor {

    override fun onError(code: Int, message: String): Error {
        return Error.Database.Unknown(code, message)
    }

    override fun onError(throwable: Throwable): Error {
        return Error.Database.Unknown(-1, throwable.message ?: "")
    }
}