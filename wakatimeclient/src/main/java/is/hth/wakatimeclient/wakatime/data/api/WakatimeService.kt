package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.api.EnvelopePayload
import `is`.hth.wakatimeclient.wakatime.data.api.dto.FullUserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Wakatime API interface as defined here https://wakatime.com/developer
 */
interface WakatimeService {

    companion object {
        private const val API_ENDPOINT = "/api/v1"
        private const val USERS = "$API_ENDPOINT/users"
        private const val LEADERS = "$API_ENDPOINT/leaders"
    }

    /**
     * Retrieves the details of the currently authenticated user.
     */
    @EnvelopePayload("data")
    @GET("$USERS/current")
    suspend fun getCurrentUser(): Response<FullUserDto>

    /**
     * Retrieves the details for the user corresponding to the supplied id.
     * This endpoint seems to only return a success if the corresponding user is the current user.
     */
    @EnvelopePayload("data")
    @GET("$USERS/{id}")
    suspend fun getUser(@Path("id") id: String): Response<FullUserDto>
}