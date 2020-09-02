package `is`.hth.wakatimeclient.wakatime.data.users

import `is`.hth.wakatimeclient.core.data.net.NetworkErrorProcessor
import `is`.hth.wakatimeclient.core.data.auth.AuthClient
import `is`.hth.wakatimeclient.core.data.db.DbErrorProcessor
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeApi
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeNetworkClient
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeDbClient
import `is`.hth.wakatimeclient.wakatime.data.db.dao.UserDao

internal object UserInjector {

    fun provideRepository(
        cacheLimit: Int,
        dbClient: WakatimeDbClient,
        session: AuthClient.Session,
        netClient: WakatimeNetworkClient
    ): UserRepository = UserRepositoryImpl(
        cacheLimit,
        remoteDataSource(netClient.api(), session, netClient.processor()),
        localDataSource(dbClient.userDao(), dbClient.processor)
    )

    private fun remoteDataSource(
        api: WakatimeApi,
        session: AuthClient.Session,
        processor: NetworkErrorProcessor
    ): UserRemoteDataSource = UserRemoteDataSourceImpl(session, processor, api)

    private fun localDataSource(
        dao: UserDao,
        errors: DbErrorProcessor
    ): UserLocalDataSource = UserLocalDataSourceImp(dao, errors)
}