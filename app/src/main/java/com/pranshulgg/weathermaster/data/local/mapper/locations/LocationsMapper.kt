package com.pranshulgg.weathermaster.data.local.mapper.locations

import com.pranshulgg.weathermaster.core.model.domain.location.Location
import com.pranshulgg.weathermaster.core.network.sources.search.geonames.GeoNamesSearchDto
import com.pranshulgg.weathermaster.core.network.sources.search.openmeteo.OpenMeteoSearchDto
import com.pranshulgg.weathermaster.core.utils.ids.UuidGenerator
import com.pranshulgg.weathermaster.data.local.entity.location.WeatherLocationEntity

fun OpenMeteoSearchDto.toDomain(): List<Location> =
    List(results.size) {
        Location(
            id = UuidGenerator.generateId(),
            name = results[it].name,
            latitude = results[it].latitude,
            longitude = results[it].longitude,
            country = results[it].country,
            timezone = results[it].timezone,
            countryCode = results[it].countryCode,
            state = results[it].state ?: results[it].state2 ?: "",
            isDefault = false
        )
    }


fun GeoNamesSearchDto.toDomain(): List<Location> =
    List(geonames.size) {
        Location(
            id = UuidGenerator.generateId(),
            name = geonames[it].name,
            latitude = geonames[it].latitude,
            longitude = geonames[it].longitude,
            country = geonames[it].countryName,
            timezone = "",
            countryCode = geonames[it].countryCode,
            state = geonames[it].state ?: "",
            isDefault = false
        )
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
