package com.pranshulgg.weather_master_app.core.network.sources.weather.accu

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition


/**
 * Source: https://apidev.accuweather.com/developers/weather-icons
 * TODO: Missing in app conditions
 */
object AccuWeatherConditionMap {
    fun getCondition(id: Int?): WeatherCondition {
        return when (id) {
            1, 33 -> WeatherCondition.CLEAR_SKY
            2, 34 -> WeatherCondition.MOSTLY_CLEAR
            3, 35, 4, 36 -> WeatherCondition.PARTLY_CLOUDY
            5, 37, 11 -> WeatherCondition.FOG_HAZE
            6, 7, 38, 8 -> WeatherCondition.OVERCAST
            12, 18 -> WeatherCondition.RAIN
            13, 14, 39, 40 -> WeatherCondition.LIGHT_RAIN
            15, 16, 17, 41, 42 -> WeatherCondition.THUNDERSTORM
            19, 20, 21, 43, 44, 23 -> WeatherCondition.LIGHT_SNOW
            22, 24, 25, 26, 29 -> WeatherCondition.SNOW
            30 -> WeatherCondition.VERY_HOT
            31 -> WeatherCondition.VERY_COLD
            32 -> WeatherCondition.OVERCAST // SHOULD BE WIND
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}