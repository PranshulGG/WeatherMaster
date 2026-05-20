package com.pranshulgg.weathermaster.core.utils.weather.forecast

import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherHourly
import java.time.Instant
import java.time.ZoneId

fun findMatchingDaily(
    targetTimeMilli: Long,
    dailyList: List<WeatherDaily>,
    timezone: String
): WeatherDaily? {

    val targetDate = Instant.ofEpochMilli(targetTimeMilli)
        .atZone(ZoneId.of(timezone))
        .toLocalDate()


    return dailyList.firstOrNull { daily ->
        val dailyDate = Instant.ofEpochMilli(daily.time)
            .atZone(ZoneId.of(timezone))
            .toLocalDate()

        targetDate == dailyDate
    }

}


fun findMatchingHourly(
    data: List<WeatherHourly>,
    currentMilli: Long,
    limit: Int = 24
): List<WeatherHourly> {

    val startIndex = data.indexOfFirst { it.time >= currentMilli }.takeIf { it != -1 } ?: 0

    return data.drop(maxOf(0, startIndex - 1)).take(limit)

}

fun findHourlyIndexForTime(time: List<Long>, startMilli: Long = System.currentTimeMillis()): Int {
    val startIndex = time.indexOfFirst { it >= startMilli }.takeIf { it != -1 } ?: 0

    return maxOf(0, (startIndex - 1))
}