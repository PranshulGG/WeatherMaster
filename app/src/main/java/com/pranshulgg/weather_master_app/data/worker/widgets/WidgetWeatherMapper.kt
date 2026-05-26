package com.pranshulgg.weather_master_app.data.worker.widgets

import android.content.Context
import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toFroggy
import com.pranshulgg.weather_master_app.core.model.weather.toIcon
import com.pranshulgg.weather_master_app.core.model.weather.toLabel
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.toWeekdayString
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingDaily
import com.pranshulgg.weather_master_app.widgets.model.WidgetDailyItem
import com.pranshulgg.weather_master_app.widgets.model.WidgetHourlyItem
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import kotlinx.serialization.json.Json
import kotlin.math.roundToInt


fun widgetWeatherMapper(
    weather: Weather,
    applicationContext: Context,
    units: WeatherUnits
): String {

    // Map everything
    val timezone = weather.location.timezone

    val currentCondition = weather.current.weatherCondition.toLabel(applicationContext)
    val currentIcon = weather.current.weatherCondition.toIcon(
        targetTimeMilli = weather.current.time,
        daily = weather.daily.firstOrNull()
    )

    val currentFrogIcon = weather.current.weatherCondition.toFroggy(
        targetTimeMilli = weather.current.time,
        daily = weather.daily.firstOrNull()
    )


    val currentTemperature = TemperatureUnit.CELSIUS.convert(
        weather.current.temperature, units.tempUnit
    )?.roundToInt()

    val hourlyStartIndex = findHourlyIndexForTime(weather.hourly.map { it.time })

    val hourly = weather.hourly
        .drop(hourlyStartIndex)
        .take(6)
        .map {
            val temperature =
                TemperatureUnit.CELSIUS.convert(it.temperature, units.tempUnit)?.roundToInt()
            val matchingDaily = findMatchingDaily(
                it.time,
                weather.daily,
                weather.location.timezone
            )

            val icon = it.weatherCondition.toIcon(matchingDaily, it.time)
            WidgetHourlyItem(
                time = to12HourTimeString(it.time, timezone),
                temp = "${temperature}°",
                conditionIcon = icon
            )
        }

    val daily = weather.daily
        .take(5)
        .map {
            val maxTemperature =
                TemperatureUnit.CELSIUS.convert(it.temperatureMax, units.tempUnit)?.roundToInt()
            val minTemperature =
                TemperatureUnit.CELSIUS.convert(it.temperatureMin, units.tempUnit)?.roundToInt()
            val icon = it.weatherCondition.toIcon(
                targetTimeMilli = System.currentTimeMillis()
            )
            WidgetDailyItem(
                tempMax = "${maxTemperature}°",
                tempMin = "${minTemperature}°",
                conditionIcon = icon,
                time = toWeekdayString(it.time, timezone)
            )
        }

    val widgetState = WidgetWeather(
        currentTemp = "${currentTemperature}°",
        hourly = hourly,
        daily = daily,
        currentCondition = currentCondition,
        currentIcon = currentIcon,
        currentFrog = currentFrogIcon,
        locationName = weather.location.name,
    )

    // Convert to string
    return Json.encodeToString(widgetState)
}