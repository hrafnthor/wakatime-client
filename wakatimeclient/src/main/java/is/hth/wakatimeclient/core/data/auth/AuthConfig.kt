package `is`.hth.wakatimeclient.core.data.auth

import android.net.Uri

/**
 *
 */
data class AuthConfig(
    /**
     *
     */
    val clientSecret: String,
    /**
     *
     */
    val clientId: String,
    /**
     *
     */
    val redirectUri: Uri,
    /**
     *
     */
    val host: Uri,
    /**
     *
     */
    val method: Method,
    /**
     *
     */
    val authorizationEndpoint: Uri = host.buildUpon().appendPath("oauth/authorize").build(),
    /**
     *
     */
    val tokenEndpoint: Uri = host.buildUpon().appendPath("oauth/token").build()
)