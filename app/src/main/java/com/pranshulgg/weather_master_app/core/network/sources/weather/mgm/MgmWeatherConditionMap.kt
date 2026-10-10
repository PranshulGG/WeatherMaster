package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

// SOURCE: https://www.mgm.gov.tr/Scripts/ziko16_js/angularService/ililceler.js?v=4
object MgmWeatherConditionMap {
    fun getCondition(code: String?): WeatherCondition {
        return when (code) {
            "A" -> WeatherCondition.CLEAR_SKY
            "AB" -> WeatherCondition.MOSTLY_CLEAR
            "PB", "CB" -> WeatherCondition.PARTLY_CLOUDY
            "HY", "HSY" -> WeatherCondition.LIGHT_RAIN
            "Y", "SY", "MSY" -> WeatherCondition.RAIN
            "KY", "KSY" -> WeatherCondition.HEAVY_RAIN
            "KKY" -> WeatherCondition.MIXED_PRECIPITATION
            "HKY" -> WeatherCondition.LIGHT_SNOW
            "K" -> WeatherCondition.SNOW
            "KYK" -> WeatherCondition.HEAVY_SNOW
            "DY" -> WeatherCondition.HAIL
            "GSY", "KGY", "KF" -> WeatherCondition.THUNDERSTORM // KF = Dust or Sandstorm
            "SIS", "PUS", "DNM" -> WeatherCondition.FOG_HAZE
            "R", "GKR", "KKR" -> WeatherCondition.CLEAR_SKY // SHOULD BE WINDY
            "SCK" -> WeatherCondition.VERY_HOT
            "SGK" -> WeatherCondition.VERY_COLD
            "HHY" -> WeatherCondition.RAIN
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}