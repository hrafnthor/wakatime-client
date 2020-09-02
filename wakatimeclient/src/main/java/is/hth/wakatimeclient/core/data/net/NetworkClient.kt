package `is`.hth.wakatimeclient.core.data.net

import `is`.hth.wakatimeclient.core.util.NullStringAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface NetworkClient {

    /**
     * Creates a network service interface through the use of a backing [Retrofit] client
     */
    fun <T> createService(clazz: Class<T>): T

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

    internal class Builder(
        private val host: String
    ) : NetworkClient.Builder {

        private val gson: Gson = GsonBuilder()
            .registerTypeAdapter(String::class.java, NullStringAdapter())
            .create()

        private val clientBuilder = OkHttpClient.Builder()
        private val retrofitBuilder = Retrofit.Builder()
        private lateinit var authenticator: Authenticator

        override fun getOKHttpBuilder(): OkHttpClient.Builder = clientBuilder

        override fun getRetrofitBuilder(): Retrofit.Builder = retrofitBuilder

        override fun setAuthenticator(authenticator: Authenticator): NetworkClient.Builder =
            apply { this.authenticator = authenticator }

        fun setAuthenticatorIfNeeded(authenticator: Authenticator): Builder =
            apply { if (!this::authenticator.isInitialized) setAuthenticator(authenticator) }

        internal fun build(): NetworkClient {
            val client = clientBuilder.authenticator(authenticator).build()
            val retrofit = retrofitBuilder
                .baseUrl(host)
                .client(client)
                .addConverterFactory(DeEnvelopingConverter(gson))
                .build()

            return NetworkClientImpl(
                client,
                retrofit
            )
        }
    }
}
