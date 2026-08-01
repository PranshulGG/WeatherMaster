package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.ipma

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.IpmaWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.json.IpmaForecastJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.calculations.computeApparentTemperature
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime
import java.time.ZonedDateTime


fun List<IpmaForecastJson>.toDomain(location: Location): Weather {

    val nowMillis = ZonedDateTime.now(safeZoneId(location.timezone))
        .toInstant()
        .toEpochMilli()

    val correctDateStr: (String) -> String = {
        val dateStr = if (it.endsWith("Z")) it else it + "Z"
        dateStr
    }

    val currentIndex =
        findHourlyIndexForTime(this.map {
            correctDateStr(it.date).iso8601TimestampToMilliseconds()
        }, nowMillis)

    val current = this[currentIndex]

    // Only daily data has min and max temps
    val daily = this.filter { it.maxTemperature != null && it.minTemperature != null }


    val sunTimings = getSunTimings(
        daily.map {
            correctDateStr(it.date).iso8601TimestampToMilliseconds()
                .normalizeToDay(location.timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        daily.map {
            correctDateStr(it.date).iso8601TimestampToMilliseconds()
                .normalizeToDay(location.timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = current.temperature.toSafeDouble(),
            humidity = current.humidity.toSafeDouble() ?: 0.0,
            windSpeed = current.windSpeed.toSafeDouble(),
            windDirection = WindDirection.toWindDirectionFromString(current.windDirection),
            pressureMsl = null,
            visibility = null,
            cloudCover = null,
            uvIndex = null,
            weatherCondition = IpmaWeatherConditionMap.getCondition(current.weatherCode),
            feelsLike = computeApparentTemperature(
                current.temperature.toSafeDouble(),
                current.humidity.toSafeDouble(),
                WindSpeedUnit.KPH.convert(current.windSpeed.toSafeDouble(), WindSpeedUnit.MPS)
            ),
            time = nowMillis,
            dewPoint = null,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = this.filter { it.temperature != null }.map { hour ->
            WeatherHourly(
                temperature = hour.temperature.toSafeDouble(),
                windSpeed = hour.windSpeed.toSafeDouble(),
                windDirection = WindDirection.toWindDirectionFromString(hour.windDirection),
                rain = 0.0,
                snowfall = null,
                uvIndex = null,
                pressureMsl = null,
                visibility = null,
                humidity = hour.humidity.toSafeDouble(),
                dewPoint = null,
                weatherCondition = IpmaWeatherConditionMap.getCondition(hour.weatherCode),
                time = correctDateStr(hour.date).iso8601TimestampToMilliseconds(),
                precipitationProbability = if (hour.precipitationProbability == "-99.0") null else hour.precipitationProbability.toSafeDouble()
                    ?.toInt(),
            )
        },
        daily = daily.mapIndexed { index, it ->
            WeatherDaily(
                temperatureMin = it.minTemperature.toSafeDouble(),
                temperatureMax = it.maxTemperature.toSafeDouble(),
                windSpeed = it.windSpeed.toSafeDouble(),
                windDirection = WindDirection.toWindDirectionFromString(it.windDirection),
                rainSum = 0.0,
                snowfallSum = null,
                uvIndexMax = it.uvIndex.toSafeDouble(),
                weatherCondition = IpmaWeatherConditionMap.getCondition(it.weatherCode),
                time = correctDateStr(it.date).iso8601TimestampToMilliseconds()
                    .normalizeToDay(location.timezone),
                precipitationProbabilityMax = it.precipitationProbability.toSafeDouble()?.toInt(),
                pressureMsl = null,
                visibility = null,
                humidity = it.humidity.toSafeDouble(),
                dewPoint = null,
                sunrise = sunTimings[index].sunrise ?: -1L,
                sunset = sunTimings[index].sunset ?: -1L,
                moonrise = moonTimings[index].moonrise ?: -1L,
                moonset = moonTimings[index].moonset ?: -1L,
                moonPhase = moonTimings[index].phase,
                dawn = sunTimings[index].dawn ?: -1L,
                dusk = sunTimings[index].dusk ?: -1L,
            )
        }
    )

}

