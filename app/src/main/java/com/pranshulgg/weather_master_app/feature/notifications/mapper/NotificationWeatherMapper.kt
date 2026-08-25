package com.pranshulgg.weather_master_app.feature.notifications.mapper

import android.content.Context
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toIcon
import com.pranshulgg.weather_master_app.core.model.weather.toLabel
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.core.utils.weather.computing.summary.computeDaySummary
import com.pranshulgg.weather_master_app.feature.notifications.model.NotificationCurrentWeather
import com.pranshulgg.weather_master_app.feature.notifications.model.NotificationDailyWeather
import com.pranshulgg.weather_master_app.feature.notifications.model.NotificationWeatherModel
import kotlin.math.roundToInt


fun notificationWeatherMapper(
    weather: Weather,
    applicationContext: Context,
    units: WeatherUnits
): NotificationWeatherModel {

    val timezone = weather.location.timezone

    val currentCondition = weather.current.weatherCondition.toLabel(applicationContext)

    val currentIcon = weather.current.weatherCondition.toIcon(
        targetTimeMilli = weather.current.time,
        daily = weather.daily.firstOrNull()
    )

    val currentTemperature = TemperatureUnit.CELSIUS.convert(
        weather.current.temperature, units.tempUnit
    )?.roundToInt()


    val is24hr = PreferencesHelper.getBool("is24HrTimeFormat") ?: true

    val currentUvIndex = weather.current.uvIndex?.roundToInt()

    val timeFormatter: (Long?) -> String? = {
        if (it != null) {
            if (is24hr) to24HourTimeString(it, timezone) else to12HourTimeString(it, timezone)
        } else {
            null
        }
    }

    return NotificationWeatherModel(
        current = NotificationCurrentWeather(
            temp = "${currentTemperature}°",
            feelsLike = null,
            uvIndex = currentUvIndex.toString(),
            currentCondition = currentCondition,
            currentConditionIcon = currentIcon
        ),
        daily = List(weather.daily.take(3).size) { index ->

            val item = weather.daily[index]
            val summary = computeDaySummary(weather, applicationContext, index, units)
            val maxTemperature =
                TemperatureUnit.CELSIUS.convert(item.temperatureMax, units.tempUnit)?.roundToInt()
            val minTemperature =
                TemperatureUnit.CELSIUS.convert(item.temperatureMin, units.tempUnit)?.roundToInt()
            val icon = item.weatherCondition.toIcon(
                targetTimeMilli = System.currentTimeMillis()
            )


            NotificationDailyWeather(
                summary = summary,
                maxTemp = "${maxTemperature}°",
                minTemp = "${minTemperature}°",
                condition = item.weatherCondition.toLabel(applicationContext),
                conditionIcon = icon
            )
        }
    )
}