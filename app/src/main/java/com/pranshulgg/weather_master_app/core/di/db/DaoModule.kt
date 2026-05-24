package com.pranshulgg.weather_master_app.core.di.db

import com.pranshulgg.weather_master_app.data.local.WeatherMasterDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
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
}