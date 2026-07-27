package com.pranshulgg.weather_master_app.data.provider

import com.pranshulgg.weather_master_app.core.model.sources.AirQualitySource
import com.pranshulgg.weather_master_app.core.network.sources.airquality.accu.AccuAqiRepository
import com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo.OpenMeteoAqiRepository
import com.pranshulgg.weather_master_app.data.repository.AirQualityRepository
import javax.inject.Inject


class AirQualityRepositoryProvider @Inject constructor(
    private val openMeteoAqiRepository: OpenMeteoAqiRepository,
    private val accuAqiRepository: AccuAqiRepository
) {

    fun getRepository(source: AirQualitySource): AirQualityRepository? {
        return when (source) {
            AirQualitySource.ACCU_WEATHER -> accuAqiRepository
            AirQualitySource.OPEN_METEO -> openMeteoAqiRepository
            AirQualitySource.NONE -> null
        }
    }

}