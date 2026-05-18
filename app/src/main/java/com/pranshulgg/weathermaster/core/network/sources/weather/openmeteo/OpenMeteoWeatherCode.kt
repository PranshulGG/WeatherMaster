package com.pranshulgg.weathermaster.core.network.sources.weather.openmeteo

import com.pranshulgg.weathermaster.core.model.weather.WeatherCondition

fun openMeteoWeatherCode(code: Int): WeatherCondition {
    return when (code) {
        0 -> WeatherCondition.CLEAR_SKY
        1 -> WeatherCondition.MOSTLY_CLEAR
        2 -> WeatherCondition.PARTLY_CLOUDY
        3 -> WeatherCondition.OVERCAST
        45, 48 -> WeatherCondition.FOG_HAZE
        51, 53, 55, 56, 57 -> WeatherCondition.LIGHT_RAIN
        61, 63, 65, 66, 67 -> WeatherCondition.RAIN
        71, 73, 75, 77 -> WeatherCondition.LIGHT_SNOW
        80, 81, 82 -> WeatherCondition.HEAVY_RAIN
        85, 86 -> WeatherCondition.HEAVY_SNOW
        95, 96, 99 -> WeatherCondition.THUNDERSTORM
        else -> WeatherCondition.CLEAR_SKY
    }
}