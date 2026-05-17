package com.pranshulgg.weathermaster.data.provider

import com.pranshulgg.weathermaster.core.model.providers.WeatherProvider
import com.pranshulgg.weathermaster.core.network.openmeteo.OpenMeteoRepository
import com.pranshulgg.weathermaster.data.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryProvider @Inject constructor(
    private val openMeteoRepo: OpenMeteoRepository
) {

    fun getRepository(provider: WeatherProvider): WeatherRepository {
        return when (provider) {
            WeatherProvider.OPEN_METEO -> openMeteoRepo
        }
    }

}