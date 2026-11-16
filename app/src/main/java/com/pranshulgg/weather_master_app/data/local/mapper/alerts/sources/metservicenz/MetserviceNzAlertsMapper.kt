package com.pranshulgg.weather_master_app.data.local.mapper.alerts.sources.metservicenz

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.core.network.sources.alerts.metservicenz.model.MetserviceNzCapAlert
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds


fun metserviceNzAlertsMapper(
    alerts: List<MetserviceNzCapAlert>,
    locationId: String
): List<Alert> {

    return alerts.map { capAlert ->

        Alert(
            locationId = locationId,
            event = capAlert.event ?: capAlert.headline ?: "",
            severity = getSeverity(capAlert.severity),
            effective = capAlert.onset?.iso8601TimestampToMilliseconds(),
            expires = capAlert.expires?.iso8601TimestampToMilliseconds(),
            description = capAlert.description ?: "",
            lastUpdatedInMilli = System.currentTimeMillis(),
            source = capAlert.senderName ?: "MetService"
        )
    }
}

private fun getSeverity(value: String?): AlertSeverity {
    return when (value) {
        "Extreme" -> AlertSeverity.CRITICAL
        "Severe" -> AlertSeverity.HIGH
        "Moderate" -> AlertSeverity.MODERATE
        "Minor" -> AlertSeverity.LOW
        else -> AlertSeverity.UNKNOWN
    }
}
