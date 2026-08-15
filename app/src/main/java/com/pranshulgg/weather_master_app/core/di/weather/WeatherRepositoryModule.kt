package com.pranshulgg.weather_master_app.core.di.weather

import com.pranshulgg.weather_master_app.core.network.sources.airquality.accu.AccuAqiApi
import com.pranshulgg.weather_master_app.core.network.sources.airquality.accu.AccuAqiRepository
import com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo.OpenMeteoAqiApi
import com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo.OpenMeteoAqiRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.accu.AlertsAccuApi
import com.pranshulgg.weather_master_app.core.network.sources.alerts.accu.AlertsAccuRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.fpas.FpasApi
import com.pranshulgg.weather_master_app.core.network.sources.alerts.fpas.FpasRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi.AlertsWeatherApi
import com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi.AlertsWeatherApiRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.WmoSevereWeatherApi
import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.WmoSevereWeatherRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.AccuApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.AccuRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.BmkgApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.BmkgRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.china.ChinaApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.china.ChinaRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.DwdApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.DwdRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.EcccApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.EcccRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.FmiApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.FmiRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.GismeteoApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.GismeteoRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.imd.ImdApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.imd.ImdRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.IpmaApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.IpmaRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.MeteoamApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.MeteoamRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.MeteoFranceApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.MeteoFranceRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.MetOfficeApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.MetOfficeRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.SmhiApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.SmhiRepository
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherRepositoryModule {
    @Provides
    @Singleton
    fun provideOpenMeteoRepository(
        dao: LocationsDao,
        api: OpenMeteoApi,
        weatherDao: WeatherDao
    ): OpenMeteoRepository = OpenMeteoRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideOpenMeteoAqiRepository(
        api: OpenMeteoAqiApi,
        dao: AirQualityDao
    ): OpenMeteoAqiRepository =
        OpenMeteoAqiRepository(api, dao)

    @Provides
    @Singleton
    fun provideNwsRepository(
        api: NwsApi,
        dao: LocationsDao,
        weatherDao: WeatherDao,
        nwsDao: NwsDao
    ): NwsRepository = NwsRepository(dao, weatherDao, nwsDao, api)

    @Provides
    @Singleton
    fun provideMetNorwayRepository(
        dao: LocationsDao,
        api: MetNorwayApi,
        weatherDao: WeatherDao
    ): MetNorwayRepository = MetNorwayRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideSmhiRepository(
        dao: LocationsDao,
        api: SmhiApi,
        weatherDao: WeatherDao
    ): SmhiRepository = SmhiRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideDwdRepository(
        dao: LocationsDao,
        api: DwdApi,
        weatherDao: WeatherDao
    ): DwdRepository = DwdRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideMeteoFranceRepository(
        dao: LocationsDao,
        api: MeteoFranceApi,
        weatherDao: WeatherDao
    ): MeteoFranceRepository = MeteoFranceRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideEcccRepository(
        dao: LocationsDao,
        api: EcccApi,
        weatherDao: WeatherDao
    ): EcccRepository = EcccRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideFmiRepository(
        dao: LocationsDao,
        api: FmiApi,
        weatherDao: WeatherDao
    ): FmiRepository = FmiRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideChinaRepository(
        dao: LocationsDao,
        api: ChinaApi,
        weatherDao: WeatherDao
    ): ChinaRepository = ChinaRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideBmkgRepository(
        dao: LocationsDao,
        api: BmkgApi,
        weatherDao: WeatherDao
    ): BmkgRepository = BmkgRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideAccuRepository(
        dao: LocationsDao,
        api: AccuApi,
        weatherDao: WeatherDao,
        locationKeysDao: LocationKeysDao
    ): AccuRepository = AccuRepository(dao, weatherDao, api, locationKeysDao)

    @Provides
    @Singleton
    fun provideMeteoamRepository(
        dao: LocationsDao,
        api: MeteoamApi,
        weatherDao: WeatherDao
    ): MeteoamRepository = MeteoamRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideAccuAqiRepository(
        api: AccuAqiApi,
        dao: AirQualityDao,
        locationKeysDao: LocationKeysDao,
        accuApi: AccuApi
    ): AccuAqiRepository = AccuAqiRepository(api, dao, locationKeysDao, accuApi)

    @Provides
    @Singleton
    fun provideAccuAlertsRepository(
        api: AlertsAccuApi,
        dao: AlertsDao,
        locationKeysDao: LocationKeysDao,
        accuApi: AccuApi,
        locationsDao: LocationsDao
    ): AlertsAccuRepository = AlertsAccuRepository(api, dao, locationKeysDao, accuApi, locationsDao)

    @Provides
    @Singleton
    fun provideWeatherApiAlertsRepository(
        api: AlertsWeatherApi,
        dao: AlertsDao,
        locationsDao: LocationsDao
    ): AlertsWeatherApiRepository = AlertsWeatherApiRepository(api, dao, locationsDao)

    @Provides
    @Singleton
    fun provideIpmaRepository(
        dao: LocationsDao,
        api: IpmaApi,
        weatherDao: WeatherDao,
        locationKeysDao: LocationKeysDao
    ): IpmaRepository = IpmaRepository(dao, weatherDao, api, locationKeysDao)

    @Provides
    @Singleton
    fun provideWmoSevereWeatherAlertsRepository(
        api: WmoSevereWeatherApi,
        dao: AlertsDao,
        locationsDao: LocationsDao
    ): WmoSevereWeatherRepository = WmoSevereWeatherRepository(api, dao, locationsDao)

    @Provides
    @Singleton
    fun provideFpasAlertsRepository(
        api: FpasApi,
        dao: AlertsDao,
        locationsDao: LocationsDao
    ): FpasRepository = FpasRepository(api, dao, locationsDao)

    @Provides
    @Singleton
    fun provideGismeteoRepository(
        dao: LocationsDao,
        api: GismeteoApi,
        weatherDao: WeatherDao,
        locationKeysDao: LocationKeysDao
    ): GismeteoRepository = GismeteoRepository(dao, weatherDao, api, locationKeysDao)

    @Provides
    @Singleton
    fun provideMetOfficeRepository(
        dao: LocationsDao,
        api: MetOfficeApi,
        weatherDao: WeatherDao,
        apiKeysDao: ApiKeysDao
    ): MetOfficeRepository = MetOfficeRepository(dao, weatherDao, api, apiKeysDao)

    @Provides
    @Singleton
    fun provideImdRepository(
        dao: LocationsDao,
        weatherDao: WeatherDao,
        api: ImdApi
    ): ImdRepository = ImdRepository(dao, weatherDao, api)
}