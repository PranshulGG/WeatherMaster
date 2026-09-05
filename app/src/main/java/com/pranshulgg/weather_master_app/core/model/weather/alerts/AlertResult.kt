package com.pranshulgg.weather_master_app.core.model.weather.alerts

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather


sealed class AlertResult {
    data class Success(val alerts: List<Alert>) : AlertResult()
    data class Error(val exception: Exception, val alerts: List<Alert> = emptyList()) :
        AlertResult()
}

data class FinishedAlertsResult(
    val alerts: List<Alert>
)


enum class AlertResultType {
    RETURN_CACHE,
    ERROR
}