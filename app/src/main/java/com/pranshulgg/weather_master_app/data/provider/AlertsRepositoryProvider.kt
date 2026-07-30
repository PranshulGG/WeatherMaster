package com.pranshulgg.weather_master_app.data.provider

import com.pranshulgg.weather_master_app.core.model.sources.AlertSource
import com.pranshulgg.weather_master_app.core.network.sources.alerts.accu.AlertsAccuRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi.AlertsWeatherApiRepository
import com.pranshulgg.weather_master_app.data.repository.AlertRepository
import javax.inject.Inject


class AlertsRepositoryProvider @Inject constructor(
    private val alertsAccuRepository: AlertsAccuRepository,
    private val alertsWeatherApiRepository: AlertsWeatherApiRepository
) {

    fun getRepository(source: AlertSource): AlertRepository? {
        return when (source) {
            AlertSource.ACCU_WEATHER -> alertsAccuRepository
            AlertSource.WEATHER_API -> alertsWeatherApiRepository
            AlertSource.NONE -> null
        }
    }

}