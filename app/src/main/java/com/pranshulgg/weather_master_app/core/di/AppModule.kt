package com.pranshulgg.weather_master_app.core.di

import android.content.Context
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
    fun provideLocationDao(db: WeatherMasterDatabase) =
        db.locationsDao()

    @Provides
    fun provideWeatherDataDao(db: WeatherMasterDatabase) =
        db.weatherDao()

    @Provides
    fun provideWeatherUnitsDao(db: WeatherMasterDatabase) =
        db.weatherUnitsDao()

    @Provides
    fun provideWeatherBlocksDao(db: WeatherMasterDatabase) =
        db.weatherBlocksDao()

    @Provides
    fun provideAirQualityDao(db: WeatherMasterDatabase) = db.airQualityDao()

    @Provides
    fun provideNwsDao(db: WeatherMasterDatabase) = db.nwsDao()

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
    fun provideOpenMeteoAqiApi(): OpenMeteoAqiApi = OpenMeteoAqiApi.create()

    @Provides
    @Singleton
    fun provideNwsApi(): NwsApi = NwsApi.create()

    @Provides
    @Singleton
    fun provideMetNorwayApi(): MetNorwayApi = MetNorwayApi.create()

    @Provides
    @Singleton
    fun provideOpenMeteoRepository(
        dao: LocationsDao,
        api: OpenMeteoApi,
        weatherDao: WeatherDao
    ): OpenMeteoRepository = OpenMeteoRepository(dao, weatherDao, api)


    @Provides
    @Singleton
    fun provideLocationsRepository(
        dao: LocationsDao,
        airQualityDao: AirQualityDao,
        @ApplicationContext context: Context
    ): LocationsRepository = LocationsRepository(dao, airQualityDao, context)


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

    @Provides
    @Singleton
    fun provideOpenMeteoAqiRepository(
        api: OpenMeteoAqiApi,
        dao: AirQualityDao
    ): OpenMeteoAqiRepository =
        OpenMeteoAqiRepository(api, dao)

    @Provides
    @Singleton
    fun provideNwsRepository(
        api: NwsApi,
        dao: LocationsDao,
        weatherDao: WeatherDao,
        nwsDao: NwsDao
    ): NwsRepository = NwsRepository(dao, weatherDao, nwsDao, api)

    @Provides
    @Singleton
    fun provideMetNorwayRepository(
        dao: LocationsDao,
        api: MetNorwayApi,
        weatherDao: WeatherDao
    ): MetNorwayRepository = MetNorwayRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideWeatherDataReconcilerRepository(
        nwsDao: NwsDao,
        locationsDao: LocationsDao
    ): WeatherDataReconcilerRepository = WeatherDataReconcilerRepository(nwsDao, locationsDao)
}