package `is`.hth.wakatimeclient.wakatime.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A web service response JSON wrapper
 */
@Serializable
data class ResponseWrapper<T : Any>(
    /**
     * The data received from the service
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