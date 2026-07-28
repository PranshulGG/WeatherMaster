package com.pranshulgg.weather_master_app.core.model.weather.alerts

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert


sealed class AlertResult {
    data class Success(val alerts: List<Alert>) : AlertResult()
    data class Error(val exception: Exception, val cacheAlerts: List<Alert> = emptyList()) :
        AlertResult()
}

enum class AlertResultType {
    RETURN_CACHE,
    ERROR
}