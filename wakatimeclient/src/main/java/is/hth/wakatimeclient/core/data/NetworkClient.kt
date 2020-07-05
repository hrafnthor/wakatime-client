package `is`.hth.wakatimeclient.core.data

import `is`.hth.wakatimeclient.core.data.api.DeEnvelopingConverter
import `is`.hth.wakatimeclient.core.util.NullStringAdapter
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit


interface NetworkClient {

    fun getService(): WakatimeService

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

internal class NetworkClientImpl private constructor(
    private val client: OkHttpClient,
    private val retrofit: Retrofit,
    private val service: WakatimeService
) : NetworkClient {

    override fun getService(): WakatimeService = service

    internal class Builder(
        private val host: String
    ) : NetworkClient.Builder {

        private val client = OkHttpClient.Builder()
        private val retrofit = Retrofit.Builder()
        private var authenticator: Authenticator? = null
        private val gson: Gson = GsonBuilder()
            .registerTypeAdapter("".javaClass, NullStringAdapter())
            .create()

        override fun getOKHttpBuilder(): OkHttpClient.Builder = client

        override fun getRetrofitBuilder(): Retrofit.Builder = retrofit

        override fun setAuthenticator(authenticator: Authenticator): NetworkClient.Builder {
            return apply { this.authenticator = authenticator }
        }

        internal fun build(
            authenticator: Authenticator
        ): NetworkClient {
            val builtClient = client.authenticator(this.authenticator ?: authenticator).build()
            val builtRetrofit = retrofit
                .baseUrl(host)
                .client(builtClient)
                .addConverterFactory(DeEnvelopingConverter(gson))
                .build()
            val service = builtRetrofit.create(WakatimeService::class.java)
            return NetworkClientImpl(
                builtClient,
                builtRetrofit,
                service
            )
        }
    }
}
