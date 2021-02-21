package `is`.hth.wakatimeclient.wakatime.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


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
@Serializable
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
    val next: Int = -1,
    /**
     * The index of the previous page. If there is none, this value will be -1
     */
    @SerialName("prev_page")
    val previous: Int = -1,
    /**
     * The number of total available pages of data, if applicable. If not, then -1.
     */
    @SerialName("total_pages")
    val totalPages: Int = -1,
    /**
     * The total amount of items available over all pages
     */
    val total: Int = -1
)

@Serializable
data class WrappedResponse<T>(
    /**
     * The payload received from the service
     */
    val data: T
)