package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.kmi.alerts

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.core.network.sources.weather.kmi.json.KmiWeatherJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds


fun KmiWeatherJson.toAlertsDomain(locationId: String): List<Alert> {

    val alerts = forecast.warning

    return alerts.mapIndexed { index, alert ->
        Alert(
            locationId = locationId,
            event = alert.warningType.en ?: "",
            severity = getSeverity(alert.warningLevel),
            effective = alert.fromTimestamp?.iso8601TimestampToMilliseconds(),
            expires = alert.toTimestamp?.iso8601TimestampToMilliseconds(),
            description = alert.text.en ?: "",
            source = "Royal Meteorological Institute of Belgium",
            lastUpdatedInMilli = System.currentTimeMillis()
        )
    }

}


// Not tested if the severity is correct or not
private fun getSeverity(waringLevel: Double?): AlertSeverity {
    return when (waringLevel) {
        3.0 -> AlertSeverity.CRITICAL
        2.0 -> AlertSeverity.HIGH
        1.0 -> AlertSeverity.MODERATE
        0.0 -> AlertSeverity.LOW
        else -> AlertSeverity.UNKNOWN
    }
}