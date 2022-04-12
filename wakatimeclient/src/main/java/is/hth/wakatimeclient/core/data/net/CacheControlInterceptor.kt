package `is`.hth.wakatimeclient.core.data.net

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * Forces cache reading onto any network request that goes through it.
 */
internal class ReadInterceptor(maxAgeSeconds: Int) : CacheControlInterceptor(maxAgeSeconds) {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val modified: Request = original.newBuilder()
            .addHeader(Header.CacheControl.toString(), maxAgeCacheControl.toString())
            .build()
        return chain.proceed(modified)
    }
}

/**
 * Forces cache writing onto all requests that go through it, and clearing all caching
 * headers received from the server in favor of a new one, set to the supplied max-age
 * value.
 */
internal class WriteInterceptor(maxAgeSeconds: Int) : CacheControlInterceptor(maxAgeSeconds) {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val original: Response = chain.proceed(request)
        return original.newBuilder()
            .removeHeader(Header.CacheControl.toString())
            .removeHeader(Header.Pragma.toString())
            .addHeader(Header.CacheControl.toString(), maxAgeCacheControl.toString())
            .build()
    }
}

internal abstract class CacheControlInterceptor internal constructor(
    maxAgeSeconds: Int
) : Interceptor {

    /**
     *  A [CacheControl] configured to use the supplied maxAgeSeconds
     */
    protected val maxAgeCacheControl: CacheControl = CacheControl.Builder()
        .maxAge(maxAgeSeconds, TimeUnit.SECONDS)
        .build()
}