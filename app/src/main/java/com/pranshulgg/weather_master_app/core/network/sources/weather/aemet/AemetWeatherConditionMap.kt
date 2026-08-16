package com.pranshulgg.weather_master_app.core.network.sources.weather.aemet

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

object AemetWeatherConditionMap {

    // AEMET's estadoCielo codes suffix night variants with "n" (e.g. "11n") - day/night art
    // is already handled elsewhere via sunrise/sunset, so both variants map the same way here.
    fun getCondition(code: String?): WeatherCondition {
        val normalized = code?.trim()?.removeSuffix("n")

        if (normalized.isNullOrBlank()) return WeatherCondition.NO_CONDITION_FOUND

        return when (normalized) {
            "11" -> WeatherCondition.CLEAR_SKY
            "12", "13" -> WeatherCondition.PARTLY_CLOUDY
            "14", "15", "16", "17" -> WeatherCondition.OVERCAST
            "23", "24", "25", "26" -> WeatherCondition.RAIN
            "43", "44", "45", "46" -> WeatherCondition.LIGHT_RAIN
            "27" -> WeatherCondition.HEAVY_RAIN
            "33", "34", "35", "36" -> WeatherCondition.SNOW
            "71", "72", "73", "74" -> WeatherCondition.LIGHT_SNOW
            "51", "52", "53", "54", "61", "62", "63", "64" -> WeatherCondition.THUNDERSTORM
            "81", "82" -> WeatherCondition.FOG_HAZE
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}
