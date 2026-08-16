package com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.model

import com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.json.CwaDatasetJson

// shortRange: the per-county "3 day" dataset (hourly-ish point-in-time elements + 3h condition/precip blocks).
// weekly: the per-county "1 week" dataset (12h day/night blocks), used for the extended daily forecast.
data class CwaForecastBundle(
    val shortRange: CwaDatasetJson,
    val weekly: CwaDatasetJson
)
