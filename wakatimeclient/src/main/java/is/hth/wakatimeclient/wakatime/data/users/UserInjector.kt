package `is`.hth.wakatimeclient.wakatime.data.users

import `is`.hth.wakatimeclient.core.data.DbErrorFactory
import `is`.hth.wakatimeclient.core.data.ErrorFactory
import `is`.hth.wakatimeclient.core.data.NetworkErrorFactory
import `is`.hth.wakatimeclient.wakatime.data.api.WakatimeService
import `is`.hth.wakatimeclient.wakatime.data.db.WakatimeDatabase
import `is`.hth.wakatimeclient.wakatime.data.db.dao.UserDao

internal object UserInjector {

    fun provideRepository(
        cacheLimit: Int,
        service: WakatimeService,
        db: WakatimeDatabase,
        networkErrorFactory: NetworkErrorFactory,
        dbErrorFactory: DbErrorFactory
    ): UserRepository {
        return UserRepositoryImpl(
            cacheLimit,
            remoteDataSource(service, networkErrorFactory),
            localDataSource(db.userDao(), dbErrorFactory)
        )
    }

    private fun remoteDataSource(
        service: WakatimeService,
        errors: ErrorFactory
    ): UserRemoteDataSource {
        return UserRemoteDataSourceImpl(errors, service)
    }

    private fun localDataSource(
        dao: UserDao,
        errors: ErrorFactory
    ): UserLocalDataSource {
        return UserLocalDataSourceImp(dao, errors)
    }
}