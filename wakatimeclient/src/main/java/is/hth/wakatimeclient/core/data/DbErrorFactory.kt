package `is`.hth.wakatimeclient.core.data

/**
 * Produces database related [Error]s based on the given inputs
 */
class DbErrorFactory : ErrorFactory {

    override fun onCode(code: Int): Error {
        return Error.Database.Unknown("")
    }

    override fun onThrowable(throwable: Throwable): Error {
        return Error.Database.Unknown(throwable.message ?: "")
    }
}