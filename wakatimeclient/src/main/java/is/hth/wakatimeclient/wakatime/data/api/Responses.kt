package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.wakatime.data.findValue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*


@Serializable
public data class ChronologicalResponse<T>(
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
@Serializable(with = PagedResponseTransformer::class)
public data class PagedResponse<T>(
    /**
     * The payload received from the service
     */
    @SerialName(DATA)
    val data: List<T>,
    /**
     * The current page for the data delivered, if applicable. If not, then 1.
     */
    @SerialName(PAGE)
    val page: Int = PAGE_DEFAULT,
    /**
     * The index of the next page. If there is none, this value will be -1
     */
    @SerialName(NEXT_PAGE)
    val nextPage: Int = NEXT_PAGE_DEFAULT,
    /**
     * The url for the next page. If this is the last page then empty
     */
    @SerialName(NEXT_PAGE_URL)
    val nextPageUrl: String = NEXT_PAGE_URL_DEFAULT,
    /**
     * The index of the previous page. If there is none, this value will be -1
     */
    @SerialName(PREVIOUS_PAGE)
    val previousPage: Int = PREVIOUS_PAGE_DEFAULT,
    /**
     * The url for the previous page. If this is the first page then empty
     */
    @SerialName(PREVIOUS_PAGE_URL)
    val previousPageUrl: String = PREVIOUS_PAGE_URL_DEFAULT,
    /**
     * The number of total available pages of data, if applicable. If not, then 1.
     */
    @SerialName(TOTAL_PAGES)
    val totalPages: Int = TOTAL_PAGES_DEFAULT,
    /**
     * The total amount of items available over all pages
     */
    @SerialName(TOTAL_ITEMS)
    val totalItems: Int = TOTAL_ITEMS_DEFAULT,
) {
    internal companion object {
        const val DATA = "data"
        const val PAGE = "page"
        const val NEXT_PAGE = "next_page"
        const val NEXT_PAGE_URL = "next_page_url"
        const val PREVIOUS_PAGE = "prev_page"
        const val PREVIOUS_PAGE_URL = "prev_page_url"
        const val TOTAL_PAGES = "total_pages"
        const val TOTAL_ITEMS = "total"

        const val PAGE_DEFAULT = 0
        const val NEXT_PAGE_DEFAULT = -1
        const val NEXT_PAGE_URL_DEFAULT = ""
        const val PREVIOUS_PAGE_DEFAULT = -1
        const val PREVIOUS_PAGE_URL_DEFAULT = ""
        const val TOTAL_PAGES_DEFAULT = 0
        const val TOTAL_ITEMS_DEFAULT = 0

        val set: Set<String> = setOf(
            DATA,
            PAGE,
            NEXT_PAGE,
            NEXT_PAGE_URL,
            PREVIOUS_PAGE,
            PREVIOUS_PAGE_URL,
            TOTAL_PAGES,
            TOTAL_ITEMS
        )
    }
}

/**
 * Performs Json transformation on the payload to standardize the way that it
 * is delivered for consumption.
 */
internal class PagedResponseTransformer<T : Any>(
    dataSerializer: KSerializer<T>
) : JsonTransformingSerializer<PagedResponse<T>>(PagedResponseSerializer(dataSerializer)) {

    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element is JsonObject) {
            return buildJsonObject {
                put(PagedResponse.DATA, element[PagedResponse.DATA] ?: buildJsonArray {
                    add(buildJsonObject {
                        // Data payload is being delivered in the root response object
                        // Iterate over it and create a new stand along object
                        element.filterKeys { PagedResponse.set.contains(it).not() }.forEach {
                            put(it.key, it.value)
                        }
                    })
                })

                findValue(
                    element = element,
                    key = PagedResponse.PAGE,
                    default = PagedResponse.PAGE_DEFAULT
                )
                findValue(
                    element = element,
                    key = PagedResponse.NEXT_PAGE,
                    default = PagedResponse.NEXT_PAGE_DEFAULT
                )
                findValue(
                    element = element,
                    key = PagedResponse.NEXT_PAGE_URL,
                    default = PagedResponse.NEXT_PAGE_URL_DEFAULT
                )
                findValue(
                    element = element,
                    key = PagedResponse.PREVIOUS_PAGE,
                    default = PagedResponse.PREVIOUS_PAGE_DEFAULT
                )
                findValue(
                    element = element,
                    key = PagedResponse.PREVIOUS_PAGE_URL,
                    default = PagedResponse.PREVIOUS_PAGE_URL_DEFAULT
                )

                findValue(
                    element = element,
                    key = PagedResponse.TOTAL_PAGES,
                    default = PagedResponse.TOTAL_PAGES_DEFAULT
                )

                findValue(
                    element = element,
                    key = PagedResponse.TOTAL_ITEMS,
                    default = PagedResponse.TOTAL_ITEMS_DEFAULT
                )
            }
        }
        throw IllegalArgumentException("Incorrect JsonElement type received for PagedResponse deserialization!")
    }
}

