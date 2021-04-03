package `is`.hth.wakatimeclient.wakatime.data.api

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*


@Serializable
data class ChronologicalResponse<T>(
    /**
     * The payload received from the service
     */
    val data: List<T> = emptyList(),
    /**
     * The start of the time range used for the request
     */
    val start: String,
    /**
     * The end of the time range used for the request
     */
    val end: String,
    /**
     * The timezone used for this request in Olson Country/Region format
     */
    val timezone: String
)

/**
 * A web service response JSON wrapper
 */
@Serializable(with = PagedResponseSerializer::class)
data class PagedResponse<T>(
    /**
     * The payload received from the service
     */
    val data: T,
    /**
     * The current page for the data delivered, if applicable. If not, then -1.
     */
    val page: Int = -1,
    /**
     * The index of the next page. If there is none, this value will be -1
     */
    @SerialName("next_page")
    val nextPage: Int = -1,
    /**
     * The url for the next page. If this is the last page then empty
     */
    @Transient
    @SerialName("next_page_url")
    val nextPageUrl: String = "",
    /**
     * The index of the previous page. If there is none, this value will be -1
     */
    @SerialName("prev_page")
    val previousPage: Int = -1,
    /**
     * The url for the previous page. If this is the first page then empty
     */
    @Transient
    @SerialName("prev_page_url")
    val previousPageUrl: String = "",
    /**
     * The number of total available pages of data, if applicable. If not, then -1.
     */
    @SerialName("total_pages")
    val totalPages: Int = -1,
    /**
     * The total amount of items available over all pages
     */
    @SerialName("total")
    val totalItems: Int = -1
)

internal class PagedResponseSerializer<T : Any>(
    private val dataSerializer: KSerializer<T>
) : KSerializer<PagedResponse<T>> {

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("PagedResponse") {}

    override fun deserialize(decoder: Decoder): PagedResponse<T> {
        require(decoder is JsonDecoder)
        val element = decoder.decodeJsonElement()
        if (element is JsonObject) {
            val data: T = if ("data" in element) {
                // The payload object is being delivered through the 'data' variable
                element.getValue("data")
            } else {
                // Parse the payload object from the root element it self
                element
            }.let {
                decoder.json.decodeFromJsonElement(dataSerializer, it)
            }
            val page: Int = getValue("page", -1, element) { it.int }
            val nextPage: Int = getValue("next_page", -1, element) { it.int }
            val nextPageUrl: String = getValue("next_page_url", "", element) { it.content }
            val previousPage: Int = getValue("prev_page", -1, element) { it.int }
            val previousPageUrl: String = getValue("prev_page_url", "", element) { it.content }
            val totalPages: Int = getValue("total_pages", -1, element) { it.int }
            val totalItems: Int = getValue("total", -1, element) { it.int }

            return PagedResponse(
                data = data,
                page = page,
                nextPage = nextPage,
                nextPageUrl = nextPageUrl,
                previousPage = previousPage,
                previousPageUrl = previousPageUrl,
                totalPages = totalPages,
                totalItems = totalItems
            )
        }
        throw SerializationException("'JsonObject' expected!")
    }

    override fun serialize(encoder: Encoder, value: PagedResponse<T>) {
        throw NotImplementedError("Serialization of PagedResponse had not been implemented yet!")
    }

    private fun <T> getValue(
        key: String,
        default: T,
        element: JsonObject,
        extract: (JsonPrimitive) -> T
    ): T {
        return if (key in element) {
            when (val value = element.getValue(key)) {
                is JsonNull -> default
                is JsonPrimitive -> extract(value)
                else -> default
            }
        } else default
    }
}

@Serializable
data class WrappedResponse<T>(
    /**
     * The payload received from the service
     */
    val data: T
)