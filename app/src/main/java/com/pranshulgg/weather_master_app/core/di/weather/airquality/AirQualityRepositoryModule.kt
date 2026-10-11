package com.pranshulgg.weather_master_app.core.di.weather.airquality

import com.pranshulgg.weather_master_app.core.network.sources.airquality.moenv.MoenvApi
import com.pranshulgg.weather_master_app.core.network.sources.airquality.moenv.MoenvRepository
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AirQualityRepositoryModule {
    @Provides
    @Singleton
    fun provideMoenvRepository(
        api: MoenvApi,
        airQualityDao: AirQualityDao
    ): MoenvRepository = MoenvRepository(api, airQualityDao)
}