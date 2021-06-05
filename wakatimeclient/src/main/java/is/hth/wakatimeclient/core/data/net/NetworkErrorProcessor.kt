package `is`.hth.wakatimeclient.core.data.net

import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.Error.Network
import `is`.hth.wakatimeclient.core.data.ErrorProcessor
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import retrofit2.Response
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Produces network related [Error]s based on the given inputs
 */
internal open class NetworkErrorProcessor : ErrorProcessor {

    override fun onError(code: Int, message: String): Error {
        return when (code) {
            Network.BadRequest.CODE -> Network.BadRequest(message)
            Network.Unauthorized.CODE -> Network.Unauthorized(message)
            Network.Forbidden.CODE -> Network.Forbidden(message)
            Network.NotFound.CODE -> Network.NotFound(message)
            Network.Timeout.Client.CODE -> Network.Timeout.Client(message)
            Network.Timeout.Gateway.CODE -> Network.Timeout.Gateway(message)
            Network.InternalServer.CODE -> Network.InternalServer(message)
            Network.Unavailable.CODE -> Network.Unavailable(message)
            Network.TooManyRequests.CODE -> Network.TooManyRequests(message)
            Network.Internal.NoNetwork.CODE -> Network.Internal.NoNetwork(message)
            Network.Internal.UnknownHost.CODE -> Network.Internal.UnknownHost(message)
            Network.Internal.Serialization.CODE -> Network.Internal.Serialization(message)
            Network.Internal.SocketTimeout.CODE -> Network.Internal.SocketTimeout(message)
            Network.Internal.Protocol.CODE -> Network.Internal.Protocol(message)
            Network.Internal.Http.CODE -> Network.Internal.Http(message)
            else -> Network.Unknown(code, message)
        }
    }

    override fun onError(throwable: Throwable): Error {
        val message = throwable.message ?: ""
        return when (throwable) {
            is SocketTimeoutException -> Network.Internal.SocketTimeout.CODE
            is UnknownHostException -> Network.Internal.UnknownHost.CODE
            is ProtocolException -> Network.Internal.Protocol.CODE
            is SerializationException -> Network.Internal.Serialization.CODE
            is HttpException -> Network.Internal.Http.CODE
            else -> -1
        }.let { code ->
            onError(code, message)
        }
    }

    /**
     * Converts the supplied failed [Response] into a [Error]
     */
    open fun onNetworkError(code: Int, message: String, error: String?): Error =
        onError(code, message)
}