package com.pranshulgg.weather_master_app.core.di.db

import com.pranshulgg.weather_master_app.data.local.WeatherMasterDatabase
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun provideWeatherContextDao(db: WeatherMasterDatabase) =
        db.weatherContextDao()

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
    fun provideGithubDao(db: WeatherMasterDatabase) = db.githubDao()

    @Provides
    fun provideLocationKeysDao(db: WeatherMasterDatabase) = db.locationKeysDao()

    @Provides
    fun provideAlertsDao(db: WeatherMasterDatabase) = db.alertsDao()

    @Provides
    fun provideApiKeysDao(db: WeatherMasterDatabase) = db.apiKeysDao()
}