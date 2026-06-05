package com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

/**
 * Extracted from
 * https://github.com/breezy-weather/breezy-weather/blob/11043cdefe8f350ba8887ec4fcc012a51f6fd472/app/src/src_nonfreenet/org/breezyweather/sources/mf/MfService.kt#L879
 */
object MeteoFranceConditionMap {
    fun getCondition(icon: String?): WeatherCondition {
        return if (icon == null) {
            WeatherCondition.NO_CONDITION_FOUND
        } else {
            with(icon) {
                when {
                    startsWith("p32") ||
                            startsWith("p33") ||
                            startsWith("p34") -> WeatherCondition.CLEAR_SKY // TODO: SHOULD BE WIND

                    startsWith("p31") -> WeatherCondition.NO_CONDITION_FOUND // What is this? (I have no idea either)
                    startsWith("p26") ||
                            startsWith("p27") ||
                            startsWith("p28") ||
                            startsWith("p29") -> WeatherCondition.THUNDERSTORM

                    startsWith("p21") ||
                            startsWith("p22") ||
                            startsWith("p23") -> WeatherCondition.SNOW

                    startsWith("p19") ||
                            startsWith("p20") -> WeatherCondition.SNOW  // TODO: SHOULD BE HAIL

                    startsWith("p17") ||
                            startsWith("p18") -> WeatherCondition.SNOW // TODO: SHOULD BE SLEET

                    startsWith("p16") ||
                            startsWith("p24") ||
                            startsWith("p25") ||
                            startsWith("p30") -> WeatherCondition.THUNDERSTORM

                    startsWith("p9") ||
                            startsWith("p10") ||
                            startsWith("p11") ||
                            startsWith("p12") ||
                            startsWith("p13") ||
                            startsWith("p14") ||
                            startsWith("p15") -> WeatherCondition.RAIN

                    startsWith("p6") ||
                            startsWith("p7") ||
                            startsWith("p8") -> WeatherCondition.FOG_HAZE

                    startsWith("p4") ||
                            startsWith("p5") -> WeatherCondition.FOG_HAZE

                    startsWith("p3") -> WeatherCondition.OVERCAST
                    startsWith("p2") -> WeatherCondition.PARTLY_CLOUDY
                    startsWith("p1") -> WeatherCondition.CLEAR_SKY
                    else -> WeatherCondition.NO_CONDITION_FOUND
                }
            }
        }
    }
}