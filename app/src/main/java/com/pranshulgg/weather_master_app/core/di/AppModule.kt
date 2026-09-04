package com.pranshulgg.weather_master_app.core.di

import android.content.Context
import com.pranshulgg.weather_master_app.core.managers.ExternalManager
import com.pranshulgg.weather_master_app.core.managers.LocationManager
import com.pranshulgg.weather_master_app.core.managers.SourceManager
import com.pranshulgg.weather_master_app.core.managers.WeatherBlocksManager
import com.pranshulgg.weather_master_app.core.managers.WeatherManager
import com.pranshulgg.weather_master_app.core.managers.WeatherUnitsManager
import com.pranshulgg.weather_master_app.core.managers.requests.PendingRequests
import com.pranshulgg.weather_master_app.core.network.github.GithubApi
import com.pranshulgg.weather_master_app.core.network.github.GithubRepository
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.NominatimApi
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json.NominatimRepository
import com.pranshulgg.weather_master_app.data.local.WeatherMasterDatabase
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.github.GithubDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherBlocksDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherUnitsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
import com.pranshulgg.weather_master_app.data.repository.ApiKeysRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.data.repository.data.SourceDataRepository
import com.pranshulgg.weather_master_app.data.store.InitializationStore
import com.pranshulgg.weather_master_app.data.store.LocationStore
import com.pranshulgg.weather_master_app.data.store.WeatherBlocksStore
import com.pranshulgg.weather_master_app.data.store.WeatherStore
import com.pranshulgg.weather_master_app.data.store.WeatherUnitsStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Lazy
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): WeatherMasterDatabase =
        WeatherMasterDatabase.getInstance(context)


    @Provides
    @Singleton
    fun provideWeatherContextRepository(
        dao: WeatherContextDao,
        airQualityDao: AirQualityDao,
        nominatimRepository: NominatimRepository,
        @ApplicationContext context: Context,
        alertsDao: AlertsDao,
        sourceManager: SourceManager
    ): WeatherContextRepository = WeatherContextRepository(
        dao,
        airQualityDao,
        context,
        nominatimRepository,
        alertsDao,
        sourceManager
    )


    @Provides
    @Singleton
    fun provideWeatherUnitsRepository(dao: WeatherUnitsDao): WeatherUnitsRepository =
        WeatherUnitsRepository(dao)

    @Provides
    @Singleton
    fun provideWeatherBlocksRepository(
        weatherBlocksDao: WeatherBlocksDao
    ): WeatherBlocksRepository =
        WeatherBlocksRepository(weatherBlocksDao)

    @Provides
    @Singleton
    fun provideGithubRepository(
        api: GithubApi,
        dao: GithubDao
    ): GithubRepository = GithubRepository(api, dao)

    @Provides
    @Singleton
    fun provideNominatimRepository(api: NominatimApi): NominatimRepository =
        NominatimRepository(api)

    @Provides
    @Singleton
    fun provideApiKeysRepository(dao: ApiKeysDao): ApiKeysRepository = ApiKeysRepository(dao)

    @Provides
    @Singleton
    fun provideSourceManager(
        nwsDao: NwsDao,
        locationKeysDao: LocationKeysDao,
        airQualityDao: AirQualityDao,
        alertsDao: AlertsDao,
        weatherContextRepository: Provider<WeatherContextRepository>,
        pendingRequests: PendingRequests,
        locationStore: LocationStore
    ): SourceManager {
        return SourceManager(
            nwsDao,
            locationKeysDao,
            airQualityDao,
            alertsDao,
            weatherContextRepository,
            pendingRequests,
            locationStore
        )
    }

    @Provides
    @Singleton
    fun provideLocationManager(
        weatherContextRepository: WeatherContextRepository,
        locationStore: LocationStore,
        initializationStore: InitializationStore,
        pendingRequests: PendingRequests
    ): LocationManager {
        return LocationManager(
            weatherContextRepository,
            locationStore,
            initializationStore,
            pendingRequests
        )
    }

    @Provides
    @Singleton
    fun provideWeatherBlocksManager(
        weatherBlocksRepository: WeatherBlocksRepository,
        weatherBlocksStore: WeatherBlocksStore
    ): WeatherBlocksManager {
        return WeatherBlocksManager(
            weatherBlocksRepository,
            weatherBlocksStore
        )
    }

    @Provides
    @Singleton
    fun provideWeatherManager(
        weatherContextRepository: WeatherContextRepository,
        sourceDataRepository: SourceDataRepository,
        weatherStore: WeatherStore,
        locationStore: LocationStore,
        initializationStore: InitializationStore,
        externalManager: ExternalManager
    ): WeatherManager {
        return WeatherManager(
            weatherContextRepository,
            sourceDataRepository,
            weatherStore,
            locationStore,
            initializationStore,
            externalManager
        )
    }

    @Provides
    @Singleton
    fun provideWeatherUnitsManager(
        weatherUnitsStore: WeatherUnitsStore,
        weatherUnitsRepository: WeatherUnitsRepository
    ): WeatherUnitsManager {
        return WeatherUnitsManager(
            weatherUnitsStore,
            weatherUnitsRepository
        )
    }
}