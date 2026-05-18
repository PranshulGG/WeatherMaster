package com.pranshulgg.weathermaster.data.provider

import com.pranshulgg.weathermaster.core.model.sources.WeatherSource
import com.pranshulgg.weathermaster.core.network.sources.weather.openmeteo.OpenMeteoRepository
import com.pranshulgg.weathermaster.data.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryProvider @Inject constructor(
    private val openMeteoRepo: OpenMeteoRepository
) {

    fun getRepository(source: WeatherSource): WeatherRepository {
        return when (source) {
            WeatherSource.OPEN_METEO -> openMeteoRepo
        }
    }

}