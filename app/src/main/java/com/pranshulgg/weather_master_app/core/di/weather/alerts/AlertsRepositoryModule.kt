package com.pranshulgg.weather_master_app.core.di.weather.alerts

import com.pranshulgg.weather_master_app.core.network.sources.alerts.fpas.FpasApi
import com.pranshulgg.weather_master_app.core.network.sources.alerts.fpas.FpasRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi.AlertsWeatherApi
import com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi.AlertsWeatherApiRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.WmoSevereWeatherApi
import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.WmoSevereWeatherRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.AccuApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.repository.AccuWeatherAlertRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.repository.NwsAlertRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.PirateWeatherApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.repository.PirateWeatherAlertRepository
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AlertsRepositoryModule {

    @Provides
    @Singleton
    fun provideWmoSevereWeatherAlertsRepository(
        api: WmoSevereWeatherApi,
        dao: AlertsDao,
        weatherContextDao: WeatherContextDao
    ): WmoSevereWeatherRepository = WmoSevereWeatherRepository(api, dao, weatherContextDao)

    @Provides
    @Singleton
    fun provideFpasAlertsRepository(
        api: FpasApi,
        dao: AlertsDao,
        weatherContextDao: WeatherContextDao
    ): FpasRepository = FpasRepository(api, dao, weatherContextDao)


    @Provides
    @Singleton
    fun provideWeatherApiAlertsRepository(
        api: AlertsWeatherApi,
        dao: AlertsDao,
        weatherContextDao: WeatherContextDao
    ): AlertsWeatherApiRepository = AlertsWeatherApiRepository(api, dao, weatherContextDao)


    @Provides
    @Singleton
    fun provideNwsAlertRepository(
        api: NwsApi,
        alertsDao: AlertsDao,
        weatherContextDao: WeatherContextDao
    ): NwsAlertRepository = NwsAlertRepository(weatherContextDao, api, alertsDao)


    @Provides
    @Singleton
    fun providePirateWeatherAlertRepository(
        api: PirateWeatherApi,
        alertsDao: AlertsDao,
        weatherContextDao: WeatherContextDao
    ): PirateWeatherAlertRepository =
        PirateWeatherAlertRepository(weatherContextDao, api, alertsDao)

    @Provides
    @Singleton
    fun provideAccuWeatherAlertRepository(
        api: AccuApi,
        alertsDao: AlertsDao,
        weatherContextDao: WeatherContextDao,
        locationKeysDao: LocationKeysDao
    ): AccuWeatherAlertRepository =
        AccuWeatherAlertRepository(weatherContextDao, api, locationKeysDao, alertsDao)
}