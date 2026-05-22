package com.pranshulgg.weathermaster.core.utils.weather.computing

import com.pranshulgg.weathermaster.core.model.weather.WeatherCondition

/**
 * We manually compute conditions for daily forecast
 * Since some sources use 12/24h aggravation, it gets confusing to get the middle ground
 */
fun computeDailyWeatherCondition(
    data: List<WeatherCondition>,
    defaultFallBack: WeatherCondition
): WeatherCondition {


    /**
     * We group what count as Rain, Snow, Cloud, Clear
     * Leave conditions like THUNDERSTORM, they need more importance
     */
    val rain = listOf(
        WeatherCondition.RAIN,
        WeatherCondition.HEAVY_RAIN,
        WeatherCondition.LIGHT_RAIN
    )
    val snow = listOf(
        WeatherCondition.SNOW,
        WeatherCondition.HEAVY_SNOW,
        WeatherCondition.LIGHT_SNOW
    )
    val cloudy = listOf(WeatherCondition.OVERCAST, WeatherCondition.PARTLY_CLOUDY)
    val clear = listOf(WeatherCondition.CLEAR_SKY, WeatherCondition.MOSTLY_CLEAR)

    // Combine MOSTLY_CLEAR -> CLEAR, both are treated the same
    val dataNormalized = data.map {
        when (it) {
            in clear -> clear.first()
            else -> it
        }
    }

    // Add weight so if there's RAIN/SNOW, it should always count
    val weight = { w: WeatherCondition ->
        when (w) {
            in rain -> 3
            in snow -> 2
            in cloudy -> 1
            in clear -> 1
            else -> 1
        }
    }

    // Count and add weights
    val counts = dataNormalized
        .groupingBy { it }
        .fold(0) { acc, w -> acc + weight(w) }

    // Pick the top 2, we keep the order as is so they don't flip, ignore weak stuff
    val topTwo = counts
        .filter { it.value > 2 }
        .keys
        .sortedBy { weather ->
            dataNormalized.indexOf(weather)
        }
        .take(2)


    val primary = topTwo.getOrNull(0)
    val secondary = topTwo.getOrNull(1)


    val primaryCount = primary?.let { counts[it] } ?: 0
    val secondaryCount = secondary?.let { counts[it] } ?: 0


    val firstSecondaryIndex = data.indexOf(secondary)

    /**
     * Drop everything before the secondary index
     * Check how many times primary shows up after that
     * Need this so conditions like ☀️☀️☀️🌧️🌧️🌧️☀️☀️☀️ don't count as clustered
     */
    val primaryReturnsLaterCount =
        data.drop(firstSecondaryIndex + 1)
            .count { it == primary }

    // More or equal it is not a cluster
    val primaryReturnsLater = primaryReturnsLaterCount >= 3

    val isClustered = primaryCount > 5 && secondaryCount > 5 && !primaryReturnsLater

    val clusteredResult = when (primary) {
        in clear if secondary in cloudy -> WeatherCondition.CLEAR_THEN_CLOUDY
        in clear if secondary in rain -> WeatherCondition.CLEAR_THEN_RAIN
        in clear if secondary in snow -> WeatherCondition.CLEAR_THEN_SNOW
        in cloudy if secondary in clear -> WeatherCondition.CLOUDY_THEN_CLEAR
        in cloudy if secondary in rain -> WeatherCondition.CLOUDY_THEN_RAIN
        in cloudy if secondary in snow -> WeatherCondition.CLOUDY_THEN_SNOW
        in rain if secondary in clear -> WeatherCondition.RAIN_THEN_CLEAR
        in rain if secondary in cloudy -> WeatherCondition.RAIN_THEN_CLOUDY
        in rain if secondary in snow -> WeatherCondition.RAIN_THEN_SNOW
        in snow if secondary in clear -> WeatherCondition.SNOW_THEN_CLEAR
        in snow if secondary in cloudy -> WeatherCondition.SNOW_THEN_CLOUDY
        in snow if secondary in rain -> WeatherCondition.SNOW_THEN_RAIN
        else -> primary ?: defaultFallBack
    }

    val result = when (primary) {
        in clear if secondary in cloudy -> WeatherCondition.CLEAR_WITH_CLOUDY
        in clear if secondary in rain -> WeatherCondition.CLEAR_WITH_RAIN
        in clear if secondary in snow -> WeatherCondition.CLEAR_WITH_SNOW
        in cloudy if secondary in clear -> WeatherCondition.CLOUDY_WITH_CLEAR
        in cloudy if secondary in rain -> WeatherCondition.CLOUDY_WITH_RAIN
        in cloudy if secondary in snow -> WeatherCondition.CLOUDY_WITH_SNOW
        in rain if secondary in clear -> WeatherCondition.RAIN_WITH_CLEAR
        in rain if secondary in cloudy -> WeatherCondition.RAIN_WITH_CLOUDY
        in rain if secondary in snow -> WeatherCondition.RAIN_WITH_SNOW
        in snow if secondary in clear -> WeatherCondition.SNOW_WITH_CLEAR
        in snow if secondary in cloudy -> WeatherCondition.SNOW_WITH_CLOUDY
        in snow if secondary in rain -> WeatherCondition.SNOW_WITH_RAIN
        else -> primary ?: defaultFallBack
    }

    return if (!isClustered) result else clusteredResult
}