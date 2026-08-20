package com.pranshulgg.weather_master_app.data.provider

import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.network.sources.alerts.fpas.FpasRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi.AlertsWeatherApiRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.WmoSevereWeatherRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.AccuRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.AemetRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.BmkgRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.china.ChinaRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.CwaRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.DwdRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.EcccRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.FmiRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.GismeteoRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.imd.ImdRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.InmetRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.IpmaRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.JmaRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.MeteoamRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.MeteoFranceRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.MetOfficeRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.PirateWeatherRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.SmhiRepository
import com.pranshulgg.weather_master_app.data.repository.data.AirQualityRepository
import com.pranshulgg.weather_master_app.data.repository.data.AlertRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherRepository
import javax.inject.Inject

class SourceRepositoryProvider @Inject constructor(
    private val openMeteoRepository: OpenMeteoRepository,
    private val nwsRepository: NwsRepository,
    private val metNorwayRepository: MetNorwayRepository,
    private val smhiRepository: SmhiRepository,
    private val dwdRepository: DwdRepository,
    private val meteoFranceRepository: MeteoFranceRepository,
    private val ecccRepository: EcccRepository,
    private val fmiRepository: FmiRepository,
    private val chinaRepository: ChinaRepository,
    private val bmkgRepository: BmkgRepository,
    private val accuRepository: AccuRepository,
    private val meteoamRepository: MeteoamRepository,
    private val ipmaRepository: IpmaRepository,
    private val gismeteoRepository: GismeteoRepository,
    private val metOfficeRepository: MetOfficeRepository,
    private val aemetRepository: AemetRepository,
    private val imdRepository: ImdRepository,
    private val pirateWeatherRepository: PirateWeatherRepository,
    private val cwaRepository: CwaRepository,
    private val jmaRepository: JmaRepository,
    private val inmetRepository: InmetRepository,

    // ALERTS
    private val alertsWeatherApiRepository: AlertsWeatherApiRepository,
    private val wmoSevereWeatherRepository: WmoSevereWeatherRepository,
    private val fpasRepository: FpasRepository,

    ) {

    val repositories = listOf(
        openMeteoRepository,
        nwsRepository,
        metNorwayRepository,
        smhiRepository,
        dwdRepository,
        meteoFranceRepository,
        ecccRepository,
        fmiRepository,
        chinaRepository,
        bmkgRepository,
        accuRepository,
        meteoamRepository,
        ipmaRepository,
        gismeteoRepository,
        metOfficeRepository,
        aemetRepository,
        imdRepository,
        pirateWeatherRepository,
        cwaRepository,
        jmaRepository,
        inmetRepository,
        alertsWeatherApiRepository,
        wmoSevereWeatherRepository,
        fpasRepository,
    )

    fun getWeatherRepository(source: Source): WeatherRepository {
        return repositories.filterIsInstance<WeatherRepository>().firstOrNull {
            it.weatherSource == source
        } ?: openMeteoRepository
    }

    fun getAlertRepository(source: Source): AlertRepository? {
        return repositories.filterIsInstance<AlertRepository>().firstOrNull {
            it.alertSource == source
        }
    }

    fun getAirQualityRepository(source: Source): AirQualityRepository? {
        return repositories.filterIsInstance<AirQualityRepository>().firstOrNull {
            it.airQualitySource == source
        }
    }

}