package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.jma

import com.pranshulgg.weather_master_app.core.model.astro.MoonPhase
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.JmaConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.value
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.model.JmaForecastBundle
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.formatters.getCurrentTimeFor
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.calculations.computeApparentTemperature
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime
import java.time.OffsetDateTime

// JMA reports wind speed as a level+range in m/s (converted to km/h below) and temperature
// already in Celsius. The forecast product has no rain/snow-amount field at all (only
// probability), so rain/snowfall are always 0.0/null here, same treatment IPMA/AEMET/CWA use
// for the same reason.

private data class JmaHourPoint(
    val time: Long,
    val condition: WeatherCondition,
    val temperature: Double?,
    val precipitationProbability: Int?,
    val windDirection: WindDirection?,
    val windSpeed: Double?
)

fun JmaForecastBundle.toDomain(location: Location): Weather {

    val timezone = location.timezone
    val currentTime = getCurrentTimeFor(timezone)

    val areaTs = hourly.areaTimeSeries
    val pointTs = hourly.pointTimeSeries

    val hourTimes = areaTs?.timeDefines.orEmpty().mapNotNull { it.dateTime.toEpochMillisOrNull() }
    val temps = pointTs?.temperature.orEmpty()
    val weatherWords = areaTs?.weather.orEmpty()
    val winds = areaTs?.wind.orEmpty()

    // Near-term block's pops (6h blocks, real precip probability) - broadcast onto the finer
    // 3h VPFD timeline by finding each hour's enclosing pop block.
    val nearTermArea = forecast.getOrNull(0)?.timeSeries?.getOrNull(1)?.areas?.firstOrNull()
    val popTimeDefines = forecast.getOrNull(0)?.timeSeries?.getOrNull(1)?.timeDefines.orEmpty()
        .mapNotNull { it.toEpochMillisOrNull() }
    val pops = nearTermArea?.pops.orEmpty()

    fun popAt(targetMillis: Long): Int? {
        val index = popTimeDefines.indexOfLast { it <= targetMillis }
        if (index == -1) return null
        val blockEnd = popTimeDefines.getOrNull(index + 1) ?: (popTimeDefines[index] + 6 * 3_600_000L)
        if (targetMillis >= blockEnd) return null
        return pops.getOrNull(index)?.toSafeProbability()
    }

    val hourPoints = hourTimes.mapIndexed { index, t ->
        val windEntry = winds.getOrNull(index)
        JmaHourPoint(
            time = t,
            condition = JmaConditionMap.getCondition(weatherWords.getOrNull(index)),
            temperature = temps.getOrNull(index)?.toDouble(),
            precipitationProbability = popAt(t),
            windDirection = jmaWindDirectionFromString(windEntry?.direction),
            windSpeed = windEntry?.range.toMidpointMps()
                ?.let { WindSpeedUnit.MPS.convert(it, WindSpeedUnit.KPH) }
        )
    }.sortedBy { it.time }

    val currentIndex = findHourlyIndexForTime(hourPoints.map { it.time }, currentTime)
    val nearestHourPoint = hourPoints.getOrNull(currentIndex)

    // Daily - the weekly block is already one entry per day, no 12h-block grouping needed.
    val weeklyConditionArea = forecast.getOrNull(1)?.timeSeries?.getOrNull(0)?.areas?.firstOrNull()
    val weeklyTempsArea = forecast.getOrNull(1)?.timeSeries?.getOrNull(1)?.areas?.firstOrNull()

    val dailyTimes = forecast.getOrNull(1)?.timeSeries?.getOrNull(0)?.timeDefines.orEmpty()
        .mapNotNull { it.toEpochMillisOrNull() }

    val weatherCodes = weeklyConditionArea?.weatherCodes.orEmpty()
    val dailyPops = weeklyConditionArea?.pops.orEmpty()
    val tempsMin = weeklyTempsArea?.tempsMin.orEmpty()
    val tempsMax = weeklyTempsArea?.tempsMax.orEmpty()

    // The weekly block leaves today's tempsMin/tempsMax as "" (confirmed live) since the
    // near-term block covers today at finer granularity instead - fall back to the real
    // hourly temps (which do cover today) so today's card isn't left blank.
    val hourlyTempsByDay = hourPoints
        .mapNotNull { p -> p.temperature?.let { p.time.normalizeToDay(timezone) to it } }
        .groupBy({ it.first }, { it.second })

    val sunTimings = getSunTimings(dailyTimes, timezone, location.latitude, location.longitude)
    val moonTimings = getMoonTimings(dailyTimes, timezone, location.latitude, location.longitude)

    // Current conditions: real numeric values from AMeDAS where available, condition derived
    // from the nearest hourly point since AMeDAS has no sky-condition field of its own.
    val amedasWindSpeed = current?.wind.value()
        ?.let { WindSpeedUnit.MPS.convert(it, WindSpeedUnit.KPH) }
    val amedasWindDirection = current?.windDirection.value()
        ?.let { WindDirection.toWindDirectionFromDegrees(amedasIndexToDegrees(it)) }
    // AMeDAS doesn't report apparent temperature directly - compute it from its own real
    // temp/humidity/wind (same convention as FMI/DWD/NWS/SMHI/etc use for this exact gap).
    val amedasFeelsLike = computeApparentTemperature(
        tempC = current?.temp.value(),
        humidity = current?.humidity.value(),
        windMs = current?.wind.value()
    )

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = current?.temp.value() ?: nearestHourPoint?.temperature,
            humidity = current?.humidity.value(),
            windSpeed = amedasWindSpeed ?: nearestHourPoint?.windSpeed,
            windDirection = amedasWindDirection ?: nearestHourPoint?.windDirection,
            pressureMsl = current?.pressure.value(),
            visibility = null,
            cloudCover = null,
            uvIndex = null,
            weatherCondition = nearestHourPoint?.condition ?: WeatherCondition.NO_CONDITION_FOUND,
            feelsLike = amedasFeelsLike,
            time = currentTime,
            dewPoint = null,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = hourPoints.map {
            WeatherHourly(
                temperature = it.temperature,
                windSpeed = it.windSpeed,
                windDirection = it.windDirection,
                rain = 0.0,
                snowfall = null,
                uvIndex = null,
                pressureMsl = null,
                visibility = null,
                humidity = null,
                dewPoint = null,
                weatherCondition = it.condition,
                time = it.time,
                precipitationProbability = it.precipitationProbability
            )
        },
        daily = dailyTimes.mapIndexed { index, dayTime ->
            val fallbackTemps = hourlyTempsByDay[dayTime.normalizeToDay(timezone)]
            WeatherDaily(
                temperatureMin = tempsMin.getOrNull(index)?.toSafeDouble()
                    ?: fallbackTemps?.minOrNull(),
                temperatureMax = tempsMax.getOrNull(index)?.toSafeDouble()
                    ?: fallbackTemps?.maxOrNull(),
                windSpeed = null,
                windDirection = null,
                rainSum = 0.0,
                snowfallSum = null,
                uvIndexMax = null,
                weatherCondition = JmaConditionMap.getDailyCondition(weatherCodes.getOrNull(index)),
                time = dayTime,
                precipitationProbabilityMax = dailyPops.getOrNull(index)?.toSafeProbability(),
                pressureMsl = null,
                visibility = null,
                humidity = null,
                dewPoint = null,
                sunrise = sunTimings.getOrNull(index)?.sunrise ?: 0L,
                sunset = sunTimings.getOrNull(index)?.sunset ?: 0L,
                moonrise = moonTimings.getOrNull(index)?.moonrise ?: 0L,
                moonset = moonTimings.getOrNull(index)?.moonset ?: 0L,
                moonPhase = moonTimings.getOrNull(index)?.phase ?: MoonPhase.NEW_MOON,
                dawn = sunTimings.getOrNull(index)?.dawn ?: 0L,
                dusk = sunTimings.getOrNull(index)?.dusk ?: 0L
            )
        }
    )
}

