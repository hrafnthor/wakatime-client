package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import `is`.hth.wakatimeclient.wakatime.data.api.PagedResponse
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*

public class ResponseTests : DescribeSpec({

    val json = WakatimeJsonFactory.json

    val payload = Payload(
        id = "123",
        name = "testing",
        number = 100f
    )

    val response = PagedResponse(
        data = listOf(payload),
        page = 2,
        nextPage = 3,
        nextPageUrl = "some kind of url",
        previousPage = 1,
        previousPageUrl = "Another kind of url",
        totalPages = 3,
        totalItems = 2
    )

    val serialized = buildJsonObject {
        put(PagedResponse.DATA, json.encodeToJsonElement(
            serializer = ListSerializer(Payload.serializer()),
            value = response.data
        ))
        put(PagedResponse.PAGE, response.page)
        put(PagedResponse.NEXT_PAGE, response.nextPage)
        put(PagedResponse.NEXT_PAGE_URL, response.nextPageUrl)
        put(PagedResponse.PREVIOUS_PAGE, response.previousPage)
        put(PagedResponse.PREVIOUS_PAGE_URL, response.previousPageUrl)
        put(PagedResponse.TOTAL_PAGES, response.totalPages)
        put(PagedResponse.TOTAL_ITEMS, response.totalItems)
    }

    describe("serialization") {
        describe("of PagedResponse") {
            it("matches expected serialization") {
                json.encodeToJsonElement(response) shouldBe serialized
            }
        }
    }

    describe("deserialization") {
        describe("of PagedResponse") {
            describe("with nested payload") {

                it("matches expected deserialization") {
                    json.decodeFromJsonElement<PagedResponse<Payload>>(serialized) shouldBe response
                }
            }
            describe("with non nested payload") {
                // The payload is present at the root level and will be
                // moved into a nested position before deserialization occurs
                val received = buildJsonObject {
                    put("id", payload.id)
                    put("name", payload.name)
                    put("number", payload.number)
                    put(PagedResponse.PAGE, response.page)
                    put(PagedResponse.NEXT_PAGE, response.nextPage)
                    put(PagedResponse.NEXT_PAGE_URL, response.nextPageUrl)
                    put(PagedResponse.PREVIOUS_PAGE, response.previousPage)
                    put(PagedResponse.PREVIOUS_PAGE_URL, response.previousPageUrl)
                    put(PagedResponse.TOTAL_PAGES, response.totalPages)
                    put(PagedResponse.TOTAL_ITEMS, response.totalItems)
                }

                it("matches expected deserialization") {
                    json.decodeFromJsonElement<PagedResponse<Payload>>(received) shouldBe response
                }
            }
        }
    }
}) {
    @Serializable
    private data class Payload(
        val id: String,
        val name: String,
        val number: Float
    )
}
