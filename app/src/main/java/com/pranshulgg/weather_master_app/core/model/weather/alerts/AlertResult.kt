package com.pranshulgg.weather_master_app.core.model.weather.alerts

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.data.repository.data.AlertsAdditionalData
import com.pranshulgg.weather_master_app.data.repository.data.WeatherAdditionalData


sealed class AlertResult {
    data class Success(val alerts: List<Alert>) : AlertResult()
    data class Error(val exception: Exception, val alerts: List<Alert> = emptyList()) :
        AlertResult()
}

data class FinishedAlertsResult(
    val alerts: List<Alert>
)

data class AlertsDataPack(
    val alerts: List<Alert>,
    val location: Location,
    val additionalData: AlertsAdditionalData? = null,
)


enum class AlertResultType {
    RETURN_CACHE,
    ERROR
}