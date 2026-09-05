package com.pranshulgg.weather_master_app.core.di.weather

import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.airquality.OpenMeteoAqiApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.AccuApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.AccuRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.AemetApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.AemetRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.BmkgApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.BmkgRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.china.ChinaApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.china.ChinaRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.CwaApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.CwaRepository
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
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.IbgeApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.InmetAvisosApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.InmetForecastApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.InmetObservationApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.InmetRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.JmaApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.JmaRepository
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
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.MgmApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.MgmRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.OpenWeatherApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.OpenWeatherRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.PirateWeatherApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.PirateWeatherRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.SmhiApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.SmhiRepository
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
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
        dao: WeatherContextDao,
        api: OpenMeteoApi,
        weatherDao: WeatherDao,
        airQualityDao: AirQualityDao,
        airQualityApi: OpenMeteoAqiApi
    ): OpenMeteoRepository = OpenMeteoRepository(dao, weatherDao, api, airQualityApi, airQualityDao)


    @Provides
    @Singleton
    fun provideNwsRepository(
        api: NwsApi,
        dao: WeatherContextDao,
        weatherDao: WeatherDao,
        nwsDao: NwsDao,
        alertsDao: AlertsDao
    ): NwsRepository = NwsRepository(dao, weatherDao, nwsDao, api, alertsDao)

    @Provides
    @Singleton
    fun provideMetNorwayRepository(
        dao: WeatherContextDao,
        api: MetNorwayApi,
        weatherDao: WeatherDao
    ): MetNorwayRepository = MetNorwayRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideSmhiRepository(
        dao: WeatherContextDao,
        api: SmhiApi,
        weatherDao: WeatherDao
    ): SmhiRepository = SmhiRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideDwdRepository(
        dao: WeatherContextDao,
        api: DwdApi,
        weatherDao: WeatherDao
    ): DwdRepository = DwdRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideMeteoFranceRepository(
        dao: WeatherContextDao,
        api: MeteoFranceApi,
        weatherDao: WeatherDao
    ): MeteoFranceRepository = MeteoFranceRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideEcccRepository(
        dao: WeatherContextDao,
        api: EcccApi,
        weatherDao: WeatherDao
    ): EcccRepository = EcccRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideFmiRepository(
        dao: WeatherContextDao,
        api: FmiApi,
        weatherDao: WeatherDao
    ): FmiRepository = FmiRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideChinaRepository(
        dao: WeatherContextDao,
        api: ChinaApi,
        weatherDao: WeatherDao
    ): ChinaRepository = ChinaRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideBmkgRepository(
        dao: WeatherContextDao,
        api: BmkgApi,
        weatherDao: WeatherDao
    ): BmkgRepository = BmkgRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideAccuRepository(
        dao: WeatherContextDao,
        api: AccuApi,
        weatherDao: WeatherDao,
        locationKeysDao: LocationKeysDao,
        airQualityDao: AirQualityDao,
        alertsDao: AlertsDao,
        weatherContextDao: WeatherContextDao
    ): AccuRepository = AccuRepository(
        dao,
        weatherDao,
        api,
        locationKeysDao,
        airQualityDao,
        alertsDao,
        weatherContextDao
    )

    @Provides
    @Singleton
    fun provideMeteoamRepository(
        dao: WeatherContextDao,
        api: MeteoamApi,
        weatherDao: WeatherDao
    ): MeteoamRepository = MeteoamRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun provideIpmaRepository(
        dao: WeatherContextDao,
        api: IpmaApi,
        weatherDao: WeatherDao,
        locationKeysDao: LocationKeysDao
    ): IpmaRepository = IpmaRepository(dao, weatherDao, api, locationKeysDao)


    @Provides
    @Singleton
    fun provideGismeteoRepository(
        dao: WeatherContextDao,
        api: GismeteoApi,
        weatherDao: WeatherDao,
        locationKeysDao: LocationKeysDao
    ): GismeteoRepository = GismeteoRepository(dao, weatherDao, api, locationKeysDao)

    @Provides
    @Singleton
    fun provideMetOfficeRepository(
        dao: WeatherContextDao,
        api: MetOfficeApi,
        weatherDao: WeatherDao,
        apiKeysDao: ApiKeysDao
    ): MetOfficeRepository = MetOfficeRepository(dao, weatherDao, api, apiKeysDao)

    @Provides
    @Singleton
    fun provideAemetRepository(
        dao: WeatherContextDao,
        api: AemetApi,
        weatherDao: WeatherDao,
        apiKeysDao: ApiKeysDao,
        locationKeysDao: LocationKeysDao
    ): AemetRepository = AemetRepository(dao, weatherDao, api, apiKeysDao, locationKeysDao)

    @Provides
    @Singleton
    fun provideImdRepository(
        dao: WeatherContextDao,
        weatherDao: WeatherDao,
        api: ImdApi
    ): ImdRepository = ImdRepository(dao, weatherDao, api)

    @Provides
    @Singleton
    fun providePirateWeatherRepository(
        dao: WeatherContextDao,
        api: PirateWeatherApi,
        weatherDao: WeatherDao,
        apiKeysDao: ApiKeysDao,
        alertsDao: AlertsDao
    ): PirateWeatherRepository =
        PirateWeatherRepository(dao, weatherDao, api, apiKeysDao, alertsDao)

    @Provides
    @Singleton
    fun provideCwaRepository(
        dao: WeatherContextDao,
        api: CwaApi,
        weatherDao: WeatherDao,
        apiKeysDao: ApiKeysDao,
        locationKeysDao: LocationKeysDao
    ): CwaRepository = CwaRepository(dao, weatherDao, api, apiKeysDao, locationKeysDao)

    @Provides
    @Singleton
    fun provideJmaRepository(
        dao: WeatherContextDao,
        weatherDao: WeatherDao,
        api: JmaApi,
        locationKeysDao: LocationKeysDao,
        alertsDao: AlertsDao
    ): JmaRepository = JmaRepository(dao, weatherDao, api, locationKeysDao, alertsDao)

    @Provides
    @Singleton
    fun provideInmetRepository(
        dao: WeatherContextDao,
        weatherDao: WeatherDao,
        forecastApi: InmetForecastApi,
        observationApi: InmetObservationApi,
        ibgeApi: IbgeApi,
        avisosApi: InmetAvisosApi,
        locationKeysDao: LocationKeysDao,
        alertsDao: AlertsDao
    ): InmetRepository = InmetRepository(
        dao,
        weatherDao,
        forecastApi,
        observationApi,
        ibgeApi,
        avisosApi,
        locationKeysDao,
        alertsDao
    )

    @Provides
    @Singleton
    fun provideOpenWeatherRepository(
        dao: WeatherContextDao,
        weatherDao: WeatherDao,
        api: OpenWeatherApi,
        airQualityDao: AirQualityDao,
        apiKeysDao: ApiKeysDao
    ): OpenWeatherRepository =
        OpenWeatherRepository(dao, weatherDao, api, airQualityDao, apiKeysDao)

    @Provides
    @Singleton
    fun provideMgmRepository(
        dao: LocationsDao,
        weatherDao: WeatherDao,
        api: MgmApi,
        locationKeysDao: LocationKeysDao
    ): MgmRepository = MgmRepository(dao, weatherDao, api, locationKeysDao)
}