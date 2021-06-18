package `is`.hth.wakatimeclient.core.data.net

import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeApi
import `is`.hth.wakatimeclient.wakatime.data.api.WrappedResponse
import `is`.hth.wakatimeclient.wakatime.data.model.Agent
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

@ExperimentalSerializationApi
@Suppress("BlockingMethodInNonBlockingContext")
public class CacheControlInterceptorTests : DescribeSpec({

    //#region setup
    fun getTempFile(): File {
        return File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString())
    }

    fun getConverterFactory(json: Json): Converter.Factory {
        return json.asConverterFactory(Mime.ApplicationJson.toString().toMediaType())
    }

    fun getCache(): Cache = Cache(getTempFile(), 10 * 1028 * 1028)

    fun getClient(cache: Cache, builder: OkHttpClient.Builder.() -> Unit): OkHttpClient =
        OkHttpClient.Builder()
            // Low timeouts so test don't take too long
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            // Configure new cache each time to avoid pollution
            .cache(cache)
            .apply(builder)
            .build()

    fun getApi(
        mockServer: MockWebServer,
        client: OkHttpClient,
        converter: Converter.Factory
    ): WakatimeApi = Retrofit.Builder()
        .baseUrl(mockServer.url("/"))
        .client(client)
        .addConverterFactory(converter)
        .build()
        .create(WakatimeApi::class.java)

    fun enqueue(response: String): MockResponse = MockResponse()
        .setResponseCode(200)
        .addHeader("Cache-Control : no-store;")
        .setBody(response)

    val json = WakatimeJsonFactory.json

    val mockServer = MockWebServer()

    val firstAgent = Agent(
        id = "1",
        value = "first agent",
        editor = "some editor",
        version = "some version",
        os = "some os name",
        lastSeen = "some date",
        createdAt = "some date"
    )

    val secondAgent = Agent(
        id = "2",
        value = "second agent",
        editor = "some editor",
        version = "some version",
        os = "some os name",
        lastSeen = "some date",
        createdAt = "some date"
    )

    val firstAgentResponse = WrappedResponse(listOf(firstAgent))
    val firstAgentResponseJson = json.encodeToString(firstAgentResponse)

    val secondAgentResponse = WrappedResponse(listOf(secondAgent))
    val secondAgentResponseJson = json.encodeToString(secondAgentResponse)

    //#endregion

    describe("WriteInterceptor") {

        it("force cache write") {
            val cache = getCache()
            val writer = WriteInterceptor(1)
            val client = getClient(cache) {
                // Add the interceptor being tested
                addNetworkInterceptor(writer)
            }

            val api = getApi(
                mockServer,
                client,
                getConverterFactory(json)
            )

            runBlocking {
                // Enqueue the mock response that gives clear
                // directions to not store the values in cache
                mockServer.enqueue(enqueue(firstAgentResponseJson))

                val actual = api.getAgents().body()

                withClue("received response should match expected") {
                    // Response should match the enqueued response
                    actual shouldBe firstAgentResponse
                }
                withClue("A single network request should have hit the cache") {
                    cache.requestCount() shouldBe 1
                }
                withClue("There should be a cached url") {
                    cache.urls().hasNext() shouldBe true
                }
                withClue("There should be one successful cache write") {
                    cache.writeSuccessCount() shouldBe 1
                }
            }
        }
    }

    describe("ReadInterceptor") {

        it("force read from cache") {
            val cache = getCache()
            val writer = WriteInterceptor(1)
            val reader = ReadInterceptor(1)
            val client = getClient(cache) {
                // Add the interceptor being tested
                addNetworkInterceptor(writer)
                addInterceptor(reader)
            }

            val api = getApi(
                mockServer,
                client,
                getConverterFactory(json)
            )

            runBlocking {
                // Enqueue the first mock response
                mockServer.enqueue(enqueue(firstAgentResponseJson))

                var actual = api.getAgents().body()
                withClue("actual response should match expected") {
                    actual shouldBe firstAgentResponse
                }

                // Enqueue the second mock response, which shouldn't be returned
                mockServer.enqueue(enqueue(secondAgentResponseJson))

                actual = api.getAgents().body()
                withClue("actual response should match first response") {
                    actual shouldBe firstAgentResponse
                }
            }
        }
    }

    afterSpec {
        mockServer.shutdown()
    }
})