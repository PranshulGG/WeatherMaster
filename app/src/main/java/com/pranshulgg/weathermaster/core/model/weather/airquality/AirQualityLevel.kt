package com.pranshulgg.weathermaster.core.model.weather.airquality

import android.content.Context
import com.pranshulgg.weathermaster.R

enum class AirQualityLevel {
    GOOD,
    FAIR,
    MODERATE,
    POOR,
    VERY_POOR,
    HAZARDOUS
}

fun AirQualityLevel.toName(context: Context): String {
    return when (this) {
        AirQualityLevel.GOOD -> context.getString(R.string.weather_airquality_good)
        AirQualityLevel.FAIR -> context.getString(R.string.weather_airquality_fair)
        AirQualityLevel.MODERATE -> context.getString(R.string.weather_airquality_moderate)
        AirQualityLevel.POOR -> context.getString(R.string.weather_airquality_poor)
        AirQualityLevel.VERY_POOR -> context.getString(R.string.weather_airquality_very_poor)
        AirQualityLevel.HAZARDOUS -> context.getString(R.string.weather_airquality_hazardous)
    }
}

