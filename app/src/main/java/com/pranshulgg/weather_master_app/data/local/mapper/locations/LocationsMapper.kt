package com.pranshulgg.weather_master_app.data.local.mapper.locations

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.json.GeoNamesSearchJson
import com.pranshulgg.weather_master_app.core.network.sources.search.openmeteo.json.OpenMeteoSearchJson
import com.pranshulgg.weather_master_app.core.utils.ids.UuidGenerator
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity

fun OpenMeteoSearchJson.toDomain(): List<Location> {

    if (results.isNullOrEmpty()) {
        return emptyList()
    }

    val filtered = results.filter { it.country != null }

    return List(filtered.size) {
        Location(
            id = UuidGenerator.generateId(),
            name = filtered[it].name,
            latitude = filtered[it].latitude,
            longitude = filtered[it].longitude,
            country = filtered[it].country!!,
            timezone = filtered[it].timezone,
            countryCode = filtered[it].countryCode,
            state = filtered[it].state ?: filtered[it].state2 ?: "",
            isDefault = false
        )
    }
}


fun GeoNamesSearchJson.toDomain(): List<Location> {
    if (geonames.isNullOrEmpty()) {
        return emptyList()
    }

    val filtered = geonames.filter { it.countryName != null }

    return List(filtered.size) {
        Location(
            id = UuidGenerator.generateId(),
            name = filtered[it].name,
            latitude = filtered[it].latitude,
            longitude = filtered[it].longitude,
            country = filtered[it].countryName!!,
            timezone = "",
            countryCode = filtered[it].countryCode,
            state = filtered[it].state ?: "",
            isDefault = false
        )
    }
}

fun Location.toEntity(): WeatherLocationEntity =
    WeatherLocationEntity(
        id = id,
        name = name,
        country = country,
        lat = latitude,
        lon = longitude,
        timezone = timezone,
        source = source,
        state = state,
        countryCode = countryCode,
        isPinned = false,
        isFavorite = false,
        isDefault = isDefault,
        isDeviceLocation = isDeviceLocation
    )

fun List<WeatherLocationEntity>.toDomain(): List<Location> =
    map { item ->
        item.toDomain()
    }

fun WeatherLocationEntity.toDomain(): Location =
    Location(
        id = id,
        name = name,
        latitude = lat,
        longitude = lon,
        country = country,
        timezone = timezone,
        countryCode = countryCode,
        state = state ?: "",
        source = source,
        isFavorite = isFavorite,
        isPinned = isPinned,
        isDefault = isDefault,
        isDeviceLocation = isDeviceLocation
    )
