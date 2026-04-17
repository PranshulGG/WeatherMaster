package com.pranshulgg.weathermaster.data.local.mapper

import com.pranshulgg.weathermaster.core.model.SearchItem
import com.pranshulgg.weathermaster.core.network.search.OpenMeteoSearchDto
import com.pranshulgg.weathermaster.core.utils.UuidGenerator

fun OpenMeteoSearchDto.toDomain(): List<SearchItem> =
    List(results.size) {
        SearchItem(
            id = UuidGenerator().generateId(),
            name = results[it].name,
            latitude = results[it].latitude,
            longitude = results[it].longitude,
            country = results[it].country,
            timezone = results[it].timezone,
            countryCode = results[it].countryCode,
            state = results[it].state ?: results[it].state2 ?: ""
        )
    }