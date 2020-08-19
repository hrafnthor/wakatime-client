package `is`.hth.wakatimeclient.core.data.db

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.ErrorFactory

/**
 * Produces database related [Error]s based on the given inputs
 */
class DbErrorFactory : ErrorFactory<Unit> {

    override fun onCode(code: Int): Error {
        return Error.Database.Unknown("")
    }

    override fun onThrowable(throwable: Throwable): Error {
        return Error.Database.Unknown(throwable.message ?: "")
    }
}