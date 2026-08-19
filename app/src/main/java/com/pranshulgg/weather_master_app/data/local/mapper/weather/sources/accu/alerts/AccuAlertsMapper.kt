package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.accu.alerts

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.alerts.json.AlertsAccuJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds


fun AlertsAccuJson.toDomain(locationId: String): Alert {
    return Alert(
        locationId = locationId,
        event = event.localized.ifBlank { event.english },
        severity = getSeverity(alarmLevel),
        effective = area[0].epochStartTimeSeconds.secondsToMilliseconds(),
        expires = area[0].epochEndTimeSeconds.secondsToMilliseconds(),
        description = area[0].text,
        source = source,
        lastUpdatedInMilli = System.currentTimeMillis()
    )
}

private fun getSeverity(severity: String?): AlertSeverity {
    return when (severity) {
        "Red" -> AlertSeverity.CRITICAL
        "Orange" -> AlertSeverity.HIGH
        "Yellow" -> AlertSeverity.MODERATE
        else -> AlertSeverity.UNKNOWN
    }
}