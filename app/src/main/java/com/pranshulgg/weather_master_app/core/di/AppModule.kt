package com.pranshulgg.weather_master_app.core.di

import android.content.Context
import com.pranshulgg.weather_master_app.core.network.github.GithubApi
import com.pranshulgg.weather_master_app.core.network.github.GithubRepository
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.NominatimApi
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json.NominatimRepository
import com.pranshulgg.weather_master_app.data.local.WeatherMasterDatabase
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.github.GithubDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherBlocksDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherUnitsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
import com.pranshulgg.weather_master_app.data.repository.ApiKeysRepository
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherDataReconcilerRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
    fun provideLocationsRepository(
        dao: LocationsDao,
        airQualityDao: AirQualityDao,
        nominatimRepository: NominatimRepository,
        @ApplicationContext context: Context
    ): LocationsRepository = LocationsRepository(dao, airQualityDao, context, nominatimRepository)


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
    fun provideWeatherDataReconcilerRepository(
        nwsDao: NwsDao,
        locationsDao: LocationsDao,
        locationKeysDao: LocationKeysDao,
        airQualityDao: AirQualityDao,
        alertsDao: AlertsDao
    ): WeatherDataReconcilerRepository =
        WeatherDataReconcilerRepository(
            nwsDao,
            locationsDao,
            locationKeysDao,
            airQualityDao,
            alertsDao
        )

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
}