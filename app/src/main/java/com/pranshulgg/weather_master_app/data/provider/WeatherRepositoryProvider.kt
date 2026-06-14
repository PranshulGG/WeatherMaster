package com.pranshulgg.weather_master_app.data.provider

import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.DwdRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.EcccRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.MeteoFranceRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.SmhiRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryProvider @Inject constructor(
    private val openMeteoRepository: OpenMeteoRepository,
    private val nwsRepository: NwsRepository,
    private val metNorwayRepository: MetNorwayRepository,
    private val smhiRepository: SmhiRepository,
    private val dwdRepository: DwdRepository,
    private val meteoFranceRepository: MeteoFranceRepository,
    private val ecccRepository: EcccRepository
) {

    fun getRepository(source: WeatherSource): WeatherRepository {
        return when (source) {
            WeatherSource.OPEN_METEO -> openMeteoRepository
            WeatherSource.NWS -> nwsRepository
            WeatherSource.MET_NORWAY -> metNorwayRepository
            WeatherSource.SMHI -> smhiRepository
            WeatherSource.DWD -> dwdRepository
            WeatherSource.METEO_FRANCE -> meteoFranceRepository
            WeatherSource.ECCC -> ecccRepository
        }
    }

}