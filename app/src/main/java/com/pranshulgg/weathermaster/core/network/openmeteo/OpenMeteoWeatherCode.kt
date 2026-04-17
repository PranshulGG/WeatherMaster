package com.pranshulgg.weathermaster.core.network.openmeteo

import com.pranshulgg.weathermaster.core.model.WeatherConditions

fun openMeteoWeatherCode(code: Int): WeatherConditions {
    return when (code) {
        0 -> WeatherConditions.CLEAR_SKY
        1 -> WeatherConditions.MOSTLY_CLEAR
        2 -> WeatherConditions.PARTLY_CLOUDY
        3 -> WeatherConditions.OVERCAST
        45, 48 -> WeatherConditions.FOG_HAZE
        51, 53, 55, 56, 57 -> WeatherConditions.LIGHT_RAIN
        61, 63, 65, 66, 67 -> WeatherConditions.RAIN
        71, 73, 75, 77 -> WeatherConditions.LIGHT_SNOW
        80, 81, 82 -> WeatherConditions.HEAVY_RAIN
        85, 86 -> WeatherConditions.HEAVY_SNOW
        95, 96, 99 -> WeatherConditions.THUNDERSTORM
        else -> WeatherConditions.CLEAR_SKY
    }
}