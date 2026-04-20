package com.pranshulgg.weathermaster.core.di

import android.content.Context
import com.pranshulgg.weathermaster.core.network.openmeteo.OpenMeteoApi
import com.pranshulgg.weathermaster.core.network.openmeteo.OpenMeteoRepository
import com.pranshulgg.weathermaster.core.network.search.OpenMeteoSearchApi
import com.pranshulgg.weathermaster.core.ui.state.ActiveLocationStore
import com.pranshulgg.weathermaster.data.local.WeatherMasterDatabase
import com.pranshulgg.weathermaster.data.local.dao.AppWeatherUnitsDao
import com.pranshulgg.weathermaster.data.local.dao.WeatherDataDao
import com.pranshulgg.weathermaster.data.local.dao.WeatherLocationDao
import com.pranshulgg.weathermaster.data.repository.AppWeatherUnitsRepository
import com.pranshulgg.weathermaster.data.repository.LocationsRepository
import com.pranshulgg.weathermaster.data.repository.SearchRepository
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
    @Singleton
    fun provideOpenMeteoApi(): OpenMeteoApi = OpenMeteoApi.create()

    @Provides
    @Singleton
    fun provideOpenMeteoSearchApi(): OpenMeteoSearchApi = OpenMeteoSearchApi.create()


    @Provides
    @Singleton
    fun provideOpenMeteoRepository(
        dao: WeatherDataDao,
        api: OpenMeteoApi
    ): OpenMeteoRepository = OpenMeteoRepository(dao, api)

    @Provides
    @Singleton
    fun provideSearchRepository(
        api: OpenMeteoSearchApi
    ): SearchRepository = SearchRepository(api)

    @Provides
    @Singleton
    fun provideLocationsRepository(
        dao: WeatherLocationDao,
        activeLocationStore: ActiveLocationStore
    ): LocationsRepository = LocationsRepository(dao, activeLocationStore)

    @Provides
    @Singleton
    fun provideActiveLocationStore(): ActiveLocationStore = ActiveLocationStore()

    @Provides
    @Singleton
    fun provideAppWeatherUnitsRepositort(dao: AppWeatherUnitsDao): AppWeatherUnitsRepository =
        AppWeatherUnitsRepository(dao)
}