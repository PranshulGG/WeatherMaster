package com.pranshulgg.weather_master_app.data.provider

import com.pranshulgg.weather_master_app.core.model.sources.AlertSource
import com.pranshulgg.weather_master_app.core.network.sources.alerts.accu.AlertsAccuRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.fpas.FpasRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.pirateweather.PirateWeatherAlertsRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi.AlertsWeatherApiRepository
import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.WmoSevereWeatherRepository
import com.pranshulgg.weather_master_app.data.repository.AlertRepository
import javax.inject.Inject


class AlertsRepositoryProvider @Inject constructor(
    private val alertsAccuRepository: AlertsAccuRepository,
    private val alertsWeatherApiRepository: AlertsWeatherApiRepository,
    private val wmoSevereWeatherRepository: WmoSevereWeatherRepository,
    private val fpasRepository: FpasRepository,
    private val pirateWeatherAlertsRepository: PirateWeatherAlertsRepository
) {

    fun getRepository(source: AlertSource): AlertRepository? {
        return when (source) {
            AlertSource.ACCU_WEATHER -> alertsAccuRepository
            AlertSource.WEATHER_API -> alertsWeatherApiRepository
            AlertSource.WMO_SEVERE_WEATHER -> wmoSevereWeatherRepository
            AlertSource.FPAS -> fpasRepository
            AlertSource.PIRATE_WEATHER -> pirateWeatherAlertsRepository
            AlertSource.NONE -> null
        }
    }

}