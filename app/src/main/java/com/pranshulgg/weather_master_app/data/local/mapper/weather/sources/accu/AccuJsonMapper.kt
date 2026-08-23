package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.accu

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.AccuWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.bundle.AccuWeatherBundle
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import kotlin.math.roundToInt


fun AccuWeatherBundle.toDomain(location: Location): Weather {

    val current = this.current
    val hourly = this.hourly
    val daily = this.daily.daily


    val sunTimings = getSunTimings(
        daily.map {
            it.time.secondsToMilliseconds().normalizeToDay(location.timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        daily.map {
            it.time.secondsToMilliseconds().normalizeToDay(location.timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = current.temperature.metric.value,
            humidity = current.humidity?.toDouble(),
            windSpeed = current.wind.speed.metric.value,
            windDirection = WindDirection.toWindDirectionFromDegrees(current.wind.direction.degrees),
            pressureMsl = current.pressure.metric.value,
            visibility = DistanceUnit.KM.convert(current.visibility.metric.value, DistanceUnit.M)
                ?.roundToInt(),
            cloudCover = null, // NOT USED IN THE APP
            uvIndex = current.uvIndex,
            weatherCondition = AccuWeatherConditionMap.getCondition(current.weatherIcon),
            feelsLike = current.feelsLike.metric.value,
            time = current.time.secondsToMilliseconds(),
            dewPoint = current.dewPoint.metric.value,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = hourly.map { hour ->
            WeatherHourly(
                temperature = hour.temperature.value,
                windSpeed = hour.wind.speed.value,
                windDirection = WindDirection.toWindDirectionFromDegrees(hour.wind.direction.degrees),
                rain = hour.rain.value ?: 0.0,
                snowfall = PrecipitationUnit.CM.convert(hour.snowCm.value, PrecipitationUnit.MM),
                uvIndex = hour.uvIndex,
                pressureMsl = null,
                visibility = DistanceUnit.KM.convert(hour.visibility.value, DistanceUnit.M)
                    ?.roundToInt(),
                humidity = hour.humidity?.toDouble(),
                dewPoint = hour.dewPoint.value,
                weatherCondition = AccuWeatherConditionMap.getCondition(hour.weatherIcon),
                time = hour.time.secondsToMilliseconds(),
                precipitationProbability = hour.precipitationProbability
            )
        },
        daily = daily.mapIndexed { index, item ->

            val windSpeed = listOf(
                item.day.wind.speed.value ?: 0.0,
                item.night.wind.speed.value ?: 0.0
            ).average()

            val windDirection = listOf(
                item.day.wind.direction.degrees ?: 0.0,
                item.night.wind.direction.degrees ?: 0.0
            ).average()

            val rain = listOf(item.day.rain.value ?: 0.0, item.night.rain.value ?: 0.0).sum()
            val snow = listOf(item.day.snowCm.value ?: 0.0, item.night.snowCm.value ?: 0.0).sum()

            val condition =
                computeDailyWeatherCondition(List(12) { AccuWeatherConditionMap.getCondition(item.day.icon) } + AccuWeatherConditionMap.getCondition(
                    item.night.icon
                ),
                    WeatherCondition.NO_CONDITION_FOUND)


            val precipitationProbabilityMax = listOf(
                item.day.precipitationProbability,
                item.night.precipitationProbability
            ).filterNotNull().maxOrNull()

            val humidity = listOf(
                item.day.humidity.value?.toDouble() ?: 0.0,
                item.night.humidity.value?.toDouble() ?: 0.0
            ).average()

            WeatherDaily(
                temperatureMin = item.temperature.min.value,
                temperatureMax = item.temperature.max.value,
                windSpeed = windSpeed,
                windDirection = WindDirection.toWindDirectionFromDegrees(windDirection.roundToInt()),
                rainSum = rain,
                snowfallSum = PrecipitationUnit.CM.convert(snow, PrecipitationUnit.MM),
                uvIndexMax = item.day.uvIndex.value,
                weatherCondition = condition,
                time = item.time.secondsToMilliseconds().normalizeToDay(location.timezone),
                precipitationProbabilityMax = precipitationProbabilityMax,
                pressureMsl = null,
                visibility = null,
                humidity = humidity,
                dewPoint = null,
                sunrise = sunTimings[index].sunrise,
                sunset = sunTimings[index].sunset,
                moonrise = moonTimings[index].moonrise,
                moonset = moonTimings[index].moonset,
                moonPhase = moonTimings[index].phase,
                dawn = sunTimings[index].dawn,
                dusk = sunTimings[index].dusk
            )
        },
    )
}