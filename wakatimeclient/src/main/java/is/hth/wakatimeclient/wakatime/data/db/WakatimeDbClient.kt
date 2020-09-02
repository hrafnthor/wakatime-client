package `is`.hth.wakatimeclient.wakatime.data.db

import `is`.hth.wakatimeclient.core.data.db.DatabaseClient
import `is`.hth.wakatimeclient.core.data.db.DbErrorProcessor

internal class WakatimeDbClient internal constructor(
    private val wakatimeDatabase: WakatimeDatabase,
    val processor: DbErrorProcessor
) : DatabaseClient(wakatimeDatabase), MasterDao by wakatimeDatabase