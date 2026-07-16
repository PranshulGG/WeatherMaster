package com.pranshulgg.weather_master_app.core.utils.weather.computing.summary

import android.content.Context
import android.util.Log
import androidx.compose.ui.res.stringResource
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.toLabel
import com.pranshulgg.weather_master_app.core.prefs.AppPrefsState
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly
import java.time.Instant
import java.time.format.DateTimeFormatter


fun computeDaySummary(
    weather: Weather,
    context: Context,
    dailyIndex: Int = 0,
    units: WeatherUnits
): String {

    val hourly = findMatchingHourly(
        weather.hourly,
        weather.daily[dailyIndex].time,
        weather.location.source,
        weather.location.timezone
    )



    val (day, night) = hourly.partition { forecast ->
        toHour(forecast.time, weather.location.timezone).toInt() in 6..17
    }


    val daily = weather.daily[dailyIndex]


    val getCommonCondition: (List<WeatherHourly>) -> WeatherCondition? = { data ->
        data.map { it.weatherCondition }.groupingBy { it }
            .eachCount().entries.maxByOrNull { it.value }?.key
    }

    if (hourly.isEmpty()) {
        return context.getString(R.string.weather_no_data)
    }

    val rainDay = findRainStarting(day)
    val rainNight = findRainStarting(night)
    val snowDay = findSnowStarting(day)
    val snowNight = findSnowStarting(night)
    val peakUv = hourly.maxBy { it.uvIndex ?: 0.0 }
    val maxTemp = daily.temperatureMax
    val minTemp = daily.temperatureMin

    if (maxTemp == null || minTemp == null) {
        return context.getString(R.string.weather_no_data)
    }

    val avgTemp = hourly.take(12).map { it.temperature ?: 0.0 }.average()


    val conditionDay = getCommonCondition(day)?.toLabel(context)

    val conditionNight = getCommonCondition(night)?.toLabel(context)



    return getHeadline(
        summaryData = SummaryData(
            rainDay = rainDay,
            rainNight = rainNight,
            uv = SummaryPeakUv(
                at = peakUv.time,
                uv = peakUv.uvIndex ?: 0.0
            ),
            temps = SummaryTemps(
                max = maxTemp,
                min = minTemp,
                avg = avgTemp
            ),
            snowDay = snowDay,
            snowNight = snowNight,
            conditionDay = conditionDay,
            conditionNight = conditionNight
        ),
        weather.location.timezone,
        units,
        context
    )


}

private fun findRainStarting(hourly: List<WeatherHourly>): SummaryPeakRain {

    val empty = SummaryPeakRain(
        at = 0,
        amount = 0.0,
        probability = 0
    )

    if (hourly.isEmpty()) return empty

    val rainStartIndex = hourly.indexOfFirst { it.rain >= 0.5 }


    if (rainStartIndex == -1) {
        return empty
    }

    val data = hourly[rainStartIndex]


    return SummaryPeakRain(
        at = data.time,
        amount = data.rain,
        probability = data.precipitationProbability ?: 0
    )
}

private fun findSnowStarting(hourly: List<WeatherHourly>): SummaryPeakSnow {
    val empty = SummaryPeakSnow(
        at = 0,
        amount = 0.0,
        probability = 0
    )

    if (hourly.isEmpty()) return empty

    val snowStartIndex =
        hourly.indexOfFirst { (it.snowfall ?: 0.0) >= 0.5 }

    if (snowStartIndex == -1) {
        return empty
    }

    val data = hourly[snowStartIndex]
    return SummaryPeakSnow(
        at = data.time,
        amount = data.snowfall ?: 0.0,
        probability = data.precipitationProbability ?: 0

    )
}

private fun toHour(timeMilli: Long, zoneId: String): String {
    val instant = Instant.ofEpochMilli(timeMilli)
    val formatter = DateTimeFormatter.ofPattern("H", getCurrentAppLocale())
        .withZone(safeZoneId(zoneId))

    return formatter.format(instant)
}