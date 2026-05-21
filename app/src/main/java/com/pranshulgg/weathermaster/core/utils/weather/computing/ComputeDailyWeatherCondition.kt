package com.pranshulgg.weathermaster.core.utils.weather.computing

import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weathermaster.core.model.weather.WeatherCondition


fun computeDailyWeatherCondition(
    data: List<WeatherCondition>,
    defaultFallBack: WeatherCondition
): WeatherCondition {

    val rain =
        listOf(WeatherCondition.RAIN, WeatherCondition.HEAVY_RAIN, WeatherCondition.LIGHT_RAIN)
    val snow =
        listOf(WeatherCondition.SNOW, WeatherCondition.HEAVY_SNOW, WeatherCondition.LIGHT_SNOW)
    val clear = listOf(WeatherCondition.CLEAR_SKY, WeatherCondition.MOSTLY_CLEAR)


    val mostRepeated =
        data.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(2)
            .map { it.key to it.value }

    var streaks = 0

    for (i in 0 until data.size - 1) {
        if (data[i] == data[i + 1]) {
            streaks++
        }
    }

    val isClustered = streaks >= 2

    val primary = mostRepeated.firstOrNull()?.first

    val secondary = mostRepeated.getOrNull(1)
        ?.takeIf { it.second > 2 }
        ?.first

    val clusteredResult = when (primary) {
        in clear if secondary == WeatherCondition.OVERCAST -> WeatherCondition.CLEAR_THEN_CLOUDY
        in clear if secondary in rain -> WeatherCondition.CLEAR_THEN_RAIN
        in clear if secondary in snow -> WeatherCondition.CLEAR_THEN_SNOW
        WeatherCondition.OVERCAST if secondary in clear -> WeatherCondition.CLOUDY_THEN_CLEAR
        WeatherCondition.OVERCAST if secondary in rain -> WeatherCondition.CLOUDY_THEN_RAIN
        WeatherCondition.OVERCAST if secondary in snow -> WeatherCondition.CLOUDY_THEN_SNOW
        in rain if secondary in clear -> WeatherCondition.RAIN_THEN_CLEAR
        in rain if secondary == WeatherCondition.OVERCAST -> WeatherCondition.RAIN_THEN_CLOUDY
        in rain if secondary in snow -> WeatherCondition.RAIN_THEN_SNOW
        in snow if secondary in clear -> WeatherCondition.SNOW_THEN_CLEAR
        in snow if secondary == WeatherCondition.OVERCAST -> WeatherCondition.SNOW_THEN_CLOUDY
        in snow if secondary in rain -> WeatherCondition.SNOW_THEN_RAIN
        else -> defaultFallBack
    }

    val result = when (primary) {
        in clear if secondary == WeatherCondition.OVERCAST -> WeatherCondition.CLEAR_WITH_CLOUDY
        in clear if secondary in rain -> WeatherCondition.CLEAR_WITH_RAIN
        in clear if secondary in snow -> WeatherCondition.CLEAR_WITH_SNOW
        WeatherCondition.OVERCAST if secondary in clear -> WeatherCondition.CLOUDY_WITH_CLEAR
        WeatherCondition.OVERCAST if secondary in rain -> WeatherCondition.CLOUDY_WITH_RAIN
        WeatherCondition.OVERCAST if secondary in snow -> WeatherCondition.CLOUDY_WITH_SNOW
        in rain if secondary in clear -> WeatherCondition.RAIN_WITH_CLEAR
        in rain if secondary == WeatherCondition.OVERCAST -> WeatherCondition.RAIN_WITH_CLOUDY
        in rain if secondary in snow -> WeatherCondition.RAIN_WITH_SNOW
        in snow if secondary in clear -> WeatherCondition.SNOW_WITH_CLEAR
        in snow if secondary == WeatherCondition.OVERCAST -> WeatherCondition.SNOW_WITH_CLOUDY
        in snow if secondary in rain -> WeatherCondition.SNOW_WITH_RAIN
        else -> defaultFallBack
    }

    return if (!isClustered) result else clusteredResult
}