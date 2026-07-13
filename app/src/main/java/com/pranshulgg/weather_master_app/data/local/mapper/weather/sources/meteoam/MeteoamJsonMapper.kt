package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.meteoam

import android.util.Log
import androidx.core.text.isDigitsOnly
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.MeteoamWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.json.bundle.MeteoamWeatherBundle
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.calculations.computeApparentTemperature
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.roundToInt


private data class Daily(
    val temp: Double?,
    val windSpeed: Double?,
    val windDirection: Int?,
    val humidity: Double?,
    val icon: String?,
    val pop: Double?,
    val time: String,
    val pressure: Double?
)

fun MeteoamWeatherBundle.toDomain(location: Location): Weather {

    val current = this.current.datasets.current
    val forecast = this.forecast.datasets.forecast
    val forecastTimes = this.forecast.timeSeries
    val time = this.current.timeSeries.first().first()
    val windDirectionCurrent = current.windDirection.value.takeUnless { it == "VRB" }


    val temperature = forecast.temperature?.values?.toList()
    val windSpeed = forecast.windSpeedKmh?.values?.toList()
    val windDirection = forecast.windDirection?.values?.toList()

    val pressure = forecast.pressure?.values?.toList()
    val humidity = forecast.humidity?.values?.toList()
    val icon = forecast.icon.values.toList()
    val precipitationProbability = forecast.precipitationProbability?.values?.toList()

    val dailyWrapper: List<Daily> =
        icon.mapIndexed { index, string ->

            val direction = windDirection?.getOrNull(index).takeUnless { it == "VRB" }

            Daily(
                temp = temperature?.getOrNull(index),
                windSpeed = windSpeed?.getOrNull(index),
                windDirection = direction.toSafeDouble()?.toInt(),
                humidity = humidity?.getOrNull(index),
                icon = string,
                pop = precipitationProbability?.getOrNull(index),
                time = forecastTimes.getOrNull(index)!!,
                pressure = pressure?.getOrNull(index)
            )
        }


    val daily = computeDaily(dailyWrapper, location)

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = current.temperature.value,
            humidity = current.humidity.value ?: 0.0,
            windSpeed = current.windSpeedKmh.value,
            windDirection = WindDirection.toWindDirectionFromDegrees(
                windDirectionCurrent.toSafeDouble()?.toInt()
            ),
            pressureMsl = current.pressure.value,
            visibility = null,
            cloudCover = null,
            uvIndex = null,
            weatherCondition = MeteoamWeatherConditionMap.getCondition(current.icon.value),
            feelsLike = computeApparentTemperature(
                current.temperature.value, current.humidity.value,
                WindSpeedUnit.KPH.convert(current.windSpeedKmh.value, WindSpeedUnit.MPS)
            ),
            time = time.iso8601TimestampToMilliseconds(),
            dewPoint = null,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = forecastTimes.mapIndexed { index, time ->


            val direction = windDirection?.getOrNull(index).takeUnless { it == "VRB" }
            

            WeatherHourly(
                temperature = temperature?.getOrNull(index),
                windSpeed = windSpeed?.getOrNull(index),
                windDirection = WindDirection.toWindDirectionFromDegrees(
                    direction.toSafeDouble()?.toInt()
                ),
                pressureMsl = pressure?.getOrNull(index),
                humidity = humidity?.getOrNull(index),
                precipitationProbability = precipitationProbability
                    ?.getOrNull(index)
                    ?.roundToInt(),
                weatherCondition = MeteoamWeatherConditionMap.getCondition(icon.getOrNull(index)),
                rain = 0.0,
                snowfall = null,
                uvIndex = null,
                visibility = null,
                dewPoint = null,
                time = time.iso8601TimestampToMilliseconds()
            )
        }.filter { it.weatherCondition != WeatherCondition.NO_CONDITION_FOUND },
        daily = daily
    )

}

private fun computeDaily(
    data: List<Daily>,
    location: Location
): List<WeatherDaily> {


    val zoneId = location.timezone

    val groupedByDay = data.groupBy {
        it.time.iso8601TimestampToMilliseconds()
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


    return groupedByDay
        .map { dailyIt ->

            val index = keyIndices[dailyIt.key] ?: -1

            val minTemperature = dailyIt.value.minOf { it.temp ?: -1.0 }.takeIf { it >= 0.0 }
            val maxTemperature = dailyIt.value.maxOf { it.temp ?: -1.0 }.takeIf { it >= 0.0 }

            val windSpeed =
                dailyIt.value.map { it.windSpeed ?: -1.0 }.average().takeIf { it >= 0.0 }
            val windDirection =
                dailyIt.value.map { it.windDirection?.toDouble() ?: -1.0 }.average()
                    .takeIf { it >= 0.0 }

            val icon = dailyIt.value.map { it.icon }.groupingBy { it }
                .eachCount().entries.maxByOrNull { it.value }

            val humidityMin = dailyIt.value.minOf { it.humidity ?: -1.0 }.takeIf { it >= 0.0 }
            val pressureMin = dailyIt.value.minOf { it.pressure ?: -1.0 }.takeIf { it >= 0.0 }

            val weatherCondition = computeDailyWeatherCondition(
                getHourlyConditionsForDay(data, dailyIt.key),
                MeteoamWeatherConditionMap.getCondition(icon?.key)
            )


            WeatherDaily(
                temperatureMin = minTemperature,
                temperatureMax = maxTemperature,
                windSpeed = windSpeed,
                windDirection = WindDirection.toWindDirectionFromDegrees(windDirection?.toInt()),
                rainSum = 0.0,
                snowfallSum = null,
                uvIndexMax = null,
                weatherCondition = weatherCondition,
                time = dailyIt.key,
                precipitationProbabilityMax = null,
                pressureMsl = pressureMin,
                visibility = null,
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

        }.take(4)
}

private fun getHourlyConditionsForDay(
    data: List<Daily>,
    time: Long
): List<WeatherCondition> {
    val startIndex =
        data.indexOfFirst { it.time.iso8601TimestampToMilliseconds() >= time }
            .takeIf { it != -1 } ?: 0


    val conditions = data.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.METEO_AM.hourlyAggregationLimitHours)
        .map {
            MeteoamWeatherConditionMap.getCondition(it.icon)
        }

    return conditions
}

