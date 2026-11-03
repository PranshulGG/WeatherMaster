package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.mgm.alerts

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmAlertJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.weather.alerts.deduplicateAlerts

private fun mapMgmWeatherListToEvent(weatherList: List<String?>?): String {
    if (weatherList.isNullOrEmpty()) return ""
    return weatherList.filterNotNull().map { item ->
        val key = item.lowercase().trim()
        when {
            key.contains("thunderstorm") -> "Gök Gürültülü Sağanak Yağış"
            key == "rain" || key.contains("heavy rain") -> "Kuvvetli Yağış"
            key == "wind" || key.contains("gale") || key.contains("storm") -> "Kuvvetli Rüzgar ve Fırtına"
            key == "snow" || key.contains("heavy snow") -> "Yoğun Kar Yağışı"
            key.contains("ice") || key.contains("freezing") || key.contains("snow-ice") -> "Buzlanma ve Don"
            key.contains("fog") -> "Yoğun Sis"
            key.contains("heat") || key.contains("high-temperature") -> "Aşırı Sıcaklık"
            key.contains("cold") || key.contains("low-temperature") || key.contains("frost") -> "Aşırı Soğuk"
            key.contains("coastal") || key.contains("wave") -> "Kıyı Riski ve Dalga"
            key.contains("fire") -> "Orman Yangını Riski"
            key.contains("avalanche") -> "Çığ Riski"
            key.contains("flood") -> "Sel ve Su Baskını"
            else -> item
        }
    }.distinct().joinToString(", ")
}

fun List<MgmAlertJson>.toDomain(locationId: String, currentStationId: Long): List<Alert> {
    val list = mutableListOf<Alert>()

    this.forEach { alert ->
        val effective = alert.begin?.iso8601TimestampToMilliseconds()
        val expires = alert.end?.iso8601TimestampToMilliseconds()

        when {
            alert.towns?.red?.contains(currentStationId) == true -> {
                list.add(
                    Alert(
                        locationId = locationId,
                        event = mapMgmWeatherListToEvent(alert.weather?.red),
                        severity = AlertSeverity.CRITICAL,
                        effective = effective,
                        expires = expires,
                        description = alert.text?.red?.trim() ?: "",
                        source = "Meteoroloji Genel Müdürlüğü",
                        lastUpdatedInMilli = System.currentTimeMillis(),
                    )
                )
            }

            alert.towns?.orange?.contains(currentStationId) == true -> {
                list.add(
                    Alert(
                        locationId = locationId,
                        event = mapMgmWeatherListToEvent(alert.weather?.orange),
                        severity = AlertSeverity.HIGH,
                        effective = effective,
                        expires = expires,
                        description = alert.text?.orange?.trim() ?: "",
                        source = "Meteoroloji Genel Müdürlüğü",
                        lastUpdatedInMilli = System.currentTimeMillis(),
                    )
                )
            }

            alert.towns?.yellow?.contains(currentStationId) == true -> {
                list.add(
                    Alert(
                        locationId = locationId,
                        event = mapMgmWeatherListToEvent(alert.weather?.yellow),
                        severity = AlertSeverity.MODERATE,
                        effective = effective,
                        expires = expires,
                        description = alert.text?.yellow?.trim() ?: "",
                        source = "Meteoroloji Genel Müdürlüğü",
                        lastUpdatedInMilli = System.currentTimeMillis(),
                    )
                )
            }
        }
    }

    return list.deduplicateAlerts()
}
