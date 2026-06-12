package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.fmi

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.astro.MoonPhase
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.model.FmiWeather
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.xml.FmiWeatherForecastXml
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime


fun FmiWeather.toDomain(location: Location): Weather {

    val forecast = this.data.groupBy { it.parameterName }
    Log.d("ALLGROUP", forecast.keys.toString())


    val currentHour = findHourlyIndexForTime(
        time = forecast["Temperature"]!!.map { it.time!!.iso8601TimestampToMilliseconds() }
    )

    val temperature = forecast["Temperature"]?.get(currentHour)


    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = temperature?.parameterValue?.toDouble(),
            humidity = 22.0,
            windSpeed = 22.0,
            windDirection = WindDirection.N,
            pressureMsl = 22.0,
            visibility = 22,
            cloudCover = null,
            uvIndex = 22.0,
            weatherCondition = WeatherCondition.NO_CONDITION_FOUND,
            feelsLike = 22.0,
            time = System.currentTimeMillis(),
            dewPoint = 22.3,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = listOf(
            WeatherHourly(
                temperature = 22.0,
                humidity = 22.0,
                windSpeed = 22.0,
                windDirection = WindDirection.N,
                pressureMsl = 22.0,
                visibility = 22,
                uvIndex = 22.0,
                weatherCondition = WeatherCondition.NO_CONDITION_FOUND,
                time = System.currentTimeMillis(),
                dewPoint = 22.3,
                rain = 0.0,
                snowfall = 0.0,
                precipitationProbability = 0,
            )
        ),
        daily = listOf(
            WeatherDaily(
                windSpeed = 22.0,
                windDirection = WindDirection.N,
                weatherCondition = WeatherCondition.NO_CONDITION_FOUND,
                time = System.currentTimeMillis(),
                temperatureMin = 0.0,
                temperatureMax = 0.0,
                rainSum = 0.0,
                snowfallSum = 0.0,
                uvIndexMax = 0.0,
                precipitationProbabilityMax = 0,
                sunrise = -0,
                sunset = -0,
                moonrise = -0,
                moonset = -0,
                moonPhase = MoonPhase.WANING_CRESCENT,
                dawn = -0,
                dusk = -0,
            )
        ),
    )
}