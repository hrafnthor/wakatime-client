package `is`.hth.wakatimeclient.core.data.net

import `is`.hth.wakatimeclient.core.util.Mime
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.File

interface NetworkClient {

    /**
     * Creates a network service interface through the use of a backing [Retrofit] client
     */
    fun <T> createService(clazz: Class<T>): T

    /**
     * Clears the underlying cache if any is set
     */
    fun clearCache()

    interface Builder {

        /**
         * Exposes the underlying [OkHttpClient.Builder] for configurations
         * exceeding what this builder implements.
         * Be advised that any [Authenticator] set though the resulting builder will be overwritten,
         * so [Builder.setAuthenticator] should be used for that instead.
         */
        fun getOKHttpBuilder(): OkHttpClient.Builder

        /**
         *
         */
        fun getRetrofitBuilder(): Retrofit.Builder

        /**
         * Assign the [Authenticator] that will handle authentication for network requests.
         * This is not required as the library sets an authenticator by default that works
         * with the OAuth flow.
         * In the case of using a API key, a custom [Authenticator] will be needed.
         */
        fun setAuthenticator(authenticator: Authenticator): Builder
    }
}

@Suppress("unused")
internal class NetworkClientImpl private constructor(
    private val client: OkHttpClient,
    private val retrofit: Retrofit
) : NetworkClient {

    override fun <T> createService(clazz: Class<T>): T = retrofit.create(clazz)

    override fun clearCache() {
        client.cache()?.delete()
    }

    internal class Builder(
        private val host: String
    ) : NetworkClient.Builder {

        private val clientBuilder = OkHttpClient.Builder()
        private val retrofitBuilder = Retrofit.Builder()
        private lateinit var authenticator: Authenticator

        override fun getOKHttpBuilder(): OkHttpClient.Builder = clientBuilder

        override fun getRetrofitBuilder(): Retrofit.Builder = retrofitBuilder

        override fun setAuthenticator(
            authenticator: Authenticator
        ): NetworkClient.Builder = apply {
            this.authenticator = authenticator
        }

        fun setAuthenticatorIfNeeded(
            authenticator: Authenticator
        ): Builder = apply {
            if (!this::authenticator.isInitialized) setAuthenticator(authenticator)
        }

        /**
         * Turns on forced network layer caching
         */
        internal fun enableCache(
            cacheDir: File,
            cacheLifetimeInSeconds: Int
        ): Builder = apply {
            if (cacheLifetimeInSeconds > 0) {
                with(getOKHttpBuilder()) {
                    val cacheSize: Long = (10 * 1028 * 1028).toLong()
                    cache(Cache(cacheDir, cacheSize))
                    addInterceptor(ReadInterceptor(cacheLifetimeInSeconds))
                    addNetworkInterceptor(WriteInterceptor(cacheLifetimeInSeconds))
                }
            }
        }

        @ExperimentalSerializationApi
        internal fun build(): NetworkClient {
            val factory: Converter.Factory = Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }.asConverterFactory(MediaType.get(Mime.ApplicationJson.toString()))

            val client: OkHttpClient = clientBuilder
                .authenticator(authenticator)
                .build()

            val retrofit: Retrofit = retrofitBuilder
                .baseUrl(host)
                .client(client)
                .addConverterFactory(factory)
                .build()

            return NetworkClientImpl(
                client,
                retrofit
            )
        }
    }
}
