package com.pranshulgg.weather_master_app.core.network.sources.weather.kmi

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition


// Source - https://github.com/jdejaegh/irm-kmi-api/blob/a66c94a649885be3afd3081cb8cf595899b46ede/irm_kmi_api/const.py#L60
object KmiWeatherConditionMap {
    fun getCondition(id: Int?): WeatherCondition {
        return when (id) {
            0, 1 -> WeatherCondition.CLEAR_SKY
            2, 5, 7, 10, 13, 17, 18, 21 -> WeatherCondition.RAIN
            3 -> WeatherCondition.PARTLY_CLOUDY
            4, 6, 16, 19 -> WeatherCondition.LIGHT_RAIN
            8, 9, 20 -> WeatherCondition.MIXED_PRECIPITATION
            11, 12, 22, 23 -> WeatherCondition.SNOW
            14, 15 -> WeatherCondition.OVERCAST
            24, 25, 26, 27 -> WeatherCondition.FOG_HAZE
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}