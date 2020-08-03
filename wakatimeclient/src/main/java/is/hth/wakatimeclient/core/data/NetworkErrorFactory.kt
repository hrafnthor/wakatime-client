package `is`.hth.wakatimeclient.core.data

import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection
import  `is`.hth.wakatimeclient.core.data.Error.Network
import com.google.gson.JsonParseException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Produces network related [Error]s based on the given inputs
 */
class NetworkErrorFactory : ErrorFactory {

    override fun onCode(code: Int): Error {
        return when (code) {
            HttpURLConnection.HTTP_NOT_FOUND -> Network.NotFound
            HttpURLConnection.HTTP_UNAVAILABLE -> Network.Unavailable
            HttpURLConnection.HTTP_CLIENT_TIMEOUT -> Network.Timeout
            HttpURLConnection.HTTP_GATEWAY_TIMEOUT -> Network.Timeout
            HttpURLConnection.HTTP_FORBIDDEN -> Network.AccessDenied
            HttpURLConnection.HTTP_BAD_REQUEST -> Network.BadRequest
            else -> Network.Unknown
        }
    }

    override fun onThrowable(throwable: Throwable): Error {
        return when (throwable) {
            is SocketTimeoutException -> Network.Timeout
            is UnknownHostException -> Network.UnknownHost
            is IOException -> Network.NoAccess
            is JsonParseException -> Network.Serialization
            is HttpException -> onCode(throwable.code())
            else -> Network.Unknown
        }
    }
}