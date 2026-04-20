package com.pranshulgg.weathermaster.core.utils

import com.pranshulgg.weathermaster.core.model.domain.WeatherHourly

class WeatherUtils {

    fun filterHourlyDataForDate(
        data: List<WeatherHourly>,
        currentSeconds: Long,
        limit: Int = 12
    ): List<WeatherHourly> {


        val startIndex = data.indexOfFirst { it.time >= currentSeconds }.takeIf { it != -1 } ?: 0

        return data.drop(startIndex).take(limit)

    }

}