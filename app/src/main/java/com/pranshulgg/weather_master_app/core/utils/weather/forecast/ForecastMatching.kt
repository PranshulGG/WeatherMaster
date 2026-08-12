package com.pranshulgg.weather_master_app.core.utils.weather.forecast

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import java.time.Instant
import java.time.ZoneId
import java.util.TimeZone

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
    source: WeatherSource,
    zoneId: String,
    alwaysReturn24Hrs: Boolean = false,
    keepPastHour: Boolean = false
): List<WeatherHourly> {


    val startIndex = data.indexOfFirst { it.time >= currentMilli }.takeIf { it >= 0 }
        ?.minus(if (keepPastHour) 1 else 0) ?: return emptyList()

    val startDay = data[startIndex].time

    return if (alwaysReturn24Hrs) {
        data.drop(maxOf(0, startIndex)).take(24)
    } else {
        data.drop(maxOf(0, startIndex)).takeWhile { isSameDay(it.time, startDay, zoneId) }
    }


}


fun findHourlyIndexForTime(time: List<Long>, startMilli: Long = System.currentTimeMillis()): Int {
    val startIndex = time.indexOfFirst { it >= startMilli }.takeIf { it != -1 } ?: 0

    return maxOf(0, (startIndex))
}

fun isSameDay(time1: Long, time2: Long, zoneId: String): Boolean {
    val zone = safeZoneId(zoneId)
    return Instant.ofEpochMilli(time1).atZone(zone).toLocalDate() ==
            Instant.ofEpochMilli(time2).atZone(zone).toLocalDate()
}