package com.pranshulgg.weathermaster.core.utils.formatters

import android.content.Context
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.utils.locale.getCurrentAppLocale
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

fun to12HourTimeString(timeMilli: Long, zoneId: String, pattern: String = "ha"): String {
    val instant = Instant.ofEpochMilli(timeMilli)
    val formatter = DateTimeFormatter.ofPattern(pattern, getCurrentAppLocale())
        .withZone(ZoneId.of(zoneId))

    return formatter.format(instant)
}

fun toWeekdayString(timeMilli: Long, zoneId: String): String {
    val instant = Instant.ofEpochMilli(timeMilli)
    val zonedDateTime = instant.atZone(ZoneId.of(zoneId))
    val formatter = DateTimeFormatter.ofPattern("EEE", getCurrentAppLocale())

    return formatter.format(zonedDateTime)
}

fun toDateString(timeMilli: Long, zoneId: String, pattern: String = "dd MMMM"): String {
    val instant = Instant.ofEpochMilli(timeMilli)
    val zonedDateTime = instant.atZone(ZoneId.of(zoneId))
    val formatter = DateTimeFormatter.ofPattern(pattern, getCurrentAppLocale())

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

fun Long.toMilliseconds(): Long {
    return (this * 1000L)
}