private fun String?.toEpochMillisOrNull(): Long? {
    if (this.isNullOrBlank()) return null
    return try {
        OffsetDateTime.parse(this).toInstant().toEpochMilli()
    } catch (e: Exception) {
        null
    }
}

// JMA's precipitation probability is an empty string "" for the first (already-elapsed) block
// of the near-term series - parsed as null so the UI hides it, same as every other source's
// null precipitationProbability.
private fun String?.toSafeProbability(): Int? {
    if (this.isNullOrBlank()) return null
    return toSafeDouble()?.toInt()
}

// "range" is a "min max" m/s pair (e.g. "3 5") - the actual usable value, unlike the bare
// "speed" level number alongside it.
private fun String?.toMidpointMps(): Double? {
    val parts = this?.trim()?.split(" ") ?: return null
    if (parts.size != 2) return null
    val min = parts[0].toSafeDouble() ?: return null
    val max = parts[1].toSafeDouble() ?: return null
    return (min + max) / 2
}

// AMeDAS wind direction is a 1-16 compass index (1=N, clockwise, 0=calm/no wind).
private fun amedasIndexToDegrees(index: Double): Int? {
    val i = index.toInt()
    if (i !in 1..16) return null
    return ((i - 1) * 22.5).toInt()
}

// JMA reports wind direction as Japanese compass words, not English abbreviations.
private fun jmaWindDirectionFromString(value: String?): WindDirection? {
    return when (value?.trim()) {
        "北" -> WindDirection.N
        "北東" -> WindDirection.NE
        "東" -> WindDirection.E
        "南東" -> WindDirection.SE
        "南" -> WindDirection.S
        "南西" -> WindDirection.SW
        "西" -> WindDirection.W
        "北西" -> WindDirection.NW
        else -> null // "静穏" (calm), blank, or unrecognized
    }
}
