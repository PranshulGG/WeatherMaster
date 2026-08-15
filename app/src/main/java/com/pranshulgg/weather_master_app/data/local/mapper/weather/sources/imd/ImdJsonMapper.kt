package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.imd

import com.google.gson.JsonElement
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.imd.model.ImdForecastModel
import com.pranshulgg.weather_master_app.core.utils.formatters.getCurrentTimeFor
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.calculations.computeApparentTemperature
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.isSameDay
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.hours


fun ImdForecastModel.toDomain(location: Location): Weather {
    val zoneId = location.timezone
    val currentTime = getCurrentTimeFor(zoneId)

    val formatter = SimpleDateFormat("yyyyMMddHH", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    val hourlyTemps = mutableMapOf<Long, Double?>()
    val hourlyPrecipitation = mutableMapOf<Long, Double?>()
    val hourlyHumidity = mutableMapOf<Long, Double?>()
    val hourlyWindSpeedMs = mutableMapOf<Long, Double?>()
    val hourlyWindDirection = mutableMapOf<Long, Double?>()
    val hourlyCloudCover = mutableMapOf<Long, Double?>()

    var key: Long

    val forecasts = listOf(this.forecast6hr, this.forecast3hr, this.forecast1hr)

    val params = listOf(
        mapOf(
            "time" to formatter.parse(this.timeStamp3)?.time,
            "interval" to 6.hours.inWholeMilliseconds,
            "size" to 40
        ),
        mapOf(
            "time" to formatter.parse(this.timeStamp2)?.time,
            "interval" to 3.hours.inWholeMilliseconds,
            "size" to 40
        ),
        mapOf(
            "time" to formatter.parse(this.timeStamp1)?.time,
            "interval" to 1.hours.inWholeMilliseconds,
            "size" to 36
        ),
    )

    forecasts.forEachIndexed { index, forecast ->
        val time = params[index]["time"] ?: return@forEachIndexed
        val interval = params[index]["interval"] ?: return@forEachIndexed
        val size = params[index]["size"] ?: return@forEachIndexed

        for (i in 1..size.toInt()) {
            key = time + (interval * i)
            hourlyTemps[key] = forecast?.temperature?.getOrNull(i).toSafeDouble()
            hourlyPrecipitation[key] = forecast?.precipitation?.getOrNull(i).toSafeDouble()
            hourlyHumidity[key] = forecast?.humidity?.getOrNull(i).toSafeDouble()
            hourlyWindSpeedMs[key] = forecast?.windSpeedMs?.getOrNull(i).toSafeDouble()
            hourlyWindDirection[key] = forecast?.windDirection?.getOrNull(i).toSafeDouble()
            hourlyCloudCover[key] = forecast?.cloudCover?.getOrNull(i).toSafeDouble()
        }
    }

    val hourly = hourlyTemps.filter { it.value != null }.map {
        WeatherHourly(
            temperature = it.value,
            rain = hourlyPrecipitation[it.key] ?: 0.0,
            humidity = hourlyHumidity[it.key],
            windSpeed = WindSpeedUnit.MPS.convert(hourlyWindSpeedMs[it.key], WindSpeedUnit.KPH),
            windDirection = WindDirection.toWindDirectionFromDegrees(hourlyWindDirection[it.key]?.roundToInt()),
            snowfall = null,
            uvIndex = null,
            pressureMsl = null,
            visibility = null,
            dewPoint = null,
            weatherCondition = computeWeatherCondition(
                hourlyPrecipitation[it.key],
                hourlyCloudCover[it.key]
            ),
            time = it.key,
            precipitationProbability = null
        )
    }

    val currentHourlyIndex = findHourlyIndexForTime(hourly.map { it.time }, currentTime)

    val futureHourly = hourly.filter { it.time >= currentTime }

    val dailyDates =
        futureHourly
            .map {
                Instant.ofEpochMilli(it.time)
                    .atZone(safeZoneId(zoneId))
                    .toLocalDate()
            }
            .distinct()

    val dailyDateFormatter: (LocalDate) -> Long = {
        it.atStartOfDay(safeZoneId(zoneId)).toInstant().toEpochMilli()
    }

    val sunTimings = getSunTimings(
        dailyDates.map {
            dailyDateFormatter(it)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        dailyDates.map {
            dailyDateFormatter(it)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )



    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = hourly[currentHourlyIndex].temperature,
            humidity = hourly[currentHourlyIndex].humidity ?: 0.0,
            windSpeed = hourly[currentHourlyIndex].windSpeed,
            windDirection = hourly[currentHourlyIndex].windDirection,
            pressureMsl = null,
            visibility = null,
            cloudCover = null,
            uvIndex = null,
            weatherCondition = hourly[currentHourlyIndex].weatherCondition,
            feelsLike = computeApparentTemperature(
                tempC = hourly[currentHourlyIndex].temperature,
                humidity = hourly[currentHourlyIndex].humidity,
                windMs = WindSpeedUnit.KPH.convert(
                    hourly[currentHourlyIndex].windSpeed,
                    WindSpeedUnit.MPS
                ),
            ),
            time = currentTime,
            dewPoint = null,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = hourly,
        daily = dailyDates.mapIndexed { index, date ->

            val time = dailyDateFormatter(date)

            val dayHourly = hourlyForDay(
                futureHourly,
                time,
                zoneId
            )


            val temperatureMin = dayHourly.minOf { it.temperature ?: -1.0 }.takeIf { it != -1.0 }
            val temperatureMax = dayHourly.maxOf { it.temperature ?: -1.0 }.takeIf { it != -1.0 }


            val windSpeedAvg =
                dayHourly.map { it.windSpeed ?: -1.0 }.average().takeIf { it != -1.0 }

            val windDirection = dayHourly.mapNotNull { it.windDirection }.maxOrNull()

            val rainSum = dayHourly.sumOf { it.rain }

            val icon = dayHourly.map { it.weatherCondition }.groupingBy { it }
                .eachCount().maxByOrNull { it.value }

            val condition = computeDailyWeatherCondition(
                dayHourly.map { it.weatherCondition },
                icon!!.key
            )

            val humidityAvg = dayHourly.map { it.humidity ?: -1.0 }.average().takeIf { it != -1.0 }

            WeatherDaily(
                temperatureMin = temperatureMin,
                temperatureMax = temperatureMax,
                windSpeed = windSpeedAvg,
                windDirection = windDirection,
                rainSum = rainSum,
                snowfallSum = null,
                uvIndexMax = null,
                weatherCondition = condition,
                time = time,
                precipitationProbabilityMax = null,
                pressureMsl = null,
                visibility = null,
                humidity = humidityAvg,
                dewPoint = null,
                sunrise = sunTimings[index].sunrise ?: -0L,
                sunset = sunTimings[index].sunset ?: -0L,
                moonrise = moonTimings[index].moonrise ?: -0L,
                moonset = moonTimings[index].moonset ?: -0L,
                moonPhase = moonTimings[index].phase,
                dawn = sunTimings[index].dawn ?: 0L,
                dusk = sunTimings[index].dusk ?: 0L,
            )
        },
    )
}


// Pretty barebones
private fun computeWeatherCondition(
    precipitationMm: Double?,
    cloudCover: Double?
): WeatherCondition {
    if (precipitationMm != null && precipitationMm >= 1.0) {
        return WeatherCondition.RAIN
    }

    return when {
        cloudCover == null -> WeatherCondition.PARTLY_CLOUDY
        cloudCover >= 70.0 -> WeatherCondition.OVERCAST
        cloudCover >= 30.0 -> WeatherCondition.PARTLY_CLOUDY
        cloudCover >= 10.0 -> WeatherCondition.MOSTLY_CLEAR
        else -> WeatherCondition.CLEAR_SKY
    }
}

private fun hourlyForDay(
    data: List<WeatherHourly>,
    time: Long,
    zoneId: String
): List<WeatherHourly> {
    val startIndex = data.indexOfFirst { it.time >= time }
        .takeIf { it != -1 }
        ?: 0

    val data = data.drop(maxOf(0, startIndex)).takeWhile { isSameDay(it.time, time, zoneId) }

    return data
}

private fun JsonElement?.toSafeDouble(): Double? {
    if (this == null || this.isJsonNull) return null

    return try {
        if (isJsonPrimitive) {

            if (asJsonPrimitive.isString) {
                asJsonPrimitive.asString.toDoubleOrNull()
            } else {
                asJsonPrimitive.asDouble.takeUnless { it.isNaN() }
            }
        } else {
            null
        }
    } catch (_: Exception) {
        null
    }
}