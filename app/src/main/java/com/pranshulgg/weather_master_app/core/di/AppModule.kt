package com.pranshulgg.weather_master_app.core.di

import android.content.Context
import com.pranshulgg.weather_master_app.core.network.github.GithubApi
import com.pranshulgg.weather_master_app.core.network.github.GithubRepository
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.NominatimApi
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json.NominatimRepository
import com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo.OpenMeteoAqiApi
import com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo.OpenMeteoAqiRepository
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.GeoNamesSearchApi
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.GeoNamesSearchRepository
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.timezone.GeoNamesTimezoneApi
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.GeoNamesTimezoneRepository
import com.pranshulgg.weather_master_app.core.network.sources.search.openmeteo.OpenMeteoSearchApi
import com.pranshulgg.weather_master_app.core.network.sources.search.openmeteo.OpenMeteoSearchRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoRepository
import com.pranshulgg.weather_master_app.data.local.WeatherMasterDatabase
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherBlocksDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherUnitsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
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
        locationsDao: LocationsDao
    ): WeatherDataReconcilerRepository = WeatherDataReconcilerRepository(nwsDao, locationsDao)

    @Provides
    @Singleton
    fun provideGithubRepository(
        api: GithubApi
    ): GithubRepository = GithubRepository(api)

    @Provides
    @Singleton
    fun provideNominatimRepository(api: NominatimApi): NominatimRepository =
        NominatimRepository(api)
}