package com.pranshulgg.weather_master_app.data.provider

import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryProvider @Inject constructor(
    private val openMeteoRepository: OpenMeteoRepository,
    private val nwsRepository: NwsRepository,
    private val metNorwayRepository: MetNorwayRepository
) {

    fun getRepository(source: WeatherSource): WeatherRepository {
        return when (source) {
            WeatherSource.OPEN_METEO -> openMeteoRepository
            WeatherSource.NWS -> nwsRepository
            WeatherSource.MET_NORWAY -> metNorwayRepository
        }
    }

}