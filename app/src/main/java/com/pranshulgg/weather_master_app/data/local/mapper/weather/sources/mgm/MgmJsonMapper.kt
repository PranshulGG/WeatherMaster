package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.mgm

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.astro.MoonPhase
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.MgmWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmCurrentJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmDailyJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmHourlyForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.bundle.MgmBundle
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// MGM's timestamps carry a 'Z' suffix but are actually Europe/Istanbul local time, not UTC -
// confirmed live and matches a documented note in breezy-weather's MGM source. Parse as local
// time in that zone instead of treating 'Z' as a real UTC offset.
private val ISTANBUL_ZONE = ZoneId.of("Europe/Istanbul")
private val MGM_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

private fun String.mgmTimestampToMilliseconds(): Long? {
    return try {
        LocalDateTime.parse(this, MGM_TIME_FORMATTER)
            .atZone(ISTANBUL_ZONE)
            .toInstant()
            .toEpochMilli()
    } catch (e: Exception) {
        null
    }
}

// MGM uses -9999 as a "no data" sentinel instead of omitting the field.
private fun Double?.orNullIfSentinel(): Double? {
    return this?.takeIf { it > -9000.0 }
}

fun MgmBundle.toDomain(location: Location): Weather {

    val dailyEntries = daily?.let { toDailyEntries(it) } ?: emptyList()
    val hourlyEntries = hourly.orEmpty()

    val dailyTimings = dailyEntries.map { it.first }

    val sunTimings = getSunTimings(dailyTimings, location.timezone, location.latitude, location.longitude)
    val moonTimings = getMoonTimings(dailyTimings, location.timezone, location.latitude, location.longitude)

    return Weather(
        location = location,
        current = current?.toDomain() ?: dailyEntries.firstOrNull()?.let { placeholderCurrent(it.first) }
            ?: placeholderCurrent(System.currentTimeMillis()),
        hourly = hourlyEntries.mapNotNull { it.toDomain() },
        daily = dailyEntries.mapIndexed { index, (time, entry) ->
            entry.toDomain(
                time = time,
                sunrise = sunTimings.getOrNull(index)?.sunrise ?: -1L,
                sunset = sunTimings.getOrNull(index)?.sunset ?: -1L,
                dawn = sunTimings.getOrNull(index)?.dawn ?: -1L,
                dusk = sunTimings.getOrNull(index)?.dusk ?: -1L,
                moonrise = moonTimings.getOrNull(index)?.moonrise ?: -1L,
                moonset = moonTimings.getOrNull(index)?.moonset ?: -1L,
                moonPhase = moonTimings.getOrNull(index)?.phase,
                hourlyForCondition = hourlyEntries,
            )
        },
    )
}

private fun MgmCurrentJson.toDomain(): WeatherCurrent {
    return WeatherCurrent(
        temperature = temperature.orNullIfSentinel(),
        humidity = humidity.orNullIfSentinel(),
        windSpeed = windSpeed.orNullIfSentinel(),
        windDirection = WindDirection.toWindDirectionFromDegrees(windDirection.orNullIfSentinel()?.toInt()),
        pressureMsl = pressureMsl.orNullIfSentinel(),
        visibility = visibility.orNullIfSentinel()?.toInt(),
        cloudCover = null,
        uvIndex = null,
        weatherCondition = MgmWeatherConditionMap.getCondition(condition),
        feelsLike = feelsLike.orNullIfSentinel(),
        time = time?.mgmTimestampToMilliseconds() ?: System.currentTimeMillis(),
        dewPoint = null,
        utcOffsetSeconds = null,
        lastUpdatedInMilli = System.currentTimeMillis()
    )
}

private fun placeholderCurrent(time: Long): WeatherCurrent {
    return WeatherCurrent(
        temperature = null,
        humidity = null,
        windSpeed = null,
        windDirection = null,
        pressureMsl = null,
        visibility = null,
        cloudCover = null,
        uvIndex = null,
        weatherCondition = WeatherCondition.NO_CONDITION_FOUND,
        feelsLike = null,
        time = time,
        dewPoint = null,
        utcOffsetSeconds = null,
        lastUpdatedInMilli = System.currentTimeMillis()
    )
}

