package com.pranshulgg.weathermaster.data.local.mapper.weather.sources.nws

import com.pranshulgg.weathermaster.core.model.domain.location.Location
import com.pranshulgg.weathermaster.core.model.domain.weather.Weather
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weathermaster.core.model.domain.weather.nws.NwsGridPoints
import com.pranshulgg.weathermaster.core.model.weather.wind.WindDirection
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.NwsWeatherConditionMap
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsGridPointDataQuantitativePrecipitationValuesJson
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsGridPointDataSnowfallAmountValuesJson
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsGridPointsJson
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.bundle.NwsWeatherJsonBundle
import com.pranshulgg.weathermaster.core.utils.Extensions.fahrenheitToCelsius
import com.pranshulgg.weathermaster.core.utils.Extensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weathermaster.core.utils.Extensions.kmhToMs
import com.pranshulgg.weathermaster.core.utils.Extensions.mmToCm
import com.pranshulgg.weathermaster.core.utils.Extensions.mphToKmh
import com.pranshulgg.weathermaster.core.utils.Extensions.pressurePaToHpa
import com.pranshulgg.weathermaster.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weathermaster.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weathermaster.core.utils.weather.calculations.computeApparentTemperature
import com.pranshulgg.weathermaster.core.utils.weather.forecast.findHourlyIndexForTime
import java.net.UnknownHostException
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.abs


private data class NwsValidTime(
    val start: Instant,
    val duration: Duration
)

// ---------------------------- JSON TO DOMAIN ----------------------------

fun NwsGridPointsJson.toDomain(location: Location, stationIdentifier: String?): NwsGridPoints {
    return NwsGridPoints(
        locationId = location.id,
        officeId = this.points.officeId,
        gridX = this.points.gridX,
        gridY = this.points.gridY,
        stationIdentifier = stationIdentifier,
        lastUpdatedMilli = System.currentTimeMillis()
    )
}

fun NwsWeatherJsonBundle.toDomain(location: Location): Weather {

    val current = this.current.properties
    val hourly = this.hourly.properties
    val gridPointData = this.gridPointsData.properties
    val precipitationData = gridPointData.quantitativePrecipitation
    val maxTemperatureData = gridPointData.maxTemperature
    val minTemperatureData = gridPointData.minTemperature
    val snowData = gridPointData.snowfallAmount
    val daily = this.forecast.properties


    val rainMap = expandedHourly(precipitationData.values.map { it.validTime to it.value })

    val snowMap = expandedHourly(snowData.values.map { it.validTime to it.value })
    val timezone = location.timezone
    val maxTemperatureMap =
        matchingMinMaxTemperature(maxTemperatureData.values.map { it.validTime to it.value })
    val minTemperatureMap =
        matchingMinMaxTemperature(minTemperatureData.values.map { it.validTime to it.value })


    val currentHourIndex =
        findHourlyIndexForTime(hourly.periods.map { it.startTime.iso8601TimestampToMilliseconds() })

    val currentIcon = if (!current.icon.isNullOrBlank()) current.icon
    else hourly.periods.getOrNull(currentHourIndex)?.icon

    val currentTemperature =
        current.temperature.value ?: hourly.periods.getOrNull(currentHourIndex)?.temperature
        ?: throw Exception(
            UnknownHostException()
        )

    val currentWindSpeed = current.windSpeed.value ?: throw Exception(UnknownHostException())

    val sunTimings = getSunTimings(
        daily.periods.map {
            it.startTime.iso8601TimestampToMilliseconds()
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        daily.periods.map {
            it.startTime.iso8601TimestampToMilliseconds()
        },
        location.timezone,
        location.latitude,
        location.longitude
    )
    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = currentTemperature,
            humidity = current.relativeHumidity.value,
            windSpeed = current.windSpeed.value,
            windDirection = WindDirection.toWindDirectionFromString(hourly.periods[currentHourIndex].windDirection),
            pressureMsl = current.seaLevelPressure.value?.pressurePaToHpa(),
            visibility = current.visibility.value?.toInt(),
            cloudCover = null, // NOT USED IN THE APP
            uvIndex = null,
            weatherCondition = NwsWeatherConditionMap.getCondition(currentIcon),
            feelsLike = computeApparentTemperature(
                currentTemperature,
                current.relativeHumidity.value,
                currentWindSpeed.kmhToMs()
            ),
            time = current.timestamp.iso8601TimestampToMilliseconds(),
            dewPoint = hourly.periods[currentHourIndex].dewPoint.value,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = hourly.periods.map {

            val hourTime = it.startTime.iso8601TimestampToMilliseconds()
            val rainAmount = rainMap[hourTime]
            val snowFall = snowMap[hourTime]

            WeatherHourly(
                temperature = it.temperature.fahrenheitToCelsius(),
                windSpeed = fixHourlyNwsWindSpeedValue(it.windSpeed),
                windDirection = WindDirection.toWindDirectionFromString(it.windDirection),
                rain = rainAmount ?: 0.0,
                snowfall = snowFall ?: 0.0,
                uvIndex = null,
                weatherCondition = NwsWeatherConditionMap.getCondition(it.icon),
                time = hourTime,
                precipitationProbability = it.probabilityOfPrecipitation.value.toInt()
            )
        },
        daily = List(daily.periods.size) {

            val item = daily.periods[it]

            val time = item.startTime.iso8601TimestampToMilliseconds()

            // Time doesn't align so we get the closest difference
            val maxTemperature = maxTemperatureMap.entries.minByOrNull { (key, _) ->
                abs(key - time)

            }

            val minTemperature = minTemperatureMap.entries.minByOrNull { (key, _) ->
                abs(key - time)
            }
            val rainSum = getRainSum(precipitationData.values, time)
            val snowfallSum = getSnowfallSum(snowData.values, time)

            val hourIndexForDaily =
                findHourlyIndexForTime(
                    hourly.periods.map { periodTime -> periodTime.startTime.iso8601TimestampToMilliseconds() },
                    time
                )

            val dailyIcon = if (!item.icon.isNullOrBlank()) item.icon
            else hourly.periods.getOrNull(hourIndexForDaily)?.icon
            val windSpeed = fixDailyNwsWindSpeedValue(item.windSpeed)

            WeatherDaily(
                temperatureMin = minTemperature?.value ?: 0.0,
                temperatureMax = maxTemperature?.value ?: 0.0,
                windSpeed = windSpeed,
                windDirection = WindDirection.toWindDirectionFromString(item.windDirection),
                rainSum = rainSum,
                snowfallSum = snowfallSum.mmToCm(),
                uvIndexMax = null,
                weatherCondition = NwsWeatherConditionMap.getCondition(dailyIcon),
                time = time,
                precipitationProbabilityMax = item.probabilityOfPrecipitation.value.toInt(),
                sunrise = sunTimings[it].sunrise ?: -0L,
                sunset = sunTimings[it].sunset ?: -0L,
                moonrise = moonTimings[it].moonrise ?: -0L,
                moonset = moonTimings[it].moonset ?: -0L,
                moonPhase = moonTimings[it].phase
            )
        }
    )

}

