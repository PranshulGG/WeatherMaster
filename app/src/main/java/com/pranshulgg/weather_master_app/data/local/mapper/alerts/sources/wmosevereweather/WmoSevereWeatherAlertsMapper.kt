package com.pranshulgg.weather_master_app.data.local.mapper.alerts.sources.wmosevereweather

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.json.WmoSevereWeatherFeatureJson
import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.model.WmoCapAlert
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds


fun wmoSevereWeatherAlertsMapper(
    alerts: List<Pair<WmoSevereWeatherFeatureJson, WmoCapAlert?>>,
    locationId: String
): List<Alert> {


    return alerts.filter {
        !it.first.properties?.event.isNullOrBlank()
                || !it.first.properties?.headline.isNullOrBlank()
                || !it.second?.event.isNullOrBlank()
                || !it.second?.headline.isNullOrBlank()
    }.map { (feature, capAlert) ->


        Alert(
            locationId = locationId,
            event = capAlert?.event ?: capAlert?.headline ?: feature.properties?.event
            ?: feature.properties?.headline ?: "",
            severity = getSeverity(feature.properties?.severity),
            effective = capAlert?.effective?.iso8601TimestampToMilliseconds()
                ?: feature.properties?.effective?.iso8601TimestampToMilliseconds(),
            expires = capAlert?.expires?.iso8601TimestampToMilliseconds()
                ?: feature.properties?.expires?.iso8601TimestampToMilliseconds(),
            description = capAlert?.description ?: feature.properties?.description ?: "",
            lastUpdatedInMilli = System.currentTimeMillis(),
            source = capAlert?.senderName ?: "WMO Severe Weather"
        )
    }
}

private fun getSeverity(code: Int?): AlertSeverity {
    return when (code) {
        4 -> AlertSeverity.CRITICAL
        3 -> AlertSeverity.HIGH
        2 -> AlertSeverity.MODERATE
        1 -> AlertSeverity.LOW
        else -> AlertSeverity.UNKNOWN
    }
}