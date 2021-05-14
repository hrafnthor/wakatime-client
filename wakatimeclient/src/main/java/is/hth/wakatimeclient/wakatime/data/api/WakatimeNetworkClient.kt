package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.net.CacheControl
import `is`.hth.wakatimeclient.core.data.net.NetworkClient
import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor

internal class WakatimeNetworkClient internal constructor(
    private val client: NetworkClient,
    private val errorProcessor: NetworkErrorProcessor
): CacheControl by client {

    private val api: WakatimeApi = client.createService(WakatimeApi::class.java)
    private val oauthApi: OauthApi = client.createService(OauthApi::class.java)

    fun api(): WakatimeApi = api

    fun oauthApi(): OauthApi = oauthApi

    fun processor(): NetworkErrorProcessor = errorProcessor
}