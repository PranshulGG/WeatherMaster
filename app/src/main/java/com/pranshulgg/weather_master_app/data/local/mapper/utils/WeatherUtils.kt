package com.pranshulgg.weather_master_app.data.local.mapper.utils

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection

object WeatherUtils {


    fun Iterable<WindDirection?>.getDominantWindDirection(): WindDirection? {
        return mapNotNull { it }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
    }

    fun Iterable<WeatherCondition?>.getDominantCondition(): WeatherCondition? =
        mapNotNull { it }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key

    fun Iterable<Double?>.safeAverage(): Double? =
        mapNotNull { it }
            .takeIf { it.isNotEmpty() }
            ?.average()


    fun Iterable<Double?>.safeMin(): Double? =
        mapNotNull { it }.minOrNull()

    fun Iterable<Double?>.safeMax(): Double? =
        mapNotNull { it }.maxOrNull()

    fun Iterable<Double?>.sumOrZero(): Double =
        sumOf { it ?: 0.0 }

}