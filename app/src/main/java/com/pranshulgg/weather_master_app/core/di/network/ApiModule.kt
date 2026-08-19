package com.pranshulgg.weather_master_app.core.di.network

import com.pranshulgg.weather_master_app.core.network.github.GithubApi
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.NominatimApi
import com.pranshulgg.weather_master_app.core.network.sources.alerts.fpas.FpasApi
import com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi.AlertsWeatherApi
import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.WmoSevereWeatherApi
import com.pranshulgg.weather_master_app.core.network.sources.search.accu.AccuSearchApi
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.GeoNamesSearchApi
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.timezone.GeoNamesTimezoneApi
import com.pranshulgg.weather_master_app.core.network.sources.search.openmeteo.OpenMeteoSearchApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.AccuApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.AemetApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.BmkgApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.china.ChinaApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.CwaApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.JmaApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.DwdApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.EcccApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.FmiApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.GismeteoApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.imd.ImdApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.IpmaApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.MeteoamApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.MeteoFranceApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.MetOfficeApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.airquality.OpenMeteoAqiApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.PirateWeatherApi
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

    @Provides
    @Singleton
    fun provideFmiApi(): FmiApi = FmiApi.create()

    @Provides
    @Singleton
    fun provideChinaApi(): ChinaApi = ChinaApi.create()

    @Provides
    @Singleton
    fun provideBmkgApi(): BmkgApi = BmkgApi.create()

    @Provides
    @Singleton
    fun provideAccuApi(): AccuApi = AccuApi.create()

    @Provides
    @Singleton
    fun provideMeteoamApi(): MeteoamApi = MeteoamApi.create()

    @Provides
    @Singleton
    fun provideAlertsWeatherApi(): AlertsWeatherApi = AlertsWeatherApi.create()

    @Provides
    @Singleton
    fun provideIpmaApi(): IpmaApi = IpmaApi.create()

    @Provides
    @Singleton
    fun provideAccuSearchApi(): AccuSearchApi = AccuSearchApi.create()

    @Provides
    @Singleton
    fun provideWmoSevereWeatherAlertsApi(): WmoSevereWeatherApi = WmoSevereWeatherApi.create()

    @Provides
    @Singleton
    fun provideFpasAlertsApi(): FpasApi = FpasApi.create()

    @Provides
    @Singleton
    fun provideGismeteoApi(): GismeteoApi = GismeteoApi.create()

    @Provides
    @Singleton
    fun provideMetOfficeApi(): MetOfficeApi = MetOfficeApi.create()

    @Provides
    @Singleton
    fun provideAemetApi(): AemetApi = AemetApi.create()

    @Provides
    @Singleton
    fun provideImdApi(): ImdApi = ImdApi.create()

    @Provides
    @Singleton
    fun providePirateWeatherApi(): PirateWeatherApi = PirateWeatherApi.create()

    @Provides
    @Singleton
    fun provideCwaApi(): CwaApi = CwaApi.create()

    @Provides
    @Singleton
    fun provideJmaApi(): JmaApi = JmaApi.create()
}