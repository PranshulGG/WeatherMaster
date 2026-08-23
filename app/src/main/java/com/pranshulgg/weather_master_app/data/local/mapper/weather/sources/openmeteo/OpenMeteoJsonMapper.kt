package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openmeteo

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.json.OpenMeteoHourlyForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.json.OpenMeteoWeatherJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime
import kotlin.collections.drop
import kotlin.collections.indexOfFirst
import kotlin.collections.map
import kotlin.uuid.ExperimentalUuidApi

// ---------------------------- JSON TO DOMAIN ----------------------------

@OptIn(ExperimentalUuidApi::class)
fun OpenMeteoWeatherJson.toDomain(location: Location): Weather {


    val sunTimings = getSunTimings(
        daily.time.map {
            it.secondsToMilliseconds()
        }, // Open-Meteo returns in seconds
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        daily.time.map {
            it.secondsToMilliseconds()
        }, // Open-Meteo returns in seconds
        location.timezone,
        location.latitude,
        location.longitude
    )

    val currentHourIndex =
        findHourlyIndexForTime(hourly.time.map { it.secondsToMilliseconds() })

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = current.temperature,
            humidity = current.relativeHumidity,
            windSpeed = current.windSpeed,
            windDirection = WindDirection.toWindDirectionFromDegrees(current.windDirection),
            pressureMsl = current.pressureMsl,
            visibility = hourly.visibility[currentHourIndex],
            cloudCover = current.cloudCover,
            uvIndex = current.uvIndex,
            weatherCondition = OpenMeteoWeatherConditionMap.getCondition(current.weatherCode),
            feelsLike = current.feelsLike,
            time = current.time.secondsToMilliseconds(), // Open-Meteo returns in seconds
            dewPoint = hourly.dewPoint[currentHourIndex],
            utcOffsetSeconds = utcOffsetSeconds,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = List(hourly.time.size) {
            WeatherHourly(
                temperature = hourly.temperature[it],
                windSpeed = hourly.windSpeed[it],
                windDirection = WindDirection.toWindDirectionFromDegrees(hourly.windDirection[it]),
                rain = hourly.rain[it] ?: 0.0,
                snowfall = hourly.snowfall[it],
                uvIndex = hourly.uvIndex[it],
                weatherCondition = OpenMeteoWeatherConditionMap.getCondition(hourly.weatherCode[it]),
                time = hourly.time[it].secondsToMilliseconds(), // Open-Meteo returns in seconds
                precipitationProbability = hourly.precipitationProbability[it],
                humidity = hourly.relativeHumidity[it],
                visibility = hourly.visibility[it],
                pressureMsl = hourly.pressureMsl[it],
                dewPoint = hourly.dewPoint[it]
            )
        },
        daily = List(daily.time.size) {

            val condition = computeDailyWeatherCondition(
                getHourlyConditionsForDay(hourly, daily.time[it]),
                OpenMeteoWeatherConditionMap.getCondition(daily.weatherCode[it])
            )


            WeatherDaily(
                temperatureMin = daily.temperatureMin[it],
                temperatureMax = daily.temperatureMax[it],
                windSpeed = daily.windSpeedMean[it],
                windDirection = WindDirection.toWindDirectionFromDegrees(daily.windDirectionDominant[it]),
                rainSum = daily.rainSum[it] ?: 0.0,
                snowfallSum = daily.snowfallSum[it],
                uvIndexMax = daily.uvIndexMax[it],
                weatherCondition = condition,
                time = daily.time[it].secondsToMilliseconds(), // Open-Meteo returns in seconds
                precipitationProbabilityMax = daily.precipitationProbabilityMax[it],
                sunrise = sunTimings[it].sunrise,
                sunset = sunTimings[it].sunset,
                moonrise = moonTimings[it].moonrise,
                moonset = moonTimings[it].moonset,
                moonPhase = moonTimings[it].phase,
                dawn = sunTimings[it].dawn,
                dusk = sunTimings[it].dusk,
                pressureMsl = daily.pressureMsl[it],
                visibility = daily.visibility[it],
                humidity = daily.humidity[it]?.toDouble(),
                dewPoint = daily.dewPoint[it]
            )
        }
    )
}


private fun getHourlyConditionsForDay(
    data: OpenMeteoHourlyForecastJson,
    time: Long
): List<WeatherCondition> {

    val startIndex =
        data.time.indexOfFirst { it.secondsToMilliseconds() >= time.secondsToMilliseconds() }
            .takeIf { it != -1 } ?: 0

    val conditions = data.weatherCode.drop(maxOf(0, startIndex)).take(24)
        .map { OpenMeteoWeatherConditionMap.getCondition(it) }


    return conditions


}