private fun MgmHourlyForecastJson.toDomain(): WeatherHourly? {
    val timeMillis = time?.mgmTimestampToMilliseconds() ?: return null

    return WeatherHourly(
        temperature = temperature.orNullIfSentinel(),
        windSpeed = windSpeed.orNullIfSentinel(),
        windDirection = WindDirection.toWindDirectionFromDegrees(windDirection.orNullIfSentinel()?.toInt()),
        rain = 0.0, // MGM's forecast endpoints don't expose a precipitation amount
        snowfall = null,
        uvIndex = null,
        pressureMsl = null,
        visibility = null,
        humidity = humidity.orNullIfSentinel(),
        dewPoint = null,
        weatherCondition = MgmWeatherConditionMap.getCondition(condition),
        time = timeMillis,
        precipitationProbability = null
    )
}

private data class MgmDailyEntry(
    val minTemp: Double?,
    val maxTemp: Double?,
    val windSpeed: Double?,
    val windDirection: Double?,
    val humidityMin: Double?,
    val humidityMax: Double?,
    val condition: String?,
) {
    fun toDomain(
        time: Long,
        sunrise: Long,
        sunset: Long,
        dawn: Long,
        dusk: Long,
        moonrise: Long,
        moonset: Long,
        moonPhase: MoonPhase?,
        hourlyForCondition: List<MgmHourlyForecastJson>,
    ): WeatherDaily {
        val humidityAvg = listOfNotNull(humidityMin, humidityMax).takeIf { it.isNotEmpty() }?.average()

        val hourlyConditionsForDay = hourlyForCondition
            .filter { it.time?.mgmTimestampToMilliseconds()?.let { t -> t >= time && t < time + 86_400_000L } == true }
            .map { MgmWeatherConditionMap.getCondition(it.condition) }

        val weatherCondition = computeDailyWeatherCondition(
            hourlyConditionsForDay,
            MgmWeatherConditionMap.getCondition(condition)
        )

        return WeatherDaily(
            temperatureMin = minTemp,
            temperatureMax = maxTemp,
            windSpeed = windSpeed,
            windDirection = WindDirection.toWindDirectionFromDegrees(windDirection?.toInt()),
            rainSum = 0.0, // MGM's forecast endpoints don't expose a precipitation amount
            snowfallSum = null,
            uvIndexMax = null,
            weatherCondition = weatherCondition,
            time = time,
            precipitationProbabilityMax = null,
            pressureMsl = null,
            visibility = null,
            humidity = humidityAvg,
            dewPoint = null,
            sunrise = sunrise,
            sunset = sunset,
            moonrise = moonrise,
            moonset = moonset,
            moonPhase = moonPhase ?: MoonPhase.NEW_MOON,
            dawn = dawn,
            dusk = dusk
        )
    }
}

private fun toDailyEntries(daily: MgmDailyJson): List<Pair<Long, MgmDailyEntry>> {
    val days = listOf(
        Triple(daily.dateDay1, 0, daily.conditionDay1),
        Triple(daily.dateDay2, 1, daily.conditionDay2),
        Triple(daily.dateDay3, 2, daily.conditionDay3),
        Triple(daily.dateDay4, 3, daily.conditionDay4),
        Triple(daily.dateDay5, 4, daily.conditionDay5),
    )

    val minTemps = listOf(daily.minTempDay1, daily.minTempDay2, daily.minTempDay3, daily.minTempDay4, daily.minTempDay5)
    val maxTemps = listOf(daily.maxTempDay1, daily.maxTempDay2, daily.maxTempDay3, daily.maxTempDay4, daily.maxTempDay5)
    val windSpeeds = listOf(daily.windSpeedDay1, daily.windSpeedDay2, daily.windSpeedDay3, daily.windSpeedDay4, daily.windSpeedDay5)
    val windDirections = listOf(daily.windDirectionDay1, daily.windDirectionDay2, daily.windDirectionDay3, daily.windDirectionDay4, daily.windDirectionDay5)
    val humidityMins = listOf(daily.minHumidityDay1, daily.minHumidityDay2, daily.minHumidityDay3, daily.minHumidityDay4, daily.minHumidityDay5)
    val humidityMaxs = listOf(daily.maxHumidityDay1, daily.maxHumidityDay2, daily.maxHumidityDay3, daily.maxHumidityDay4, daily.maxHumidityDay5)

    return days.mapNotNull { (date, index, condition) ->
        val time = date?.mgmTimestampToMilliseconds() ?: return@mapNotNull null

        time to MgmDailyEntry(
            minTemp = minTemps[index].orNullIfSentinel(),
            maxTemp = maxTemps[index].orNullIfSentinel(),
            windSpeed = windSpeeds[index].orNullIfSentinel(),
            windDirection = windDirections[index].orNullIfSentinel(),
            humidityMin = humidityMins[index].orNullIfSentinel(),
            humidityMax = humidityMaxs[index].orNullIfSentinel(),
            condition = condition,
        )
    }
}