/**
 * This serializer manually parses the structure of the PagedResponse payload
 * and modifies the structure in cases where that is needed
 */
@OptIn(ExperimentalSerializationApi::class)
internal class PagedResponseSerializer<T : Any>(
    dataSerializer: KSerializer<T>
) : KSerializer<PagedResponse<T>> {

    private val listSerializer: KSerializer<List<T>> = ListSerializer(dataSerializer)

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("response") {
            element(PagedResponse.DATA, listSerializer.descriptor)
            element<Int>(PagedResponse.PAGE)
            element<Int>(PagedResponse.NEXT_PAGE)
            element<String>(PagedResponse.NEXT_PAGE_URL)
            element<Int>(PagedResponse.PREVIOUS_PAGE)
            element<String>(PagedResponse.PREVIOUS_PAGE_URL)
            element<Int>(PagedResponse.TOTAL_PAGES)
            element<Int>(PagedResponse.TOTAL_ITEMS)
        }


    override fun serialize(encoder: Encoder, value: PagedResponse<T>) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.DATA),
                listSerializer,
                value.data
            )
            encodeIntElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.PAGE),
                value.page
            )
            encodeIntElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.NEXT_PAGE),
                value.nextPage
            )
            encodeStringElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.NEXT_PAGE_URL),
                value.nextPageUrl
            )
            encodeIntElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.PREVIOUS_PAGE),
                value.previousPage
            )
            encodeStringElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.PREVIOUS_PAGE_URL),
                value.previousPageUrl
            )
            encodeIntElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.TOTAL_PAGES),
                value.totalPages
            )
            encodeIntElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.TOTAL_ITEMS),
                value.totalItems
            )
        }
    }

    override fun deserialize(decoder: Decoder): PagedResponse<T> {
        require(decoder is JsonDecoder)
        return decoder.decodeStructure(descriptor) {
            val data = decodeSerializableElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.DATA),
                listSerializer
            )
            val page = decodeIntElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.PAGE)
            )
            val nextPage = decodeIntElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.NEXT_PAGE)
            )
            val nextPageUrl = decodeStringElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.NEXT_PAGE_URL)
            )
            val previousPage = decodeIntElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.PREVIOUS_PAGE)
            )
            val previousPageUrl = decodeStringElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.PREVIOUS_PAGE_URL)
            )
            val totalPages = decodeIntElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.TOTAL_PAGES)
            )
            val totalItems = decodeIntElement(
                descriptor,
                descriptor.getElementIndex(PagedResponse.TOTAL_ITEMS)
            )

            PagedResponse(
                data = data,
                page = page,
                nextPage = nextPage,
                nextPageUrl = nextPageUrl,
                previousPage = previousPage,
                previousPageUrl = previousPageUrl,
                totalPages = totalPages,
                totalItems = totalItems,
            )
        }
    }
}

@Serializable
public data class WrappedResponse<T>(
    /**
     * The payload received from the service
     */
    val data: T
)