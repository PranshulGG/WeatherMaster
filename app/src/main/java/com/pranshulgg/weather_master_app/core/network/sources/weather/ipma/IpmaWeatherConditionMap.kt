package com.pranshulgg.weather_master_app.core.network.sources.weather.ipma

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

// TODO - remap
// Source - https://api.ipma.pt/open-data/weather-type-classe.json

object IpmaWeatherConditionMap {
    fun getCondition(id: Int?): WeatherCondition {
        return when (id) {
            1 -> WeatherCondition.CLEAR_SKY
            2, 25 -> WeatherCondition.PARTLY_CLOUDY
            3 -> WeatherCondition.MOSTLY_CLEAR
            4, 5, 24, 27 -> WeatherCondition.OVERCAST
            6, 12, 9 -> WeatherCondition.RAIN
            7, 10, 13, 15, 16 -> WeatherCondition.LIGHT_RAIN
            8, 11, 14, 22, 21 -> WeatherCondition.HEAVY_RAIN
            17, 26 -> WeatherCondition.FOG_HAZE
            18, 28, 29, 30 -> WeatherCondition.SNOW
            19, 20, 23 -> WeatherCondition.THUNDERSTORM
            else -> WeatherCondition.NO_CONDITION_FOUND

        }
    }
}