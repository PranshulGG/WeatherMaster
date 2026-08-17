package com.pranshulgg.weather_master_app.core.utils.formatters

import android.content.Context
import android.text.format.DateFormat
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.zone.ZoneRulesException
import java.util.concurrent.TimeUnit

/**
 * Modify the date and/or time pattern to adapt to the current locale.
 */
fun getLocalizedPattern(pattern: String): String {
    return DateFormat.getBestDateTimePattern(getCurrentAppLocale(), pattern)
}

fun to12HourTimeString(timeMilli: Long, zoneId: String, pattern: String = "ha"): String {
    val instant = Instant.ofEpochMilli(timeMilli)
    val formatter = DateTimeFormatter
        .ofPattern(getLocalizedPattern(pattern), getCurrentAppLocale())
        .withZone(safeZoneId(zoneId))

    return formatter.format(instant)
}

fun to24HourTimeString(timeMilli: Long, zoneId: String, pattern: String = "Hm"): String {
    val instant = Instant.ofEpochMilli(timeMilli)
    val formatter = DateTimeFormatter
        .ofPattern(getLocalizedPattern(pattern), getCurrentAppLocale())
        .withZone(safeZoneId(zoneId))

    return formatter.format(instant)
}


fun toWeekdayString(timeMilli: Long, zoneId: String): String {
    val instant = Instant.ofEpochMilli(timeMilli)
    val zonedDateTime = instant.atZone(safeZoneId(zoneId))
    val formatter = DateTimeFormatter.ofPattern("EEE", getCurrentAppLocale())

    return formatter.format(zonedDateTime)
}

fun toDateString(timeMilli: Long, zoneId: String, pattern: String = "ddMMMM"): String {
    val instant = Instant.ofEpochMilli(timeMilli)
    val zonedDateTime = instant.atZone(safeZoneId(zoneId))
    val formatter = DateTimeFormatter
        .ofPattern(getLocalizedPattern(pattern), getCurrentAppLocale())

    return formatter.format(zonedDateTime)

}

fun getLastUpdatedTimeString(context: Context, timeMilli: Long): String {

    val ageMillis = System.currentTimeMillis() - timeMilli
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

fun safeZoneId(id: String): ZoneId =
    try {
        ZoneId.of(id)
    } catch (e: ZoneRulesException) {
        ZoneId.systemDefault()
    }


fun getCurrentTimeFor(zoneId: String): Long =
    ZonedDateTime.now(safeZoneId(zoneId)).toInstant().toEpochMilli()