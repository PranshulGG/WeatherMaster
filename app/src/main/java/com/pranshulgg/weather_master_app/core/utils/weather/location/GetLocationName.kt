package com.pranshulgg.weather_master_app.core.utils.weather.location

import com.pranshulgg.weather_master_app.core.model.domain.location.Location

fun getLocationNameInShort(location: Location?): String {

    if (location == null) {
        return "••••"
    }
    val isCustomNameValid = !location.customName.isNullOrBlank()

    return if (isCustomNameValid) {
        location.customName
    } else {
        location.name
    }
}

fun getFullLocationName(location: Location?): String {

    if (location == null) {
        return "••••"
    }

    val isCustomNameValid = !location.customName.isNullOrBlank()

    val locationText = location.let {
        buildString {
            append(location.name)
            if (location.country.isNotBlank()) {
                append(", ")
            }
            if (location.state.isNotBlank()) {
                append(location.state)
                append(", ")
            }
            append(location.country)
        }
    }

    return if (isCustomNameValid) {
        location.customName
    } else {
        locationText
    }
}