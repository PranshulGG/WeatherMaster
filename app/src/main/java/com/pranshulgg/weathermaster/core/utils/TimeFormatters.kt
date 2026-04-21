package com.pranshulgg.weathermaster.core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


class TimeFormatters {

    fun to12HourTimeString(millis: Long, zoneId: String): String {
        val instant = Instant.ofEpochMilli(millis)
        val formatter = DateTimeFormatter.ofPattern("ha", Locale.ENGLISH)
            .withZone(ZoneId.of(zoneId))

        return formatter.format(instant)
    }

    fun toWeekdayString(millis: Long, zoneId: String): String {
        val instant = Instant.ofEpochMilli(millis)
        val zonedDateTime = instant.atZone(ZoneId.of(zoneId))
        val formatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)

        return formatter.format(zonedDateTime)
    }

}