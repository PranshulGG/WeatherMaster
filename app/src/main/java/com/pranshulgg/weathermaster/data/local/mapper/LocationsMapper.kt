package com.pranshulgg.weathermaster.data.local.mapper

import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.WeatherProviders
import com.pranshulgg.weathermaster.core.network.search.OpenMeteoSearchDto
import com.pranshulgg.weathermaster.core.utils.UuidGenerator
import com.pranshulgg.weathermaster.data.local.entity.WeatherLocationEntity

fun OpenMeteoSearchDto.toDomain(): List<Location> =
    List(results.size) {
        Location(
            id = UuidGenerator().generateId(),
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


fun Location.toEntity(): WeatherLocationEntity =
    WeatherLocationEntity(
        id = id,
        name = name,
        country = country,
        lat = latitude,
        lon = longitude,
        timezone = timezone,
        provider = provider,
        state = state,
        countryCode = countryCode,
        isPinned = false,
        isFavorite = false,
        isDefault = isDefault
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
        countryCode = country,
        state = state ?: "",
        provider = provider,
        isFavorite = isFavorite,
        isPinned = isPinned,
        isDefault = isDefault
    )
