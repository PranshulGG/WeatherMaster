package com.pranshulgg.weather_master_app.core.network.sources.weather.jma

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

object JmaConditionMap {

    // The hourly (VPFD) endpoint's `weather` field only ever contains a single bare word per
    // block, never the compound "A時々B"/"AのちB" phrases JMA's text bulletin uses elsewhere -
    // confirmed live by sampling all 142 class10 regions in one pass. 晴れ/くもり/雨 were the
    // only values actually observed (August, no snow season); 雪/雷/雨または雪 aren't covered by
    // a direct match yet since they weren't seen live, so they fall through to the substring
    // fallback below.
    fun getCondition(weatherText: String?): WeatherCondition {
        val text = weatherText?.trim()
        if (text.isNullOrEmpty()) return WeatherCondition.NO_CONDITION_FOUND

        return when (text) {
            "晴れ" -> WeatherCondition.CLEAR_SKY
            "くもり" -> WeatherCondition.OVERCAST
            "雨" -> WeatherCondition.RAIN
            "雪" -> WeatherCondition.SNOW
            "雨または雪" -> WeatherCondition.SLEET
            else -> when {
                text.contains("雷") -> WeatherCondition.THUNDERSTORM
                text.contains("雪") -> WeatherCondition.SNOW
                text.contains("雨") -> WeatherCondition.RAIN
                text.contains("曇") || text.contains("くもり") -> WeatherCondition.OVERCAST
                text.contains("晴") -> WeatherCondition.CLEAR_SKY
                else -> WeatherCondition.NO_CONDITION_FOUND
            }
        }
    }

