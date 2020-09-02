package `is`.hth.wakatimeclient.wakatime.data.api

import `is`.hth.wakatimeclient.core.data.net.NetworkClient
import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor

interface WakatimeNetworkClient {

    fun api(): WakatimeApi

    fun oauthApi(): OauthApi

    fun processor(): NetworkErrorProcessor
}

class WakatimeNetworkClientImpl internal constructor(
    private val client: NetworkClient,
    private val errorProcessor: NetworkErrorProcessor
) : WakatimeNetworkClient {

    private val api: WakatimeApi = client.createService(WakatimeApi::class.java)
    private val oauthApi: OauthApi = client.createService(OauthApi::class.java)

    override fun api(): WakatimeApi = api

    override fun oauthApi(): OauthApi = oauthApi

    override fun processor(): NetworkErrorProcessor = errorProcessor
}