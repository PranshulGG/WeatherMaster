package com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json

// getAmedasCurrent() response is Map<timestampKey, JmaAmedasCurrentJson>, one entry per 10-min
// reading in the requested hour - take the entry with the max timestamp key for "current".
// Every field is a [value, qualityFlag] pair - use .value() below, ignore the flag.
data class JmaAmedasCurrentJson(
    val temp: List<Double>? = null,
    val humidity: List<Double>? = null,
    val wind: List<Double>? = null, // m/s
    val windDirection: List<Double>? = null, // 1-16 compass index
    val precipitation1h: List<Double>? = null,
    val pressure: List<Double>? = null // only present for major stations
)

fun List<Double>?.value(): Double? = this?.firstOrNull()
