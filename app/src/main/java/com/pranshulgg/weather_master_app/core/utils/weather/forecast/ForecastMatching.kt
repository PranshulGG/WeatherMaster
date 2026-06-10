package com.pranshulgg.weather_master_app.core.utils.weather.forecast

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import java.time.Instant
import java.time.ZoneId

fun findMatchingDaily(
    targetTimeMilli: Long,
    dailyList: List<WeatherDaily>,
    timezone: String
): WeatherDaily? {

    val targetDate = Instant.ofEpochMilli(targetTimeMilli)
        .atZone(safeZoneId(timezone))
        .toLocalDate()


    return dailyList.firstOrNull { daily ->
        val dailyDate = Instant.ofEpochMilli(daily.time)
            .atZone(safeZoneId(timezone))
            .toLocalDate()

        targetDate == dailyDate
    }

}


fun findMatchingHourly(
    data: List<WeatherHourly>,
    currentMilli: Long,
    source: WeatherSource
): List<WeatherHourly> {


    val startIndex = data.indexOfFirst { it.time >= currentMilli }.takeIf { it != -1 } ?: 0

    return data.drop(maxOf(0, startIndex)).take(source.hourlyAggregationLimitHours)

}


fun findHourlyIndexForTime(time: List<Long>, startMilli: Long = System.currentTimeMillis()): Int {
    val startIndex = time.indexOfFirst { it >= startMilli }.takeIf { it != -1 } ?: 0

    return maxOf(0, (startIndex))
}

