package com.pranshulgg.weather_master_app.core.di.weather.radar

import com.pranshulgg.weather_master_app.core.network.sources.radar.librewxr.LibreWxrRadarRepository
import com.pranshulgg.weather_master_app.data.repository.radar.RadarRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RadarRepositoryModule {

    // LibreWXR is the only radar source for now
    @Provides
    @Singleton
    fun provideRadarRepository(libreWxr: LibreWxrRadarRepository): RadarRepository = libreWxr
}
