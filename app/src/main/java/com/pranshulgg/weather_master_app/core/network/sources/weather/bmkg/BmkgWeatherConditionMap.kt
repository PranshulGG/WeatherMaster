package com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition


object BmkgWeatherConditionMap {
    fun getCondition(id: Int?): WeatherCondition {
        return when (id) {
            0, 1 -> WeatherCondition.CLEAR_SKY
            2 -> WeatherCondition.PARTLY_CLOUDY
            3, 4 -> WeatherCondition.OVERCAST
            10, 45 -> WeatherCondition.FOG_HAZE
            17 -> WeatherCondition.THUNDERSTORM
            60, 61 -> WeatherCondition.LIGHT_RAIN
            63 -> WeatherCondition.RAIN
            65 -> WeatherCondition.HEAVY_RAIN
            95, 96, 99 -> WeatherCondition.THUNDERSTORM
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}