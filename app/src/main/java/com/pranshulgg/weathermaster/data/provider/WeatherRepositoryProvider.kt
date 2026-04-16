package com.pranshulgg.weathermaster.data.provider

import com.pranshulgg.weathermaster.core.model.WeatherProviders
import com.pranshulgg.weathermaster.core.network.openmeteo.OpenMeteoApi
import com.pranshulgg.weathermaster.core.network.openmeteo.OpenMeteoRepository
import com.pranshulgg.weathermaster.data.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryProvider @Inject constructor(
    private val openMeteoRepo: OpenMeteoRepository
) {

    fun getRepository(provider: WeatherProviders): WeatherRepository {
        return when (provider) {
            WeatherProviders.OPEN_METEO -> openMeteoRepo
        }
    }

}