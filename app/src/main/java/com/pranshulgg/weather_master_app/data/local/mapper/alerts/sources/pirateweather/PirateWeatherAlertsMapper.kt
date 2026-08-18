package com.pranshulgg.weather_master_app.data.local.mapper.alerts.sources.pirateweather

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.json.PirateWeatherAlertJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds

fun List<PirateWeatherAlertJson>.toDomain(locationId: String): List<Alert> {
    return map {
        Alert(
            locationId = locationId,
            event = it.title,
            severity = getSeverity(it.severity),
            effective = it.time.secondsToMilliseconds(),
            expires = it.expires.secondsToMilliseconds(),
            description = it.description,
            source = "Pirate Weather",
            lastUpdatedInMilli = System.currentTimeMillis()
        )
    }
}

private fun getSeverity(severity: String?): AlertSeverity {
    return when (severity?.lowercase()) {
        "extreme" -> AlertSeverity.CRITICAL
        "severe" -> AlertSeverity.HIGH
        "moderate" -> AlertSeverity.MODERATE
        "minor", "advisory" -> AlertSeverity.LOW
        else -> AlertSeverity.UNKNOWN
    }
}
