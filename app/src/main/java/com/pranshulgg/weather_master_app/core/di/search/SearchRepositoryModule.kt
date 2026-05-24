package com.pranshulgg.weather_master_app.core.di.search

import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.GeoNamesSearchApi
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.GeoNamesSearchRepository
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.GeoNamesTimezoneRepository
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.timezone.GeoNamesTimezoneApi
import com.pranshulgg.weather_master_app.core.network.sources.search.openmeteo.OpenMeteoSearchApi
import com.pranshulgg.weather_master_app.core.network.sources.search.openmeteo.OpenMeteoSearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchRepositoryModule {
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

}