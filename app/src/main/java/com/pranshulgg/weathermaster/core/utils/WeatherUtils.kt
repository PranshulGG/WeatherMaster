package com.pranshulgg.weathermaster.core.utils

import android.content.Context
import android.content.res.Configuration
import android.text.format.DateUtils
import android.util.Log
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.platform.LocalConfiguration
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.MoonTimings
import com.pranshulgg.weathermaster.core.model.SunTimings
import com.pranshulgg.weathermaster.core.model.WeatherResultType
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.domain.WeatherDaily
import com.pranshulgg.weathermaster.core.model.domain.WeatherHourly
import com.pranshulgg.weathermaster.core.model.getMoonPhase
import com.pranshulgg.weathermaster.data.local.entity.WeatherWithRelations
import org.shredzone.commons.suncalc.MoonIllumination
import org.shredzone.commons.suncalc.MoonPhase
import org.shredzone.commons.suncalc.MoonPosition
import org.shredzone.commons.suncalc.MoonTimes
import org.shredzone.commons.suncalc.SunTimes
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale
import java.util.concurrent.TimeUnit

object WeatherUtils {

    private const val MANUAL_REFRESH_MINUTES = 15
    private const val AUTO_REFRESH_MAX_MINUTES = 4500 // TODO: DEFAULT 45

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

    fun getLastUpdatedTimeString(context: Context, timeSeconds: Long): String {


        val milli = timeSeconds * 1000L
        val ageMillis = System.currentTimeMillis() - milli
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ageMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(ageMillis)
        val days = TimeUnit.MILLISECONDS.toDays(ageMillis)


        val lastUpdated = when {
            seconds < 60 -> context.getString(R.string.time_just_now)

            minutes < 60 -> context.resources.getQuantityString(
                R.plurals.time_minutes_ago,
                minutes.toInt(),
                minutes
            )

            hours < 24 -> context.resources.getQuantityString(
                R.plurals.time_hours_ago,
                hours.toInt(),
                hours
            )

            else -> context.resources.getQuantityString(
                R.plurals.time_days_ago,
                days.toInt(),
                days
            )
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


    fun shouldReturnCache(
        cache: WeatherWithRelations?,
        isManualRefresh: Boolean
    ): WeatherResultType {

        if (!DataSafe().isCacheSafe(cache)) return WeatherResultType.ERROR

        val cacheMilli = cache!!.current!!.lastUpdatedSecs * 1000L
        val ageMillis = System.currentTimeMillis() - cacheMilli
        val ageMinutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)

        val tooEarly = isManualRefresh && ageMinutes < MANUAL_REFRESH_MINUTES
        val maxAge = if (isManualRefresh) MANUAL_REFRESH_MINUTES else AUTO_REFRESH_MAX_MINUTES

        if (tooEarly) return WeatherResultType.REFRESH_TOO_EARLY

        val shouldReturnCache = ageMinutes < maxAge

        return if (shouldReturnCache) WeatherResultType.SUCCESS else WeatherResultType.ERROR
    }

    fun getSunTimings(
        timeSecs: List<Long>,
        zoneId: String,
        lat: Double,
        lon: Double
    ): List<SunTimings> {

        return timeSecs.map {
            val date = Instant.ofEpochSecond(it)
                .atZone(ZoneId.of(zoneId))
                .toLocalDate()


            val sunTimes = SunTimes.compute()
                .on(date)
                .fullCycle()
                .timezone(zoneId)
                .at(lat, lon)
                .execute()

            SunTimings(it, sunTimes.rise?.toEpochSecond(), sunTimes.set?.toEpochSecond())

        }

    }

    fun getMoonTimings(
        timeSecs: List<Long>,
        zoneId: String,
        lat: Double,
        lon: Double
    ): List<MoonTimings> {

        return timeSecs.map {
            val date = Instant.ofEpochSecond(it)
                .atZone(ZoneId.of(zoneId))
                .toLocalDate()


            val moonTimes = MoonTimes.compute()
                .on(date)
                .at(lat, lon)
                .timezone(zoneId)
                .execute()

            val phase = MoonIllumination.compute().on(date).execute().phase

            val phaseName = getMoonPhase(phase)


            MoonTimings(
                it,
                moonTimes.rise?.toEpochSecond(),
                moonTimes.set?.toEpochSecond(),
                phase = phaseName
            )
        }
    }
}

class DataSafe {

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

    fun isWeatherHourlyDomainSafe(weather: Weather?): Boolean {
        val isCacheSafe =
            weather != null &&
                    weather.hourly.isNotEmpty()

        return isCacheSafe
    }

    fun isWeatherDailyDomainSafe(weather: Weather?): Boolean {
        val isCacheSafe =
            weather != null &&
                    weather.daily.isNotEmpty()

        return isCacheSafe
    }

}