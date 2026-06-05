package com.pranshulgg.weather_master_app.core.di.weather

import com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo.OpenMeteoAqiApi
import com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo.OpenMeteoAqiRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.DwdApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.DwdRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.MeteoFranceApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.MeteoFranceRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.SmhiApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.SmhiRepository
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherRepositoryModule {
    @Provides
    @Singleton
    fun provideOpenMeteoRepository(
        dao: LocationsDao,
        api: OpenMeteoApi,
        weatherDao: WeatherDao
    ): OpenMeteoRepository = OpenMeteoRepository(dao, weatherDao, api)

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
    fun provideSmhiRepository(
        dao: LocationsDao,
        api: SmhiApi,
        weatherDao: WeatherDao
    ): SmhiRepository = SmhiRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideDwdRepository(
        dao: LocationsDao,
        api: DwdApi,
        weatherDao: WeatherDao
    ): DwdRepository = DwdRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideMeteoFranceRepository(
        dao: LocationsDao,
        api: MeteoFranceApi,
        weatherDao: WeatherDao
    ): MeteoFranceRepository = MeteoFranceRepository(dao, weatherDao, api)
}