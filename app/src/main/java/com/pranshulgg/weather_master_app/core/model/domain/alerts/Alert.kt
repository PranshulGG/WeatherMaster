package com.pranshulgg.weather_master_app.core.model.domain.alerts

import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity


data class Alert(
    val locationId: String,
    val event: String,
    val severity: AlertSeverity?,
    val effective: Long,
    val expires: Long,
    val description: String,
    val source: String?,
    val lastUpdatedInMilli: Long
)