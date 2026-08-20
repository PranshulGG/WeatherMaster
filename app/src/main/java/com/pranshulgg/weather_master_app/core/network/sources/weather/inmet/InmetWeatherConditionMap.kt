package com.pranshulgg.weather_master_app.core.network.sources.weather.inmet

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

object InmetWeatherConditionMap {

    fun getCondition(codIcone: String?, resumo: String?): WeatherCondition {
        val byCode = codIcone?.toIntOrNull()?.let { mapByCode(it) }
        if (byCode != null && byCode != WeatherCondition.NO_CONDITION_FOUND) return byCode

        val byText = resumo?.let { mapByText(it) }
        if (byText != null) return byText

        return WeatherCondition.NO_CONDITION_FOUND
    }

    private fun mapByCode(code: Int): WeatherCondition {
        return when (code) {
            1, 25 -> WeatherCondition.CLEAR_SKY
            2, 29, 30 -> WeatherCondition.PARTLY_CLOUDY
            3 -> WeatherCondition.OVERCAST
            4 -> WeatherCondition.OVERCAST
            5, 6 -> WeatherCondition.RAIN
            7 -> WeatherCondition.THUNDERSTORM
            8 -> WeatherCondition.THUNDERSTORM
            9, 11 -> WeatherCondition.FOG_HAZE
            10 -> WeatherCondition.SNOW
            12 -> WeatherCondition.HAIL
            13 -> WeatherCondition.LIGHT_SNOW
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }

    private fun mapByText(text: String): WeatherCondition? {
        val lower = text.lowercase().trim()

        if (lower.contains("tempestade") || lower.contains("trovoada") ||
            lower.contains("raio") || lower.contains("relâmpago")
        ) return WeatherCondition.THUNDERSTORM

        if (lower.contains("granizo")) return WeatherCondition.HAIL
        if (lower.contains("neve")) return WeatherCondition.SNOW
        if (lower.contains("geada")) return WeatherCondition.LIGHT_SNOW

        if (lower.contains("chuva forte") || lower.contains("pancada forte") ||
            lower.contains("temporada")
        ) return WeatherCondition.HEAVY_RAIN

        if (lower.contains("chuva fraca") || lower.contains("leve chuva") ||
            lower.contains("garoa")
        ) return WeatherCondition.LIGHT_RAIN

        if (lower.contains("chuva") || lower.contains("pancada")) return WeatherCondition.RAIN

        if (lower.contains("nevoeiro") || lower.contains("névoa") ||
            lower.contains("neblina") || lower.contains("fumaça")
        ) return WeatherCondition.FOG_HAZE

        if (lower.contains("encoberto")) return WeatherCondition.OVERCAST
        if (lower.contains("nublado")) return WeatherCondition.OVERCAST

        if (lower.contains("poucas nuvens") || lower.contains("parcialmente nublado") ||
            lower.contains("sol entre nuvens")
        ) return WeatherCondition.PARTLY_CLOUDY

        if (lower.contains("céu claro") || lower.contains("ceu claro") ||
            lower.contains("sol") || lower.contains("limpo")
        ) return WeatherCondition.CLEAR_SKY

        return null
    }
}
