package com.pranshulgg.weather_master_app.data.local.mapper.alerts

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.data.local.entity.alerts.AlertEntity


fun Alert.toEntity(locationId: String): AlertEntity {
    return AlertEntity(
        locationId = locationId,
        event = event,
        severity = severity,
        effective = effective,
        expires = expires,
        description = description,
        source = source,
        lastUpdatedInMilli = lastUpdatedInMilli
    )
}

fun AlertEntity.toDomain(): Alert {
    return Alert(
        locationId = locationId,
        event = event,
        severity = severity,
        effective = effective,
        expires = expires,
        description = description,
        source = source,
        lastUpdatedInMilli = lastUpdatedInMilli
    )
}