package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.metnorway

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.json.MetNorwayForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.json.MetNorwayForecastTimeSeriesJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.calculations.computeApparentTemperature
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime
import kotlin.math.roundToInt

// ---------------------------- JSON TO DOMAIN ----------------------------

fun MetNorwayForecastJson.toDomain(location: Location): Weather {

    val currentHour = findHourlyIndexForTime(
        this.properties.data.map { it.time.iso8601TimestampToMilliseconds() }
    )

    val current = this.properties.data[currentHour].data
    val currentTime = this.properties.data[currentHour].time.iso8601TimestampToMilliseconds()
    val daily = computeDaily(this, location)


    val nextHourSummary = this.properties.data.map {
        it.data.nextHour?.summary
            ?: it.data.next6Hours?.summary
            ?: it.data.next12Hours?.summary
    }

    return Weather(
        current = WeatherCurrent(
            temperature = current.instant.details.temperature,
            humidity = current.instant.details.relativeHumidity,
            windSpeed = current.instant.details.windSpeed,
            windDirection = WindDirection.toWindDirectionFromDegrees(current.instant.details.windDirection.roundToInt()),
            pressureMsl = current.instant.details.pressureMsl,
            visibility = null,
            cloudCover = null, // NOT USED IN THE APP
            uvIndex = current.instant.details.uvIndex,
            weatherCondition = MetNorwayWeatherConditionMap.getCondition(current.nextHour?.summary?.symbolCode),
            feelsLike = computeApparentTemperature(
                current.instant.details.temperature,
                current.instant.details.relativeHumidity,
                current.instant.details.windSpeed
            ),
            time = currentTime,
            dewPoint = current.instant.details.dewPoint,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        location = location,
        hourly = this.properties.data.map { item ->

            val data = item.data.instant.details

            val nextHourDetails =
                item.data.nextHour?.details
                    ?: item.data.next6Hours?.details
                    ?: item.data.next12Hours?.details

            val icon =
                item.data.nextHour?.summary?.symbolCode
                    ?: item.data.next6Hours?.summary?.symbolCode
                    ?: item.data.next12Hours?.summary?.symbolCode

            WeatherHourly(
                temperature = data.temperature,
                windSpeed = data.windSpeed,
                windDirection = WindDirection.toWindDirectionFromDegrees(data.windDirection.roundToInt()),
                rain = nextHourDetails?.precipitationAmount ?: 0.0,
                snowfall = null,
                uvIndex = data.uvIndex,
                weatherCondition = MetNorwayWeatherConditionMap.getCondition(icon),
                time = item.time.iso8601TimestampToMilliseconds(),
                precipitationProbability = null
            )
        },
        daily = daily
    )
}

private fun computeDaily(data: MetNorwayForecastJson, location: Location): List<WeatherDaily> {

    val daily = data.properties.data

    val zoneId = location.timezone

    val groupedByDay = daily.groupBy {
        it.time.iso8601TimestampToMilliseconds()
            .normalizeToDay(zoneId)
    }.filter { (_, values) ->
        values.size >= 12
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


    return groupedByDay.map { dailyIt ->


        val nextHourDetails = dailyIt.value.map {
            it.data.nextHour?.details
                ?: it.data.next6Hours?.details
                ?: it.data.next12Hours?.details
        }

        val nextHourSummary = dailyIt.value.map {
            it.data.nextHour?.summary
                ?: it.data.next6Hours?.summary
                ?: it.data.next12Hours?.summary
        }


        val minTemperature = dailyIt.value.minOf { it.data.instant.details.temperature }
        val maxTemperature = dailyIt.value.maxOf { it.data.instant.details.temperature }
        val windSpeed = dailyIt.value.map { it.data.instant.details.windSpeed }.average()
        val windDirection =
            dailyIt.value.map { it.data.instant.details.windDirection }.average().roundToInt()
        val rainSum =
            nextHourDetails.sumOf { it?.precipitationAmount ?: 0.0 } ?: 0.0
        val uvIndexMax = dailyIt.value.maxOf { it.data.instant.details.uvIndex }
        val time = dailyIt.key
        val icon = nextHourSummary.map { it?.symbolCode }.groupingBy { it }
            .eachCount().entries.maxByOrNull { it.value }

        val condition = computeDailyWeatherCondition(
            getHourlyConditionsForDay(dailyIt.value, time),
            MetNorwayWeatherConditionMap.getCondition(icon?.key)
        )


        val index = groupedByDay.keys.indexOf(dailyIt.key)

        WeatherDaily(
            temperatureMin = minTemperature,
            temperatureMax = maxTemperature,
            windSpeed = windSpeed,
            windDirection = WindDirection.toWindDirectionFromDegrees(windDirection),
            rainSum = rainSum,
            snowfallSum = null,
            uvIndexMax = uvIndexMax,
            weatherCondition = condition,
            time = time,
            precipitationProbabilityMax = null,
            sunrise = sunTimings[index].sunrise ?: -0L,
            sunset = sunTimings[index].sunset ?: -0L,
            moonrise = moonTimings[index].moonrise ?: -0L,
            moonset = moonTimings[index].moonset ?: -0L,
            moonPhase = moonTimings[index].phase
        )
    }

}

private fun getHourlyConditionsForDay(
    data: List<MetNorwayForecastTimeSeriesJson>,
    time: Long
): List<WeatherCondition> {
    val startIndex =
        data.indexOfFirst { it.time.iso8601TimestampToMilliseconds() >= time }
            .takeIf { it != -1 } ?: 0


    val conditions = data.drop(maxOf(0, startIndex - 1)).take(12)
        .map {
            MetNorwayWeatherConditionMap.getCondition(
                it.data.nextHour?.summary?.symbolCode
                    ?: it.data.next6Hours?.summary?.symbolCode
                    ?: it.data.next12Hours?.summary?.symbolCode
            )
        }

    return conditions
}

