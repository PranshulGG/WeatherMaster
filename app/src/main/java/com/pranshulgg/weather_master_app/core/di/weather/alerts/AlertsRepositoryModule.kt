package com.pranshulgg.weather_master_app.core.di.weather.alerts

import com.pranshulgg.weather_master_app.core.network.sources.alerts.fpas.FpasApi
import com.pranshulgg.weather_master_app.core.network.sources.alerts.fpas.FpasRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi.AlertsWeatherApi
import com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi.AlertsWeatherApiRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.WmoSevereWeatherApi
import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.WmoSevereWeatherRepository
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
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


}