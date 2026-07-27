package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.accu

import com.pranshulgg.weather_master_app.core.model.weather.airquality.accu.AccuCityKey
import com.pranshulgg.weather_master_app.data.local.entity.airquality.accu.AccuEntity

// ---------------------------- ENTITY TO DOMAIN ----------------------------

fun AccuEntity.toDomain(): AccuCityKey {
    return AccuCityKey(
        locationId = locationId,
        cityKey = cityKey
    )
}


// ---------------------------- DOMAIN TO ENTITY ----------------------------

fun AccuCityKey.toEntity(): AccuEntity {
    return AccuEntity(
        locationId = locationId,
        cityKey = cityKey
    )
}