package `is`.hth.wakatimeclient.core.data.net

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
import kotlin.math.abs

public interface CacheControl {
    /**
     * Clears the underlying cache if any is set
     */
    public fun clearCache()
}

public interface NetworkClient : CacheControl {

    /**
     * Creates a network service interface through the use of a backing [Retrofit] client
     */
    public fun <T> createService(clazz: Class<T>): T

    public interface Builder {

        /**
         * Exposes the underlying [OkHttpClient.Builder] for configurations outside the scope of
         * what this builder implements.
         * Be advised that any [Authenticator] set though the resulting builder will be overwritten,
         * so [Builder.setAuthenticator] should be used if a custom authenticator is needed.
         */
        public fun getOKHttpBuilder(): OkHttpClient.Builder

        /**
         * Exposes the underlying [Retrofit.Builder] for configurations outside of the scope
         * of what this builder implements.
         */
        public fun getRetrofitBuilder(): Retrofit.Builder

        /**
         * Assign the [Authenticator] that will handle authentication for network requests.
         * This is not required as the library sets an authenticator by default that works
         * with the OAuth flow.
         * In the case of using a API key, a custom [Authenticator] will be needed.
         */
        public fun setAuthenticator(authenticator: Authenticator): Builder

        /**
         * Assigns the global cache lifetime in seconds used to determine if new
         * values should be fetched over the network. The default value is 5 minutes.
         * @param cacheDir The location of where the cache will be stored, for instance Context.cacheDir
         * @param cacheLifetimeInSeconds The lifetime of the cache in seconds
         */
        public fun enableCache(cacheDir: File, cacheLifetimeInSeconds: Int): Builder
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

    @OptIn(ExperimentalSerializationApi::class)
    internal class Builder(
        private val host: String
    ) : NetworkClient.Builder {

        private val clientBuilder = OkHttpClient.Builder()
        private val retrofitBuilder = Retrofit.Builder()

        private lateinit var authenticator: Authenticator
        private var cacheLifetimeInSeconds: Int = 0
        private var cacheDir: File? = null

        override fun getOKHttpBuilder(): OkHttpClient.Builder = clientBuilder

        override fun getRetrofitBuilder(): Retrofit.Builder = retrofitBuilder

        override fun setAuthenticator(
            authenticator: Authenticator
        ): NetworkClient.Builder = apply { this.authenticator = authenticator }

        override fun enableCache(
            cacheDir: File,
            cacheLifetimeInSeconds: Int
        ): Builder = apply {
            this.cacheLifetimeInSeconds = abs(cacheLifetimeInSeconds)
            this.cacheDir = cacheDir
        }

        internal fun setAuthenticatorIfNeeded(
            authenticator: Authenticator
        ): Builder = apply {
            if (!this::authenticator.isInitialized) setAuthenticator(authenticator)
        }

        internal fun build(json: Json): NetworkClient {
            val factory: Converter.Factory = json
                .asConverterFactory(MediaType.get(Mime.ApplicationJson.toString()))

            val client: OkHttpClient = clientBuilder
                .authenticator(authenticator)
                .apply {
                    cacheDir?.let { directory ->
                        if (cacheLifetimeInSeconds > 0) {
                            val cacheSize: Long = (10 * 1028 * 1028).toLong()
                            cache(Cache(directory, cacheSize))
                            addInterceptor(ReadInterceptor(cacheLifetimeInSeconds))
                            addNetworkInterceptor(WriteInterceptor(cacheLifetimeInSeconds))
                        }
                    }
                }
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

internal object WakatimeJsonFactory {

    private const val ignoreUnknownKeys: Boolean = true
    private const val coerceInputValues: Boolean = true
    private const val encodeDefaults: Boolean = true

    val json: Json = Json {
        ignoreUnknownKeys = WakatimeJsonFactory.ignoreUnknownKeys
        coerceInputValues = WakatimeJsonFactory.coerceInputValues
        encodeDefaults = WakatimeJsonFactory.encodeDefaults
    }
}