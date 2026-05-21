package com.pranshulgg.weathermaster.core.utils

import java.time.Instant
import java.time.ZoneId


object Extensions {
    private val formatter = java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

    fun Long.secondsToMilliseconds(): Long {
        return (this * 1000L)
    }

    fun Double.fahrenheitToCelsius(): Double {
        return (this - 32.0) * 5.0 / 9.0
    }

    fun Double.kmhToMs(): Double {
        return (this / 3.6)
    }

    fun Double.mphToKmh(): Double {
        return (this * 1.609)
    }

    fun Double.mmToCm(): Double {
        return (this / 10)
    }

    fun Double.pressurePaToHpa(): Double {
        return (this / 100)
    }

    fun String.iso8601TimestampToMilliseconds(): Long {
        val cleaned = this.trim().substringBefore("/").trim()

        return java.time.OffsetDateTime
            .parse(cleaned, formatter)
            .toInstant()
            .toEpochMilli()
    }


    // UTC to Local
    fun Long.normalizeToDay(zoneId: String): Long {

        val zone = ZoneId.of(zoneId)

        val localDate = Instant.ofEpochMilli(this)
            .atZone(zone)
            .toLocalDate()

        return localDate
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()
    }

    fun Long.toLocalMillis(zoneId: String): Long {
        return Instant.ofEpochMilli(this)
            .atZone(ZoneId.of(zoneId))
            .toInstant()
            .toEpochMilli()
    }
}