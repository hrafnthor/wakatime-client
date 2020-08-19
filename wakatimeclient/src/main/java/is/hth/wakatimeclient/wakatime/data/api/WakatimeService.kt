package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.api.EnvelopePayload
import `is`.hth.wakatimeclient.wakatime.data.api.dto.TotalRecordDto
import `is`.hth.wakatimeclient.wakatime.data.api.dto.FullUserDto
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET

/**
 * Wakatime API interface as defined here https://wakatime.com/developer
 */
@Suppress("unused")
interface WakatimeService {

    companion object {
        private const val API_ENDPOINT = "/api/v1"
        private const val USERS = "$API_ENDPOINT/users"
        private const val CURRENT_USER = "$USERS/current"
        private const val LEADERS = "$API_ENDPOINT/leaders"
    }

    /**
     * Retrieves the details of the currently authenticated user.
     */
    @EnvelopePayload("data")
    @GET(CURRENT_USER)
    suspend fun getCurrentUser(): Response<FullUserDto>

    /**
     * Retrieves the total recorded time for the current user.
     */
    @EnvelopePayload("data")
    @GET("$CURRENT_USER/all_time_since_today")
    suspend fun getTotalRecord(): Response<TotalRecordDto>
}

/**
 * The Wakatime service error payload
 */
internal data class ServiceError(
    @SerializedName("error")
    val message: String
)