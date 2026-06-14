package com.pranshulgg.weather_master_app.core.network.sources.weather.eccc

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

object EcccConditionMap {
    fun getCondition(icon: String?): WeatherCondition {
        return when (icon) {
            "00", "01", "30", "31" -> WeatherCondition.CLEAR_SKY
            "02", "03", "04", "05", "22", "32", "33", "34", "35" -> WeatherCondition.PARTLY_CLOUDY
            "10", "20", "21" -> WeatherCondition.OVERCAST
            "06", "11", "12", "13", "28", "36" -> WeatherCondition.RAIN
            "14", "27" -> WeatherCondition.SNOW // SHOULD BE HAIL
            "07", "15", "37" -> WeatherCondition.SNOW // SHOULD BE SLEET
            "08", "16", "17", "18", "26", "38" -> WeatherCondition.SNOW
            "09", "19", "39", "46", "47" -> WeatherCondition.THUNDERSTORM
            "23", "44", "45", "24" -> WeatherCondition.FOG_HAZE
            "25", "40", "41", "42", "43", "48" -> WeatherCondition.CLEAR_SKY // SHOULD BE WIND
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }

}
