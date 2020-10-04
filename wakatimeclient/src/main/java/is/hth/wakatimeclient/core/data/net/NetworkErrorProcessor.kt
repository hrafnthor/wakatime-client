package `is`.hth.wakatimeclient.core.data.net

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.Error.Network
import `is`.hth.wakatimeclient.core.data.ErrorProcessor
import com.google.gson.JsonParseException
import retrofit2.HttpException
import retrofit2.Response
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Produces network related [Error]s based on the given inputs
 */
open class NetworkErrorProcessor : ErrorProcessor {

    override fun onError(code: Int, message: String): Error {
        return when (code) {
            HttpURLConnection.HTTP_NOT_FOUND -> Network.NotFound(message)
            HttpURLConnection.HTTP_UNAVAILABLE -> Network.Unavailable(message)
            HttpURLConnection.HTTP_CLIENT_TIMEOUT -> Network.Timeout(message)
            HttpURLConnection.HTTP_GATEWAY_TIMEOUT -> Network.Timeout(message)
            HttpURLConnection.HTTP_UNAUTHORIZED -> Network.Unauthorized(message)
            HttpURLConnection.HTTP_BAD_REQUEST -> Network.BadRequest(message)
            HttpURLConnection.HTTP_FORBIDDEN -> Network.Forbidden(message)
            HttpURLConnection.HTTP_INTERNAL_ERROR -> Network.Unavailable(message)
            429 -> Network.TooManyRequests(message)
            else -> Network.Unknown(code, message)
        }
    }

    override fun onError(throwable: Throwable): Error {
        val message = throwable.message ?: ""
        return when (throwable) {
            is SocketTimeoutException -> Network.Timeout(message)
            is UnknownHostException -> Network.UnknownHost(message)
            is ProtocolException -> Network.Internal(message)
            is JsonParseException -> Network.Serialization(message)
            is HttpException -> onError(throwable.code(), message)
            else -> Network.Unknown(-1, message)
        }
    }

    /**
     * Converts the supplied failed [Response] into a [Error]
     */
    open fun onError(response: Response<*>): Error = onError(response.code(), response.message() ?: "")
}