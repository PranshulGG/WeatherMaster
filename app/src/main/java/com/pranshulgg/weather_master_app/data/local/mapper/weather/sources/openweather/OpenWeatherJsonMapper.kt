package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openweather

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.OpenWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.OpenWeatherForecastItemJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.OpenWeatherForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.bundle.OpenWeatherJsonBundle
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.data.local.mapper.utils.WeatherUtils
import com.pranshulgg.weather_master_app.data.local.mapper.utils.WeatherUtils.getDominantCondition
import com.pranshulgg.weather_master_app.data.local.mapper.utils.WeatherUtils.getDominantWindDirection
import com.pranshulgg.weather_master_app.data.local.mapper.utils.WeatherUtils.safeAverage
import com.pranshulgg.weather_master_app.data.local.mapper.utils.WeatherUtils.safeMax
import com.pranshulgg.weather_master_app.data.local.mapper.utils.WeatherUtils.safeMin
import com.pranshulgg.weather_master_app.data.local.mapper.utils.WeatherUtils.sumOrZero
import kotlin.math.roundToInt


fun OpenWeatherJsonBundle.toDomain(location: Location): Weather {

    val zoneId = location.timezone

    val groupedDays = forecast.list.groupBy {
        it.dt.secondsToMilliseconds().normalizeToDay(zoneId)
    }

    val sunTimings = getSunTimings(
        groupedDays.map {
            it.key
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        groupedDays.map {
            it.key
        },
        location.timezone,
        location.latitude,
        location.longitude
    )
    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = current.main.temp,
            humidity = current.main.humidity,
            windSpeed = current.wind.speedMs?.times(3.6),
            windDirection = WindDirection.toWindDirectionFromDegrees(current.wind.deg?.toInt()),
            pressureMsl = current.main.pressureSeaLevel,
            visibility = current.visibility?.toInt(),
            cloudCover = null,
            uvIndex = null,
            weatherCondition = OpenWeatherConditionMap.getCondition(current.weather[0].icon),
            feelsLike = current.main.feelsLike,
            time = current.dt.secondsToMilliseconds(),
            dewPoint = current.main.dewPoint,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = forecast.list.map {
            WeatherHourly(
                temperature = it.main.temp,
                windSpeed = it.wind.speedMs?.times(3.6),
                windDirection = WindDirection.toWindDirectionFromDegrees(it.wind.deg?.toInt()),
                rain = it.rain?.amountMm ?: 0.0,
                snowfall = it.snow?.amountMm,
                uvIndex = null,
                pressureMsl = it.main.pressureSeaLevel,
                visibility = it.visibility?.toInt(),
                humidity = it.main.humidity,
                dewPoint = it.main.dewPoint,
                weatherCondition = OpenWeatherConditionMap.getCondition(it.weather[0].icon),
                time = it.dt.secondsToMilliseconds(),
                precipitationProbability = it.pop?.times(100)?.roundToInt()
            )
        },
        daily = groupedDays.map { (key, dayHours) ->

            val index = groupedDays.keys.indexOf(key)

            val sunTimings = sunTimings[index]
            val moonTimings = moonTimings[index]

            val temperature = dayHours.map { it.main.temp }
            val windSpeedMs = dayHours.map { it.wind.speedMs }
            val windDirection =
                dayHours.map { WindDirection.toWindDirectionFromDegrees(it.wind.deg?.toInt()) }
            val humidity = dayHours.map { it.main.humidity }
            val dewPoint = dayHours.map { it.main.dewPoint }
            val pop = dayHours.map { it.pop }
            val rain = dayHours.map { it.rain?.amountMm }
            val snow = dayHours.map { it.snow?.amountMm }
            val visibility = dayHours.map { it.visibility }
            val pressure = dayHours.map { it.main.pressureSeaLevel }
            val condition =
                dayHours.map { OpenWeatherConditionMap.getCondition(it.weather[0].icon) }


            val minTemperature = temperature.safeMin()
            val maxTemperature = temperature.safeMax()

            val avgWindSpeed = windSpeedMs.safeAverage()

            val dominantWindDirection = windDirection.getDominantWindDirection()


            val rainSum = rain.sumOrZero()
            val snowSum = snow.sumOrZero()


            val dayCondition = computeDailyWeatherCondition(
                dayHours.map { OpenWeatherConditionMap.getCondition(it.weather[0].icon) },
                condition.getDominantCondition() ?: WeatherCondition.NO_CONDITION_FOUND
            )

            val maxPop = pop.safeMax()
            val avgPressure = pressure.safeAverage()
            val minVisibility = visibility.safeMin()
            val humidityAvg = humidity.safeAverage()
            val avgDewPoint = dewPoint.safeAverage()

            WeatherDaily(
                temperatureMin = minTemperature,
                temperatureMax = maxTemperature,
                windSpeed = avgWindSpeed?.times(3.6),
                windDirection = dominantWindDirection,
                rainSum = rainSum,
                snowfallSum = snowSum,
                uvIndexMax = null,
                weatherCondition = dayCondition,
                time = key,
                precipitationProbabilityMax = maxPop?.times(100)?.roundToInt(),
                pressureMsl = avgPressure,
                visibility = minVisibility?.toInt(),
                humidity = humidityAvg,
                dewPoint = avgDewPoint,
                sunrise = sunTimings.sunrise,
                sunset = sunTimings.sunset,
                moonrise = moonTimings.moonrise,
                moonset = moonTimings.moonset,
                moonPhase = moonTimings.phase,
                dawn = sunTimings.dawn,
                dusk = sunTimings.dusk,
            )
        }
    )

}

