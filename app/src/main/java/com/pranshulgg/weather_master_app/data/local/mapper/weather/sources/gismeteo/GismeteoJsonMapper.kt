package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.gismeteo

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.GismeteoWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.model.GismeteoModel
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.model.GismeteoModelHourly
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition

private fun fixDateString(date: String): String = "${date}T00:00Z" // DAILY
private fun fixTimeString(date: String): String = "${date}Z" // HOURLY

fun GismeteoModel.toDomain(location: Location): Weather {


    val convertWindDirection: (Int?) -> WindDirection? = {
        when (it) {
            1 -> WindDirection.N
            2 -> WindDirection.NE
            3 -> WindDirection.E
            4 -> WindDirection.SE
            5 -> WindDirection.S
            6 -> WindDirection.SW
            7 -> WindDirection.W
            8 -> WindDirection.NW
            else -> null
        }

    }

    val zoneId = location.timezone

    val sunTimings = getSunTimings(
        daily.map {
            fixDateString(it.time).iso8601TimestampToMilliseconds().normalizeToDay(zoneId)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        daily.map {
            fixDateString(it.time).iso8601TimestampToMilliseconds().normalizeToDay(zoneId)

        },
        location.timezone,
        location.latitude,
        location.longitude
    )
    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = current.temperature,
            humidity = current.humidity ?: 0.0,
            windSpeed = WindSpeedUnit.MPS.convert(current.windSpeedMs, WindSpeedUnit.KPH),
            windDirection = convertWindDirection(current.windDirection),
            pressureMsl = PressureUnit.MMHG.convert(current.pressureMmHg, PressureUnit.HPA),
            visibility = null,
            cloudCover = null,
            uvIndex = null,
            weatherCondition = GismeteoWeatherConditionMap.getCondition(current.icon),
            feelsLike = current.feelsLike,
            time = fixTimeString(current.time).iso8601TimestampToMilliseconds(),
            dewPoint = null,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis(),
        ),
        hourly = hourly.map {

            val snow = if (it.precipitationType == 2) it.precipitation else null
            val rain = if (it.precipitationType != 2) it.precipitation else null

            WeatherHourly(
                temperature = it.temperature,
                humidity = it.humidity ?: 0.0,
                windSpeed = WindSpeedUnit.MPS.convert(it.windSpeedMs, WindSpeedUnit.KPH),
                windDirection = convertWindDirection(it.windDirection),
                pressureMsl = PressureUnit.MMHG.convert(it.pressureMmHg, PressureUnit.HPA),
                visibility = null,
                uvIndex = null,
                weatherCondition = GismeteoWeatherConditionMap.getCondition(it.icon),
                time = fixTimeString(it.time).iso8601TimestampToMilliseconds(),
                dewPoint = null,
                rain = rain ?: 0.0,
                snowfall = snow,
                precipitationProbability = null,
            )
        },
        daily = daily.mapIndexed { index, day ->

            val dayHourly = hourlyForDay(
                hourly,
                fixDateString(day.time).iso8601TimestampToMilliseconds().normalizeToDay(zoneId)
            )

            val windSpeed = dayHourly.map { speed -> speed.windSpeedMs }.mapNotNull { it }.average()
                .takeUnless { it.isNaN() }

            val snow = if (day.precipitationType == 2) day.precipitation else null
            val rain = if (day.precipitationType != 2) day.precipitation else null

            val condition = computeDailyWeatherCondition(
                dayHourly.map { GismeteoWeatherConditionMap.getCondition(it.icon) },
                GismeteoWeatherConditionMap.getCondition(day.icon)
            )

            val humidityMin = dayHourly.map { it.humidity ?: -1.0 }.average().takeIf { it >= 0 }
            val pressureMin = dayHourly.map { it.pressureMmHg ?: -1.0 }.average().takeIf { it >= 0 }

            WeatherDaily(
                temperatureMin = day.temperatureMin,
                temperatureMax = day.temperatureMax,
                windSpeed = WindSpeedUnit.MPS.convert(windSpeed, WindSpeedUnit.KPH),
                windDirection = convertWindDirection(day.windDirection),
                rainSum = rain ?: 0.0,
                snowfallSum = snow,
                uvIndexMax = null,
                weatherCondition = condition,
                time = fixDateString(day.time).iso8601TimestampToMilliseconds()
                    .normalizeToDay(zoneId),
                precipitationProbabilityMax = null,
                pressureMsl = PressureUnit.MMHG.convert(pressureMin, PressureUnit.HPA),
                visibility = null,
                humidity = humidityMin,
                dewPoint = null,
                sunrise = sunTimings[index].sunrise ?: -0L,
                sunset = sunTimings[index].sunset ?: -0L,
                moonrise = moonTimings[index].moonrise ?: -0L,
                moonset = moonTimings[index].moonset ?: -0L,
                moonPhase = moonTimings[index].phase,
                dawn = sunTimings[index].dawn ?: 0L,
                dusk = sunTimings[index].dusk ?: 0L,
            )

        }
    )
}

private fun hourlyForDay(data: List<GismeteoModelHourly>, time: Long): List<GismeteoModelHourly> {
    val startIndex =
        data.indexOfFirst { fixTimeString(it.time).iso8601TimestampToMilliseconds() >= time }
            .minus(1)
            .takeIf { it != -1 }
            ?: 0

    val data = data.toList().drop(maxOf(0, startIndex))
        .take(24)

    return data
}