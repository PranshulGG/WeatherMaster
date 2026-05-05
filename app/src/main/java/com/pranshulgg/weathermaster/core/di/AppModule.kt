package com.pranshulgg.weathermaster.core.di

import android.content.Context
import com.pranshulgg.weathermaster.core.network.openmeteo.OpenMeteoApi
import com.pranshulgg.weathermaster.core.network.openmeteo.OpenMeteoRepository
import com.pranshulgg.weathermaster.core.network.search.geonames.GeoNamesSearchApi
import com.pranshulgg.weathermaster.core.network.search.geonames.GeoNamesSearchRepository
import com.pranshulgg.weathermaster.core.network.search.geonames.GeoNamesTimezoneApi
import com.pranshulgg.weathermaster.core.network.search.geonames.GeoNamesTimezoneRepository
import com.pranshulgg.weathermaster.core.network.search.openmeteo.OpenMeteoSearchApi
import com.pranshulgg.weathermaster.core.network.search.openmeteo.OpenMeteoSearchRepository
import com.pranshulgg.weathermaster.data.local.WeatherMasterDatabase
import com.pranshulgg.weathermaster.data.local.dao.AppWeatherUnitsDao
import com.pranshulgg.weathermaster.data.local.dao.WeatherBlocksDao
import com.pranshulgg.weathermaster.data.local.dao.WeatherDataDao
import com.pranshulgg.weathermaster.data.local.dao.WeatherLocationDao
import com.pranshulgg.weathermaster.data.repository.AppWeatherUnitsRepository
import com.pranshulgg.weathermaster.data.repository.LocationsRepository
import com.pranshulgg.weathermaster.data.repository.SearchRepository
import com.pranshulgg.weathermaster.data.repository.WeatherDataRepository
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
    fun provideWeatherLocationDao(db: WeatherMasterDatabase) =
        db.weatherLocationDao()

    @Provides
    fun provideWeatherDataDao(db: WeatherMasterDatabase) =
        db.weatherDataDao()

    @Provides
    fun provideAppWeatherUnitsDao(db: WeatherMasterDatabase) =
        db.appWeatherUnitsDao()

    @Provides
    fun provideWeatherBlocksDao(db: WeatherMasterDatabase) =
        db.weatherBlocksDao()


    @Provides
    @Singleton
    fun provideOpenMeteoApi(): OpenMeteoApi = OpenMeteoApi.create()

    @Provides
    @Singleton
    fun provideOpenMeteoSearchApi(): OpenMeteoSearchApi = OpenMeteoSearchApi.create()

    @Provides
    @Singleton
    fun provideGeoNamesSearchApi(): GeoNamesSearchApi = GeoNamesSearchApi.create()

    @Provides
    @Singleton
    fun provideGeoNamesTimezoneApi(): GeoNamesTimezoneApi = GeoNamesTimezoneApi.create()

    @Provides
    @Singleton
    fun provideOpenMeteoRepository(
        dao: WeatherDataDao,
        api: OpenMeteoApi
    ): OpenMeteoRepository = OpenMeteoRepository(dao, api)


    @Provides
    @Singleton
    fun provideLocationsRepository(
        dao: WeatherLocationDao,
        @ApplicationContext context: Context
    ): LocationsRepository = LocationsRepository(dao, context)


    @Provides
    @Singleton
    fun provideAppWeatherUnitsRepositort(dao: AppWeatherUnitsDao): AppWeatherUnitsRepository =
        AppWeatherUnitsRepository(dao)

    @Provides
    @Singleton
    fun provideWeatherDataRepository(
        dao: WeatherDataDao,
        weatherBlocksDao: WeatherBlocksDao
    ): WeatherDataRepository =
        WeatherDataRepository(dao, weatherBlocksDao)

    @Provides
    @Singleton
    fun provideOpenMeteoSearchRepository(
        api: OpenMeteoSearchApi
    ): OpenMeteoSearchRepository = OpenMeteoSearchRepository(api)

    @Provides
    @Singleton
    fun provideGeoNamesSearchRepository(
        api: GeoNamesSearchApi
    ): GeoNamesSearchRepository = GeoNamesSearchRepository(api)

    @Provides
    @Singleton
    fun provideGeoNamesTimezoneRepository(api: GeoNamesTimezoneApi): GeoNamesTimezoneRepository =
        GeoNamesTimezoneRepository(api)
}