package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.bmkg

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.BmkgWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.json.BmkgForecastWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.json.bundle.BmkgForecastBundle
import com.pranshulgg.weather_master_app.core.network.sources.weather.china.ChinaWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.DwdWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.json.DwdWeatherForecastDataJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.json.SmhiForecastJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.calculations.computeApparentTemperature
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import kotlin.math.roundToInt


fun BmkgForecastBundle.toDomain(location: Location): Weather {

    val current = this.current.data.weather
    val forecast = this.forecast.data.flatMap { it.weather }.flatten()

    val daily = computeDaily(forecast, location)

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = current.temperature,
            humidity = current.humidity ?: 0.0,
            windSpeed = current.windSpeed,
            windDirection = WindDirection.toWindDirectionFromDegrees(current.windDirection?.toInt()),
            pressureMsl = null,
            visibility = current.visibility?.roundToInt(),
            cloudCover = null, // NOT USED IN THE APP
            uvIndex = null,
            weatherCondition = BmkgWeatherConditionMap.getCondition(current.weather),
            feelsLike = computeApparentTemperature(
                current.temperature,
                current.humidity,
                WindSpeedUnit.KPH.convert(current.windSpeed, WindSpeedUnit.MPS)
            ),
            time = current.datetime.iso8601TimestampToMilliseconds(),
            dewPoint = null,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = forecast.map {
            WeatherHourly(
                temperature = it.t,
                windSpeed = it.ws,
                windDirection = WindDirection.toWindDirectionFromDegrees(it.windDegree?.toInt()),
                rain = it.tp ?: 0.0,
                snowfall = null,
                uvIndex = null,
                pressureMsl = null,
                visibility = it.vs?.roundToInt(),
                humidity = it.hu,
                dewPoint = null,
                weatherCondition = BmkgWeatherConditionMap.getCondition(it.weather),
                time = it.datetime.iso8601TimestampToMilliseconds(),
                precipitationProbability = null
            )
        },
        daily = daily,
    )

}

private fun computeDaily(
    data: List<BmkgForecastWeatherJson>,
    location: Location
): List<WeatherDaily> {


    val zoneId = location.timezone

    val groupedByDay = data.groupBy {
        it.datetime.iso8601TimestampToMilliseconds()
            .normalizeToDay(zoneId)
    }

    val sunTimings = getSunTimings(
        groupedByDay.map {
            it.key
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        groupedByDay.map {
            it.key
        },
        location.timezone,
        location.latitude,
        location.longitude
    )
    val keyIndices = groupedByDay.keys.withIndex().associate { it.value to it.index }


    return groupedByDay.map { dailyIt ->

        val index = keyIndices[dailyIt.key] ?: -1

        val minTemperature = dailyIt.value.minOf { it.t ?: -1.0 }.takeIf { it >= 0.0 }
        val maxTemperature = dailyIt.value.maxOf { it.t ?: -1.0 }.takeIf { it >= 0.0 }

        val windSpeed = dailyIt.value.map { it.ws ?: -1.0 }.average().takeIf { it >= 0.0 }
        val windDirection =
            dailyIt.value.map { it.windDegree ?: -1.0 }.average().takeIf { it >= 0.0 }
        val rainSum = dailyIt.value.maxOf { it.tp ?: -1.0 }.takeIf { it >= 0.0 }

        val weatherConditionAverage = dailyIt.value.map { it.weather.toDouble() }.average()

        val weatherCondition = computeDailyWeatherCondition(
            getHourlyConditionsForDay(data, dailyIt.key),
            BmkgWeatherConditionMap.getCondition(weatherConditionAverage.toInt())
        )

        val visibilityMin = dailyIt.value.minOf { it.vs ?: -1.0 }.takeIf { it >= 0.0 }
        val humidityMin = dailyIt.value.minOf { it.hu ?: -1.0 }.takeIf { it >= 0.0 }


        WeatherDaily(
            temperatureMin = minTemperature,
            temperatureMax = maxTemperature,
            windSpeed = windSpeed,
            windDirection = WindDirection.toWindDirectionFromDegrees(windDirection?.toInt()),
            rainSum = rainSum ?: 0.0,
            snowfallSum = null,
            uvIndexMax = null,
            weatherCondition = weatherCondition,
            time = dailyIt.key,
            precipitationProbabilityMax = null,
            pressureMsl = null,
            visibility = visibilityMin?.toInt(),
            humidity = humidityMin,
            dewPoint = null,
            sunrise = sunTimings[index].sunrise ?: -1L,
            sunset = sunTimings[index].sunset ?: -1L,
            moonrise = moonTimings[index].moonrise ?: -1L,
            moonset = moonTimings[index].moonset ?: -1L,
            moonPhase = moonTimings[index].phase,
            dawn = sunTimings[index].dawn ?: -1L,
            dusk = sunTimings[index].dusk ?: -1L
        )

    }
}

private fun getHourlyConditionsForDay(
    data: List<BmkgForecastWeatherJson>,
    time: Long
): List<WeatherCondition> {
    val startIndex =
        data.indexOfFirst { it.datetime.iso8601TimestampToMilliseconds() >= time }
            .takeIf { it != -1 } ?: 0


    val conditions = data.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.BMKG.hourlyAggregationLimitHours)
        .map {
            ChinaWeatherConditionMap.getCondition(it.weather)
        }

    return conditions
}
