package com.pranshulgg.weather_master_app.core.di.network

import com.pranshulgg.weather_master_app.core.network.github.GithubApi
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.NominatimApi
import com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo.OpenMeteoAqiApi
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.GeoNamesSearchApi
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.timezone.GeoNamesTimezoneApi
import com.pranshulgg.weather_master_app.core.network.sources.search.openmeteo.OpenMeteoSearchApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.DwdApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.EcccApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.MeteoFranceApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.SmhiApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideOpenMeteoApi(): OpenMeteoApi = OpenMeteoApi.create()

    @Provides
    @Singleton
    fun provideOpenMeteoSearchApi(): OpenMeteoSearchApi = OpenMeteoSearchApi.create()

    @Provides
    @Singleton
    fun provideGeoNamesSearchApi(): GeoNamesSearchApi = GeoNamesSearchApi.create()

    @Provides
    @Singleton
    fun provideGeoNamesTimezoneApi(): GeoNamesTimezoneApi = GeoNamesTimezoneApi.create()

    @Provides
    @Singleton
    fun provideOpenMeteoAqiApi(): OpenMeteoAqiApi = OpenMeteoAqiApi.create()

    @Provides
    @Singleton
    fun provideNwsApi(): NwsApi = NwsApi.create()

    @Provides
    @Singleton
    fun provideMetNorwayApi(): MetNorwayApi = MetNorwayApi.create()

    @Provides
    @Singleton
    fun provideSmhiApi(): SmhiApi = SmhiApi.create()

    @Provides
    @Singleton
    fun provideGithubApi(): GithubApi = GithubApi.create()

    @Provides
    @Singleton
    fun provideNominatimApi(): NominatimApi = NominatimApi.create()

    @Provides
    @Singleton
    fun provideDwdApi(): DwdApi = DwdApi.create()

    @Provides
    @Singleton
    fun provideMeteoFranceApi(): MeteoFranceApi = MeteoFranceApi.create()

    @Provides
    @Singleton
    fun provideEcccApi(): EcccApi = EcccApi.create()
}