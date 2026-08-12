package com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

object GismeteoWeatherConditionMap {

    fun getCondition(code: String?): WeatherCondition {
        if (code.isNullOrBlank()) {
            return WeatherCondition.NO_CONDITION_FOUND
        }

        val normalized = code.lowercase()
        val parts = normalized.split('.').toSet()

        if ("st" in parts) {
            return WeatherCondition.THUNDERSTORM
        }

        if ("r1s1" in parts || ("r1" in parts && "s1" in parts)) {
            return WeatherCondition.LIGHT_RAIN
        }

        if ("r2s2" in parts || ("r2" in parts && "s2" in parts)) {
            return WeatherCondition.RAIN
        }

        when {
            "r3" in parts -> return WeatherCondition.HEAVY_RAIN
            "r2" in parts -> return WeatherCondition.RAIN
            "r1" in parts -> return WeatherCondition.LIGHT_RAIN
        }

        when {
            "s3" in parts -> return WeatherCondition.HEAVY_SNOW
            "s2" in parts -> return WeatherCondition.SNOW
            "s1" in parts -> return WeatherCondition.LIGHT_SNOW
        }

        when {
            "c4" in parts -> return WeatherCondition.OVERCAST
            "c3" in parts -> return WeatherCondition.OVERCAST
            "c2" in parts -> return WeatherCondition.PARTLY_CLOUDY
            "c1" in parts -> return WeatherCondition.MOSTLY_CLEAR
            normalized == "d" || normalized == "n" ->
                return WeatherCondition.CLEAR_SKY
        }

        return WeatherCondition.NO_CONDITION_FOUND
    }
}