private fun fixHourlyNwsWindSpeedValue(value: String): Double {
    val filteredValue = value.filter { it.isDigit() } // 12 mph to 12

    return filteredValue.toDouble().mphToKmh()
}


/**
 * NWS returns wind speed for daily forecast like "10 to 20 mph"
 * So we just strip it down and get the avg
 */
private fun fixDailyNwsWindSpeedValue(value: String): Double? {
    val value = Regex("""\d+""").findAll(value).map { it.value.toDouble() }.toList()

    if (value.isEmpty()) return null
    return value.average()
}

private fun parseValidTime(validTime: String): NwsValidTime {
    val parts = validTime.trim().split("/")

    val start = java.time.OffsetDateTime
        .parse(parts[0].trim())
        .toInstant()

    val duration = java.time.Duration.parse(parts[1].trim())

    return NwsValidTime(
        start = start,
        duration = duration
    )
}

/**
 * NWS gives valid time and value (for e.g. windSpeed: value 22, valid time 3H with start time)
 * Meaning the value will stay the same for next 3 hours from starting time
 * We need to expand the hours so it matches with the hourly forecast (and sacrificing a goat)
 */
private fun <T> expandedHourly(values: List<Pair<String, T>>): Map<Long, T> {

    val expanded = mutableMapOf<Long, T>()

    // Loop through each item
    values.forEach { (validTime, value) ->

        /**
         * Parse the time into duration and start times
         * For e.g. 2026-05-19T00:00:00+00:00/PT2H -> NwsValidTime(2026-05-19T00:00:00Z, PT2H)
         */
        val parse = parseValidTime(validTime)

        /**
         * Convert duration to hours
         * For e.g. PT2H -> 2
         */
        val hours = parse.duration.toHours()

        /**
         * Create one timestamp per hour by adding
         * the current offset to the start time
         */
        repeat(hours.toInt()) { offset ->
            val instant = parse.start.plus(
                offset.toLong(),
                ChronoUnit.HOURS
            )

            expanded[instant.toEpochMilli()] = value
        }

    }

    return expanded

}


private fun <T> matchingMinMaxTemperature(values: List<Pair<String, T>>): Map<Long, T> {
    val daily = mutableMapOf<Long, T>()

    values.forEach { (validTime, value) ->
        val milli = validTime.iso8601TimestampToMilliseconds()

        daily[milli] = value
    }

    return daily
}


private fun getRainSum(
    data: List<NwsGridPointDataQuantitativePrecipitationValuesJson>,
    time: Long
): Double {

    val startIndex = data.indexOfFirst { it.validTime.iso8601TimestampToMilliseconds() >= time }
        .takeIf { it != -1 } ?: 0
    val data = data.drop(maxOf(0, startIndex - 1)).take(12)

    val rainSum = data.sumOf { it.value ?: 0.0 }

    return rainSum
}

private fun getSnowfallSum(
    data: List<NwsGridPointDataSnowfallAmountValuesJson>,
    time: Long
): Double {

    val startIndex = data.indexOfFirst { it.validTime.iso8601TimestampToMilliseconds() >= time }
        .takeIf { it != -1 } ?: 0
    val data = data.drop(maxOf(0, startIndex - 1)).take(12)

    val snowfallSum = data.sumOf { it.value ?: 0.0 }

    return snowfallSum
}