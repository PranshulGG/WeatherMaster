package com.pranshulgg.weathermaster.core.utils

import android.content.res.Configuration
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.platform.LocalConfiguration
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.pranshulgg.weathermaster.core.model.WeatherResultType
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.domain.WeatherDaily
import com.pranshulgg.weathermaster.core.model.domain.WeatherHourly
import com.pranshulgg.weathermaster.data.local.entity.WeatherWithRelations
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import java.util.concurrent.TimeUnit

object WeatherUtils {
    fun filterHourlyDataForDate(
        data: List<WeatherHourly>,
        currentSeconds: Long,
        limit: Int = 24
    ): List<WeatherHourly> {


        val startIndex = data.indexOfFirst { it.time >= currentSeconds }.takeIf { it != -1 } ?: 0

        return data.drop(startIndex - 1).take(limit)

    }

    fun formatNumbers(locale: Locale = Locale.US, number: Double, decimalPlaces: Int = 0): String {
        return "%,.${decimalPlaces}f".format(locale, number)
    }

    fun getLastUpdatedTimeString(timeSeconds: Long): String {
        val milli = timeSeconds * 1000L
        val ageMillis = System.currentTimeMillis() - milli
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ageMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(ageMillis)
        val days = TimeUnit.MILLISECONDS.toDays(ageMillis)


        val lastUpdated = when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes ${if (minutes == 1L) "min" else "mins"} ago"
            hours < 24 -> "$hours ${if (hours == 1L) "hr" else "hrs"} ago"
            else -> "$days ${if (days == 1L) "day" else "days"} ago"
        }

        return lastUpdated
    }

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


    fun isCacheSafe(cache: WeatherWithRelations?): Boolean {
        val isCacheSafe = cache != null &&
                cache.daily.isNotEmpty() &&
                cache.hourly.isNotEmpty() &&
                cache.current != null

        return isCacheSafe
    }

    fun isWeatherDomainSafe(weather: Weather?): Boolean {
        val isCacheSafe = weather != null &&
                weather.daily.isNotEmpty() &&
                weather.hourly.isNotEmpty()
        return isCacheSafe
    }

    fun shouldReturnCache(cache: WeatherWithRelations?, isRefresh: Boolean): WeatherResultType {

        if (!isCacheSafe(cache)) return WeatherResultType.ERROR

        if (cache?.current == null) return WeatherResultType.ERROR

        val cacheMilli = cache.current.lastUpdatedSecs * 1000L
        val ageMillis = System.currentTimeMillis() - cacheMilli
        val ageMinutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)

        val tooEarly = isRefresh && ageMinutes < 15
        val maxAge = if (isRefresh) 15 else 4500

        if (tooEarly) return WeatherResultType.REFRESH_TOO_EARLY

        val isSafe = ageMinutes < maxAge

        return if (isSafe) WeatherResultType.SUCCESS else WeatherResultType.ERROR
    }


}