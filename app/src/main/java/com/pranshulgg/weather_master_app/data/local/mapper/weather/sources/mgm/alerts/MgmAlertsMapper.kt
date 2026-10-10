package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.mgm.alerts

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmAlertJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds


fun List<MgmAlertJson>.toDomain(locationId: String, currentStationId: Long): List<Alert> {


    val list = mutableListOf<Alert>()

    this.forEach { alert ->
        when {
            alert.towns?.yellow?.contains(currentStationId)!! -> {
                list.add(
                    Alert(
                        locationId = locationId,
                        event = alert.weather?.yellow?.mapNotNull { it }?.joinToString(", ") ?: "",
                        severity = AlertSeverity.MODERATE,
                        effective = alert.begin?.iso8601TimestampToMilliseconds(),
                        expires = alert.end?.iso8601TimestampToMilliseconds(),
                        description = alert.text?.yellow ?: "",
                        source = "Meteoroloji Genel Müdürlüğü",
                        lastUpdatedInMilli = System.currentTimeMillis(),
                    )
                )
            }

            alert.towns.orange.contains(currentStationId) -> {
                list.add(
                    Alert(
                        locationId = locationId,
                        event = alert.weather?.orange?.mapNotNull { it }?.joinToString(", ") ?: "",
                        severity = AlertSeverity.HIGH,
                        effective = alert.begin?.iso8601TimestampToMilliseconds(),
                        expires = alert.end?.iso8601TimestampToMilliseconds(),
                        description = alert.text?.orange ?: "",
                        source = "Meteoroloji Genel Müdürlüğü",
                        lastUpdatedInMilli = System.currentTimeMillis(),
                    )
                )
            }

            alert.towns.red.contains(currentStationId) -> {
                list.add(
                    Alert(
                        locationId = locationId,
                        event = alert.weather?.red?.mapNotNull { it }?.joinToString(", ") ?: "",
                        severity = AlertSeverity.CRITICAL,
                        effective = alert.begin?.iso8601TimestampToMilliseconds(),
                        expires = alert.end?.iso8601TimestampToMilliseconds(),
                        description = alert.text?.red ?: "",
                        source = "Meteoroloji Genel Müdürlüğü",
                        lastUpdatedInMilli = System.currentTimeMillis(),
                    )
                )
            }
        }
    }


    return list
}

