package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.nws.alerts

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsAlertsJson
import java.time.OffsetDateTime


/**
 * Initial NWS alerts integration implemented by https://github.com/reveler-hub
 */

fun NwsAlertsJson.toDomain(locationId: String): List<Alert> {
    return features.orEmpty().mapNotNull { feature ->
        val properties = feature.properties ?: return@mapNotNull null
        val event = properties.event ?: return@mapNotNull null

        Alert(
            locationId = locationId,
            event = event,
            severity = nwsAlertSeverity(properties.severity),
            effective = properties.effective?.toEpochMillisOrNull(),
            expires = properties.expires?.toEpochMillisOrNull(),
            description = properties.description.orEmpty(),
            source = properties.senderName,
            lastUpdatedInMilli = System.currentTimeMillis()
        )
    }
}

private fun nwsAlertSeverity(severity: String?): AlertSeverity = when (severity) {
    "Extreme" -> AlertSeverity.CRITICAL
    "Severe" -> AlertSeverity.HIGH
    "Moderate" -> AlertSeverity.MODERATE
    "Minor" -> AlertSeverity.LOW
    else -> AlertSeverity.UNKNOWN
}

private fun String.toEpochMillisOrNull(): Long? {
    return try {
        OffsetDateTime.parse(this).toInstant().toEpochMilli()
    } catch (e: Exception) {
        null
    }
}
