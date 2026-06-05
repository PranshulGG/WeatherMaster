package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.nws

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.nws.NwsGridPoints
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsGridPointDataQuantitativePrecipitationValuesJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsGridPointDataSnowfallAmountValuesJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsGridPointsJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsHourlyForecastPeriodsJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.bundle.NwsWeatherJsonBundle
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.calculations.computeApparentTemperature
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.roundToInt


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
    val visibilityData = gridPointData.visibility
    val daily = this.forecast.properties
    val zoneId = location.timezone

    val rainMap = expandedHourly(precipitationData.values.map { it.validTime to it.value })

    val snowMap = expandedHourly(snowData.values.map { it.validTime to it.value })
    val visibilityMap = expandedHourly(visibilityData.values.map { it.validTime to it.value })


    val maxTemperatureMap =
        matchingMinMaxTemperature(
            maxTemperatureData.values.map { it.validTime to it.value },
            location.timezone
        )
    val minTemperatureMap =
        matchingMinMaxTemperature(
            minTemperatureData.values.map { it.validTime to it.value },
            location.timezone
        )


    val currentHourIndex =
        findHourlyIndexForTime(hourly.periods.map { it.startTime.iso8601TimestampToMilliseconds() })

    val currentIcon = if (!current.icon.isNullOrBlank()) current.icon
    else hourly.periods.getOrNull(currentHourIndex)?.icon

    val currentTemperature =
        current.temperature.value ?: hourly.periods.getOrNull(currentHourIndex)?.temperature


    val sunTimings = getSunTimings(
        daily.periods.map {
            it.startTime.iso8601TimestampToMilliseconds().normalizeToDay(zoneId)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        daily.periods.map {
            it.startTime.iso8601TimestampToMilliseconds().normalizeToDay(zoneId)
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
                current.windSpeed.value?.kmhToMs()
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
            val visibility = visibilityMap[hourTime]

            WeatherHourly(
                temperature = TemperatureUnit.FAHRENHEIT.convert(
                    it.temperature,
                    TemperatureUnit.CELSIUS
                ),
                windSpeed = fixHourlyNwsWindSpeedValue(it.windSpeed),
                windDirection = WindDirection.toWindDirectionFromString(it.windDirection),
                rain = rainAmount ?: 0.0,
                snowfall = snowFall ?: 0.0,
                uvIndex = null,
                weatherCondition = NwsWeatherConditionMap.getCondition(it.icon),
                time = hourTime,
                precipitationProbability = it.probabilityOfPrecipitation.value.toInt(),
                humidity = it.relativeHumidity.value,
                pressureMsl = null,
                visibility = visibility?.roundToInt(),
                dewPoint = it.dewPoint.value
            )
        },
        daily = daily.periods.filter { it.isDayTime }.map { item ->


            val index = daily.periods.indexOf(item)


            val time =
                item.startTime.iso8601TimestampToMilliseconds().normalizeToDay(zoneId)

            // Time doesn't align so we get the closest difference
            val maxTemperature = maxTemperatureMap.entries.minByOrNull { (key, _) ->
                abs(key - time)

            }

            val minTemperature = minTemperatureMap.entries.minByOrNull { (key, _) ->
                abs(key - time)
            }
            val rainSum = getRainSum(rainMap, time)
            val snowfallSum = getSnowfallSum(snowMap, time)


            val windSpeed = getMaxWindSpeed(hourly, time)


            val condition = computeDailyWeatherCondition(
                getHourlyConditionsForDay(hourly, time),
                NwsWeatherConditionMap.getCondition(item.icon)
            )

            WeatherDaily(
                temperatureMin = minTemperature?.value ?: 0.0,
                temperatureMax = maxTemperature?.value ?: 0.0,
                windSpeed = windSpeed,
                windDirection = WindDirection.toWindDirectionFromString(item.windDirection),
                rainSum = rainSum,
                snowfallSum = PrecipitationUnit.MM.convert(snowfallSum, PrecipitationUnit.CM),
                uvIndexMax = null,
                weatherCondition = condition,
                time = time,
                precipitationProbabilityMax = item.probabilityOfPrecipitation.value.toInt(),
                sunrise = sunTimings[index].sunrise ?: -0L,
                sunset = sunTimings[index].sunset ?: -0L,
                moonrise = moonTimings[index].moonrise ?: -0L,
                moonset = moonTimings[index].moonset ?: -0L,
                moonPhase = moonTimings[index].phase,
                dawn = sunTimings[index].dawn ?: 0L,
                dusk = sunTimings[index].dusk ?: 0L
            )
        }
    )

}

private fun fixHourlyNwsWindSpeedValue(value: String): Double? {
    val filteredValue = value.filter { it.isDigit() } // 12 mph to 12

    return WindSpeedUnit.MPH.convert(filteredValue.toDouble(), WindSpeedUnit.KPH)
}


private fun getMaxWindSpeed(data: NwsHourlyForecastPeriodsJson, time: Long): Double {
    val startIndex =
        data.periods.indexOfFirst { it.startTime.iso8601TimestampToMilliseconds() >= time }
            .takeIf { it != -1 } ?: 0

    val maxSpeed = data.periods.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.NWS.hourlyAggregationLimitHours)
        .map { fixHourlyNwsWindSpeedValue(it.windSpeed) }

    return maxSpeed.maxOf { it ?: 0.0 }
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

        val parse = parseValidTime(validTime)

        val hours = parse.duration.toHours()

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


private fun <T> matchingMinMaxTemperature(
    values: List<Pair<String, T>>,
    zoneId: String
): Map<Long, T> {
    val daily = mutableMapOf<Long, T>()

    values.forEach { (validTime, value) ->
        val milli = validTime.iso8601TimestampToMilliseconds().normalizeToDay(zoneId)

        daily[milli] = value
    }

    return daily
}


private fun getRainSum(
    data: Map<Long, Double>,
    time: Long
): Double {

    val startIndex = data.toList().indexOfFirst { it.first >= time }.takeIf { it != -1 } ?: 0

    val data =
        data.toList().drop(maxOf(0, startIndex)).take(WeatherSource.NWS.hourlyAggregationLimitHours)


    val rainSum = data.sumOf { it.second ?: 0.0 }

    return rainSum
}

private fun getSnowfallSum(
    data: Map<Long, Double>,
    time: Long
): Double {

    val startIndex = data.toList().indexOfFirst { it.first >= time }.takeIf { it != -1 } ?: 0

    val data =
        data.toList().drop(maxOf(0, startIndex)).take(WeatherSource.NWS.hourlyAggregationLimitHours)


    val snowfallSum = data.sumOf { it.second ?: 0.0 }

    return snowfallSum
}

private fun getHourlyConditionsForDay(
    data: NwsHourlyForecastPeriodsJson,
    time: Long
): List<WeatherCondition> {
    val startIndex =
        data.periods.indexOfFirst { it.startTime.iso8601TimestampToMilliseconds() >= time }
            .takeIf { it != -1 } ?: 0

    val conditions = data.periods.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.NWS.hourlyAggregationLimitHours)
        .map { NwsWeatherConditionMap.getCondition(it.icon) }

    return conditions
}

private fun Double.pressurePaToHpa(): Double {
    return (this / 100)
}


private fun Double.kmhToMs(): Double {
    return (this / 3.6)
}