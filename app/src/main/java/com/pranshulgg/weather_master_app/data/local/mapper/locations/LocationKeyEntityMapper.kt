package com.pranshulgg.weather_master_app.data.local.mapper.locations

import com.pranshulgg.weather_master_app.core.model.domain.location.LocationKey
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity

// ---------------------------- ENTITY TO DOMAIN ----------------------------

fun LocationKeyEntity.toDomain(): LocationKey {
    return LocationKey(
        locationId = locationId,
        cityKey = cityKey
    )
}


// ---------------------------- DOMAIN TO ENTITY ----------------------------

fun LocationKey.toEntity(): LocationKeyEntity {
    return LocationKeyEntity(
        locationId = locationId,
        cityKey = cityKey
    )
}