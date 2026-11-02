package com.pranshulgg.weather_master_app.core.utils.weather.alerts

import android.content.Context
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert

fun getLocalizedAlertEvent(event: String?, context: Context): String {
    if (event.isNullOrBlank()) return ""

    val terms = event.split(",").map { it.trim() }
    val mappedTerms = terms.map { term ->
        val key = term.lowercase()
        when {
            key.contains("thunderstorm") -> context.getString(R.string.alert_event_thunderstorm)
            key == "rain" || key.contains("heavy rain") -> context.getString(R.string.alert_event_rain)
            key == "wind" || key.contains("gale") || key.contains("storm") -> context.getString(R.string.alert_event_wind)
            key == "snow" || key.contains("heavy snow") -> context.getString(R.string.alert_event_snow)
            key.contains("ice") || key.contains("freezing") || key.contains("snow-ice") -> context.getString(R.string.alert_event_snow_ice)
            key.contains("fog") -> context.getString(R.string.alert_event_fog)
            key.contains("heat") || key.contains("high-temperature") -> context.getString(R.string.alert_event_heat)
            key.contains("cold") || key.contains("low-temperature") || key.contains("frost") -> context.getString(R.string.alert_event_cold)
            key.contains("coastal") || key.contains("wave") -> context.getString(R.string.alert_event_coastal)
            key.contains("fire") -> context.getString(R.string.alert_event_fire)
            key.contains("avalanche") -> context.getString(R.string.alert_event_avalanche)
            key.contains("flood") -> context.getString(R.string.alert_event_flood)
            else -> term
        }
    }

    return mappedTerms.distinct().joinToString(", ")
}

fun List<Alert>.deduplicateAlerts(): List<Alert> {
    return this.distinctBy { alert ->
        Triple(
            alert.event.trim().lowercase(),
            alert.severity,
            (alert.effective ?: 0L) to (alert.expires ?: 0L)
        ) to alert.description.trim()
    }
}
