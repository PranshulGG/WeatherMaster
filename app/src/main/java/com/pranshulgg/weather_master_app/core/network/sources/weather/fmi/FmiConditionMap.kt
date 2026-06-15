package com.pranshulgg.weather_master_app.core.network.sources.weather.fmi

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

object FmiConditionMap {

    // FIXME: Wrong mapping because of missing conditions
    fun getCondition(code: Int?): WeatherCondition {
        return when (code) {
            1 -> WeatherCondition.CLEAR_SKY
            2 -> WeatherCondition.PARTLY_CLOUDY
            3 -> WeatherCondition.OVERCAST
            21, 22, 23, 31, 32, 33 -> WeatherCondition.RAIN
            41, 42, 43, 51, 52, 53 -> WeatherCondition.SNOW
            61, 62, 63, 64 -> WeatherCondition.THUNDERSTORM
            71, 72, 73, 81, 82, 83 -> WeatherCondition.SNOW
            91, 92 -> WeatherCondition.FOG_HAZE
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }

    fun getCurrentCondition(code: Int?): WeatherCondition {
        return when (code) {
            0 -> WeatherCondition.NO_CONDITION_FOUND
            4, 5, 10, 20, 30, 31, 32, 33, 34 -> WeatherCondition.FOG_HAZE
            21, 22, 23, 40, 41, 42, 50, 51, 52, 53, 60, 61, 62, 63, 80, 81, 82, 83, 84 -> WeatherCondition.RAIN
            24, 70, 71, 72, 73, 74, 75, 76, 77, 85, 86, 87 -> WeatherCondition.SNOW
            25, 54, 55, 56, 64, 65, 66, 67, 68 -> WeatherCondition.SNOW
            78 -> WeatherCondition.SNOW
            89 -> WeatherCondition.SNOW
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }

}

