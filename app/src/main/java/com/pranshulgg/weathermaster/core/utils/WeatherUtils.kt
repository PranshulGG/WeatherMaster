package com.pranshulgg.weathermaster.core.utils

import com.pranshulgg.weathermaster.core.model.domain.WeatherHourly
import java.util.Locale

class WeatherUtils {

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

}