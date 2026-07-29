package com.pranshulgg.weather_master_app.data.local.mapper.alerts.sources.weatherapi

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi.json.AlertsWeatherApiJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds


fun AlertsWeatherApiJson.toDomain(locationId: String): List<Alert> {
    return this.alerts.alert.map {

        val desc = if (it.desc.isNullOrBlank()) it.headline else it.desc

        Alert(
            locationId = locationId,
            event = it.event,
            severity = getSeverity(it.severity),
            effective = it.effective?.iso8601TimestampToMilliseconds(),
            expires = it.expires?.iso8601TimestampToMilliseconds(),
            description = desc ?: "",
            source = "WeatherApi.com",
            lastUpdatedInMilli = System.currentTimeMillis()
        )
    }
}

private fun getSeverity(severity: String?): AlertSeverity {
    return when (severity) {
        "Extreme" -> AlertSeverity.CRITICAL
        "Severe" -> AlertSeverity.HIGH
        "Moderate" -> AlertSeverity.MODERATE
        "Minor" -> AlertSeverity.LOW
        else -> AlertSeverity.UNKNOWN
    }
}