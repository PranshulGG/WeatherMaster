package com.pranshulgg.weather_master_app.core.network.sources.weather.cwa

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

object CwaWeatherConditionMap {

    // Based on CWA's own official code table ("預報產品天氣描述代碼表",
    // https://www.cwa.gov.tw/V8/assets/pdf/Weather_Icon.pdf), codes 1-42, verified live against
    // real API responses for codes 1-32. Values come back as zero-padded strings (e.g. "08").
    fun getCondition(code: String?): WeatherCondition {
        val normalized = code?.trim()?.toIntOrNull() ?: return WeatherCondition.NO_CONDITION_FOUND

        return when (normalized) {
            1 -> WeatherCondition.CLEAR_SKY
            2 -> WeatherCondition.MOSTLY_CLEAR
            3, 4 -> WeatherCondition.PARTLY_CLOUDY
            5, 6, 7 -> WeatherCondition.OVERCAST
            8, 9, 10 -> WeatherCondition.LIGHT_RAIN
            11, 12, 13, 14 -> WeatherCondition.RAIN
            15, 16, 17, 18 -> WeatherCondition.THUNDERSTORM
            19, 20 -> WeatherCondition.LIGHT_RAIN // local/afternoon rain, no thunder in this pair
            21, 22 -> WeatherCondition.THUNDERSTORM
            23 -> WeatherCondition.MIXED_PRECIPITATION // "rain or snow" - uncertain precipitation type
            24, 25, 26, 27, 28 -> WeatherCondition.FOG_HAZE
            29, 30 -> WeatherCondition.LIGHT_RAIN
            31, 32 -> WeatherCondition.LIGHT_RAIN // fog + local/occasional rain
            33, 34 -> WeatherCondition.THUNDERSTORM // local thundershowers (+fog for 33)
            35, 36, 41 -> WeatherCondition.THUNDERSTORM // thunderstorm + fog
            37 -> WeatherCondition.MIXED_PRECIPITATION // rain or snow + fog
            38, 39 -> WeatherCondition.RAIN // rain + fog
            42 -> WeatherCondition.SNOW // snow, ice, or snow flurries
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}