    // The weekly forecast block uses a completely different, official numeric code system
    // (3-digit "weatherCodes", e.g. "201"/"202") - not the plain-word field above. Table
    // transcribed from the open-source Breezy Weather project's own JMA implementation
    // (JMA_DAILY_WEATHER_CODES, GPLv3, github.com/breezy-weather/breezy-weather), which already
    // did the legwork of mapping every code to its day/night meaning. Where day and night
    // conditions differ and both have a THEN-composite in this app's WeatherCondition enum
    // (CLEAR/CLOUDY/RAIN/SNOW), that composite is used; otherwise falls back to the day
    // condition alone (this enum has no composite for PARTLY_CLOUDY/SLEET/THUNDERSTORM/FOG).
    fun getDailyCondition(code: String?): WeatherCondition {
        val normalized = code?.trim()
        return when (normalized) {
            "100" -> WeatherCondition.CLEAR_SKY
            "101" -> WeatherCondition.PARTLY_CLOUDY
            "102" -> WeatherCondition.RAIN
            "103" -> WeatherCondition.RAIN
            "104" -> WeatherCondition.SNOW
            "105" -> WeatherCondition.SNOW
            "106" -> WeatherCondition.SLEET
            "107" -> WeatherCondition.SLEET
            "108" -> WeatherCondition.THUNDERSTORM
            "110" -> WeatherCondition.CLEAR_SKY
            "111" -> WeatherCondition.CLEAR_THEN_CLOUDY
            "112" -> WeatherCondition.CLEAR_THEN_RAIN
            "113" -> WeatherCondition.CLEAR_THEN_RAIN
            "114" -> WeatherCondition.CLEAR_THEN_RAIN
            "115" -> WeatherCondition.CLEAR_THEN_SNOW
            "116" -> WeatherCondition.CLEAR_THEN_SNOW
            "117" -> WeatherCondition.CLEAR_THEN_SNOW
            "118" -> WeatherCondition.CLEAR_SKY
            "119" -> WeatherCondition.CLEAR_SKY
            "120" -> WeatherCondition.CLEAR_THEN_RAIN
            "121" -> WeatherCondition.RAIN_THEN_CLEAR
            "122" -> WeatherCondition.CLEAR_THEN_RAIN
            "123" -> WeatherCondition.CLEAR_SKY
            "124" -> WeatherCondition.CLEAR_SKY
            "125" -> WeatherCondition.THUNDERSTORM
            "126" -> WeatherCondition.RAIN_THEN_CLEAR
            "127" -> WeatherCondition.CLEAR_THEN_RAIN
            "128" -> WeatherCondition.CLEAR_THEN_RAIN
            "130" -> WeatherCondition.FOG_HAZE
            "131" -> WeatherCondition.FOG_HAZE
            "132" -> WeatherCondition.PARTLY_CLOUDY
            "140" -> WeatherCondition.THUNDERSTORM
            "160" -> WeatherCondition.SLEET
            "170" -> WeatherCondition.SLEET
            "181" -> WeatherCondition.CLEAR_SKY
            "200" -> WeatherCondition.OVERCAST
            "201" -> WeatherCondition.PARTLY_CLOUDY
            "202" -> WeatherCondition.RAIN
            "203" -> WeatherCondition.RAIN
            "204" -> WeatherCondition.SNOW
            "205" -> WeatherCondition.SNOW
            "206" -> WeatherCondition.SNOW
            "207" -> WeatherCondition.SLEET
            "208" -> WeatherCondition.THUNDERSTORM
            "209" -> WeatherCondition.FOG_HAZE
            "210" -> WeatherCondition.OVERCAST
            "211" -> WeatherCondition.CLOUDY_THEN_CLEAR
            "212" -> WeatherCondition.CLOUDY_THEN_RAIN
            "213" -> WeatherCondition.CLOUDY_THEN_RAIN
            "214" -> WeatherCondition.CLOUDY_THEN_RAIN
            "215" -> WeatherCondition.CLOUDY_THEN_SNOW
            "216" -> WeatherCondition.CLOUDY_THEN_SNOW
            "217" -> WeatherCondition.CLOUDY_THEN_SNOW
            "218" -> WeatherCondition.OVERCAST
            "219" -> WeatherCondition.OVERCAST
            "220" -> WeatherCondition.RAIN
            "221" -> WeatherCondition.RAIN_THEN_CLOUDY
            "222" -> WeatherCondition.CLOUDY_THEN_RAIN
            "223" -> WeatherCondition.PARTLY_CLOUDY
            "224" -> WeatherCondition.RAIN_THEN_CLOUDY
            "225" -> WeatherCondition.CLOUDY_THEN_RAIN
            "226" -> WeatherCondition.CLOUDY_THEN_RAIN
            "228" -> WeatherCondition.SNOW_THEN_CLOUDY
            "229" -> WeatherCondition.CLOUDY_THEN_SNOW
            "230" -> WeatherCondition.CLOUDY_THEN_SNOW
            "231" -> WeatherCondition.OVERCAST
            "240" -> WeatherCondition.THUNDERSTORM
            "250" -> WeatherCondition.SNOW
            "260" -> WeatherCondition.SLEET
            "270" -> WeatherCondition.SLEET
            "281" -> WeatherCondition.OVERCAST
            "300" -> WeatherCondition.RAIN
            "301" -> WeatherCondition.RAIN
            "302" -> WeatherCondition.RAIN
            "303" -> WeatherCondition.SLEET
            "304" -> WeatherCondition.SLEET
            "306" -> WeatherCondition.RAIN
            "308" -> WeatherCondition.RAIN
            "309" -> WeatherCondition.SLEET
            "311" -> WeatherCondition.RAIN_THEN_CLEAR
            "313" -> WeatherCondition.RAIN_THEN_CLOUDY
            "314" -> WeatherCondition.RAIN_THEN_SNOW
            "315" -> WeatherCondition.RAIN_THEN_SNOW
            "316" -> WeatherCondition.SLEET
            "317" -> WeatherCondition.SLEET
            "320" -> WeatherCondition.RAIN_THEN_CLEAR
            "321" -> WeatherCondition.RAIN_THEN_CLOUDY
            "322" -> WeatherCondition.RAIN_THEN_SNOW
            "323" -> WeatherCondition.RAIN_THEN_CLEAR
            "324" -> WeatherCondition.RAIN_THEN_CLEAR
            "325" -> WeatherCondition.RAIN_THEN_CLEAR
            "326" -> WeatherCondition.RAIN_THEN_SNOW
            "327" -> WeatherCondition.RAIN_THEN_SNOW
            "328" -> WeatherCondition.RAIN
            "329" -> WeatherCondition.SLEET
            "340" -> WeatherCondition.SLEET
            "350" -> WeatherCondition.THUNDERSTORM
            "361" -> WeatherCondition.SLEET
            "371" -> WeatherCondition.SLEET
            "400" -> WeatherCondition.SNOW
            "401" -> WeatherCondition.SNOW
            "402" -> WeatherCondition.SNOW
            "403" -> WeatherCondition.SLEET
            "405" -> WeatherCondition.SNOW
            "406" -> WeatherCondition.SNOW
            "407" -> WeatherCondition.SNOW
            "409" -> WeatherCondition.SLEET
            "411" -> WeatherCondition.SNOW_THEN_CLEAR
            "413" -> WeatherCondition.SNOW_THEN_CLOUDY
            "414" -> WeatherCondition.SNOW_THEN_RAIN
            "420" -> WeatherCondition.SNOW_THEN_CLEAR
            "421" -> WeatherCondition.SNOW_THEN_CLOUDY
            "422" -> WeatherCondition.SNOW_THEN_RAIN
            "423" -> WeatherCondition.SNOW_THEN_RAIN
            "425" -> WeatherCondition.SNOW
            "426" -> WeatherCondition.SNOW
            "427" -> WeatherCondition.SLEET
            "450" -> WeatherCondition.SNOW
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}
