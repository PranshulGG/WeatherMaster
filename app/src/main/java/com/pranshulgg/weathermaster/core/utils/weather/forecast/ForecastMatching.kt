package com.pranshulgg.weathermaster.core.utils.weather.forecast

import com.pranshulgg.weathermaster.core.model.domain.WeatherDaily
import com.pranshulgg.weathermaster.core.model.domain.WeatherHourly
import java.time.Instant
import java.time.ZoneId

fun findMatchingDaily(
    targetTimeSecs: Long,
    dailyList: List<WeatherDaily>,
    timezone: String
): WeatherDaily? {

    val targetDate = Instant.ofEpochSecond(targetTimeSecs)
        .atZone(ZoneId.of(timezone))
        .toLocalDate()


    return dailyList.firstOrNull { daily ->
        val dailyDate = Instant.ofEpochSecond(daily.time)
            .atZone(ZoneId.of(timezone))
            .toLocalDate()

        targetDate == dailyDate
    }

}


fun findMatchingHourly(
    data: List<WeatherHourly>,
    currentSeconds: Long,
    limit: Int = 24
): List<WeatherHourly> {

    val startIndex = data.indexOfFirst { it.time >= currentSeconds }.takeIf { it != -1 } ?: 0

    return data.drop(startIndex - 1).take(limit)

}