package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.net.NetworkClient
import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor

class WakatimeNetworkClient internal constructor(
    client: NetworkClient,
    private val errorProcessor: NetworkErrorProcessor
) {

    private val api: WakatimeApi = client.createService(WakatimeApi::class.java)
    private val oauthApi: OauthApi = client.createService(OauthApi::class.java)

    fun api(): WakatimeApi = api

    fun oauthApi(): OauthApi = oauthApi

    fun processor(): NetworkErrorProcessor = errorProcessor
}