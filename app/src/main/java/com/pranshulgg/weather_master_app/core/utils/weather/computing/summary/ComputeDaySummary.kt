package com.pranshulgg.weather_master_app.core.utils.weather.computing.summary

import android.content.Context
import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.toLabel
import com.pranshulgg.weather_master_app.core.prefs.AppPrefsState
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly


fun computeDaySummary(
    weather: Weather,
    context: Context,
    dailyIndex: Int = 0,
    units: WeatherUnits
): String {

    val hourly = findMatchingHourly(
        weather.hourly,
        weather.daily[dailyIndex].time,
        weather.location.source
    )

    val rain = findRainStarting(hourly)
    val snow = findSnowStarting(hourly)
    val peakUv = hourly.maxBy { it.uvIndex ?: 0.0 }
    val maxTemp = hourly.maxOf { it.temperature ?: 0.0 }
    val minTemp = hourly.minOf { it.temperature ?: 0.0 }
    val avgTemp = hourly.map { it.temperature ?: 0.0 }.average()


    val avgCondition = hourly.map { it.weatherCondition }.groupingBy { it }
        .eachCount().entries.maxByOrNull { it.key }?.key

    val condition =
        computeDailyWeatherCondition(hourly.map { it.weatherCondition }, avgCondition!!).toLabel(
            context
        )

    return getHeadline(
        summaryData = SummaryData(
            rain = rain,
            uv = SummaryPeakUv(
                at = peakUv.time,
                uv = peakUv.uvIndex ?: 0.0
            ),
            temps = SummaryTemps(
                max = maxTemp,
                min = minTemp,
                avg = avgTemp
            ),
            condition = condition,
            snow = snow,
        ),
        weather.location.timezone,
        units,
        context
    )


}

private fun findRainStarting(hourly: List<WeatherHourly>): SummaryPeakRain {

    val rainStartIndex = hourly.indexOfFirst { it.rain > 0.0 }.plus(1).coerceIn(0, hourly.size - 1)


    val data = hourly[rainStartIndex]
    return SummaryPeakRain(
        at = data.time,
        amount = data.rain,
        probability = data.precipitationProbability ?: 0
    )
}

private fun findSnowStarting(hourly: List<WeatherHourly>): SummaryPeakSnow {

    val snowStartIndex =
        hourly.indexOfFirst { (it.snowfall ?: 0.0) > 0.0 }.plus(1).coerceIn(0, hourly.size - 1)

    val data = hourly[snowStartIndex]
    return SummaryPeakSnow(
        at = data.time,
        amount = data.snowfall ?: 0.0,
        probability = data.precipitationProbability ?: 0
    )
}

