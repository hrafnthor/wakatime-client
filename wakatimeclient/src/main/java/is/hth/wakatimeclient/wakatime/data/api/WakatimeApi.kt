package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.net.EnvelopePayload
import `is`.hth.wakatimeclient.wakatime.model.FullUser
import `is`.hth.wakatimeclient.wakatime.model.Leaders
import `is`.hth.wakatimeclient.wakatime.model.TotalRecord
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Wakatime API interface as defined here https://wakatime.com/developer
 */
@Suppress("unused")
interface WakatimeApi {

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
    suspend fun getCurrentUser(): Response<Wrapper<FullUser>>

    /**
     * Retrieves the total recorded time for the current user.
     */
    @EnvelopePayload("data")
    @GET("$CURRENT_USER/all_time_since_today")
    suspend fun getTotalRecord(): Response<Wrapper<TotalRecord>>

    /**
     * Retrieves the public leaderboard.
     * Can filter by [language] which will give the public leaders for that language.
     * The results are paginated so iterate by requesting a [page]
     */
    @GET(LEADERS)
    suspend fun getPublicLeaderboard(
        @Query("language") language: String,
        @Query("page") page: Int
    ): Response<Leaders>
}

/**
 * The Wakatime service error payload
 */
@Serializable
internal data class ServiceError(
    @SerialName("error")
    val message: String
)

/**
 * A dumb JSON wrapper
 */
@Serializable
data class Wrapper<T : Any>(val data: T)