package com.pranshulgg.weathermaster.core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


class TimeFormatters {

    fun to12HourTimeString(millis: Long): String {
        val instant = Instant.ofEpochMilli(millis)
        val formatter = DateTimeFormatter.ofPattern("ha", Locale.ENGLISH)
            .withZone(ZoneId.systemDefault())

        return formatter.format(instant)
    }

}