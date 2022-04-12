package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.net.CacheControl
import `is`.hth.wakatimeclient.core.data.net.NetworkClient
import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor

internal class WakatimeNetworkClient internal constructor(
    private val client: NetworkClient,
    val processor: NetworkErrorProcessor
): CacheControl by client {

    val api: WakatimeApi = client.createService(WakatimeApi::class.java)
    val oauthApi: OauthApi = client.createService(OauthApi::class.java)
}