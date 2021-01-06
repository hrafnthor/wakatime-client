package `is`.hth.wakatimeclient.wakatime.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


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
    @Transient
    val page: Int = -1,
    /**
     * The number of total available pages of data, if applicable. If not, then -1.
     */
    @Transient
    @SerialName("total_pages")
    val totalPages: Int = -1
)

@Serializable
data class WrappedResponse<T>(
    /**
     * The payload received from the service
     */
    val data: T
)