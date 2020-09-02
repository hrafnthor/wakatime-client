package `is`.hth.wakatimeclient.wakatime.data.api

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OauthApi {

    /**
     * Revokes the [token] belonging to the client with matching [id] and [secret].
     * The token can either be the access token or refresh token, depending on
     * which should be revoked.
     */
    @FormUrlEncoded
    @POST("/oauth/revoke")
    suspend fun revoke(
        @Field("client_id") id: String,
        @Field("client_secret") secret: String,
        @Field("token") token: String
    ): Response<Unit>
}