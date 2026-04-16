package com.pranshulgg.weathermaster.core.di

import android.content.Context
import com.pranshulgg.weathermaster.core.network.openmeteo.OpenMeteoApi
import com.pranshulgg.weathermaster.core.network.openmeteo.OpenMeteoRepository
import com.pranshulgg.weathermaster.data.local.WeatherMasterDatabase
import com.pranshulgg.weathermaster.data.local.dao.WeatherDataDao
import com.pranshulgg.weathermaster.data.repository.WeatherRepository
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
    fun provideWeatherDataDao(db: WeatherMasterDatabase) =
        db.weatherDataDao()

    @Provides
    @Singleton
    fun provideOpenMeteoApi(): OpenMeteoApi = OpenMeteoApi.create()


    @Provides
    @Singleton
    fun provideOpenMeteoRepository(
        dao: WeatherDataDao,
        api: OpenMeteoApi
    ): WeatherRepository =
        OpenMeteoRepository(dao, api)

}