package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openweather

import com.pranshulgg.weather_master_app.core.model.astro.MoonPhase
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.OpenWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.bundle.OpenWeatherOneCallJsonBundle
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import kotlin.math.roundToInt

fun OpenWeatherOneCallJsonBundle.toDomain(location: Location): Weather {

    val zoneId = location.timezone

    val currentItem = current.data.first()
    val dailyItems = daily.data

    val dayStarts = dailyItems.map { it.dt.secondsToMilliseconds().normalizeToDay(zoneId) }

    val sunTimings = getSunTimings(dayStarts, zoneId, location.latitude, location.longitude)
    val moonTimings = getMoonTimings(dayStarts, zoneId, location.latitude, location.longitude)

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = currentItem.temp,
            humidity = currentItem.humidity,
            windSpeed = currentItem.windSpeedMs?.times(3.6),
            windDirection = WindDirection.toWindDirectionFromDegrees(currentItem.windDeg?.toInt()),
            pressureMsl = currentItem.pressure,
            visibility = currentItem.visibility?.toInt(),
            cloudCover = currentItem.clouds,
            uvIndex = currentItem.uvi,
            weatherCondition = OpenWeatherConditionMap.getCondition(currentItem.weather.firstOrNull()?.icon),
            feelsLike = currentItem.feelsLike,
            time = currentItem.dt.secondsToMilliseconds(),
            dewPoint = currentItem.dewPoint,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = hourly.data.map {
            WeatherHourly(
                temperature = it.temp,
                windSpeed = it.windSpeedMs?.times(3.6),
                windDirection = WindDirection.toWindDirectionFromDegrees(it.windDeg?.toInt()),
                rain = it.rain?.amountMm ?: 0.0,
                snowfall = it.snow?.amountMm,
                uvIndex = it.uvi,
                pressureMsl = it.pressure,
                visibility = it.visibility?.toInt(),
                humidity = it.humidity,
                dewPoint = it.dewPoint,
                weatherCondition = OpenWeatherConditionMap.getCondition(it.weather.firstOrNull()?.icon),
                time = it.dt.secondsToMilliseconds(),
                precipitationProbability = it.pop?.times(100)?.roundToInt()
            )
        },
        daily = dailyItems.mapIndexed { index, item ->

            val sun = sunTimings.getOrNull(index)
            val moon = moonTimings.getOrNull(index)

            WeatherDaily(
                temperatureMin = item.temp.min,
                temperatureMax = item.temp.max,
                windSpeed = item.windSpeedMs?.times(3.6),
                windDirection = WindDirection.toWindDirectionFromDegrees(item.windDeg?.toInt()),
                rainSum = item.rain ?: 0.0,
                snowfallSum = item.snow,
                uvIndexMax = item.uvi,
                weatherCondition = OpenWeatherConditionMap.getCondition(item.weather.firstOrNull()?.icon),
                time = dayStarts[index],
                precipitationProbabilityMax = item.pop?.times(100)?.roundToInt(),
                pressureMsl = item.pressure,
                visibility = null,
                humidity = item.humidity,
                dewPoint = item.dewPoint,
                sunrise = sun?.sunrise,
                sunset = sun?.sunset,
                moonrise = moon?.moonrise,
                moonset = moon?.moonset,
                moonPhase = moon?.phase ?: MoonPhase.NEW_MOON,
                dawn = sun?.dawn,
                dusk = sun?.dusk
            )
        }
    )
}
