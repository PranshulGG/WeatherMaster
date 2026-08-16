package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.cwa

import com.pranshulgg.weather_master_app.core.model.astro.MoonPhase
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.CwaWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.json.CwaLocationJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.json.CwaWeatherElementJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.model.CwaForecastBundle
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.formatters.getCurrentTimeFor
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime
import java.time.OffsetDateTime

// CWA reports wind speed in m/s - needs converting to this app's canonical km/h.
// Temperature is already Celsius. CWA's forecast product has no rain-amount-in-mm field at all
// (only probability + a text description), so rain/snowfall are always 0.0/null here, same
// treatment IPMA uses for the same reason.

private data class CwaHourPoint(
    val time: Long,
    val condition: WeatherCondition,
    val temperature: Double?,
    val feelsLike: Double?,
    val humidity: Double?,
    val dewPoint: Double?,
    val precipitationProbability: Int?,
    val windDirection: WindDirection?,
    val windSpeed: Double?
)

private data class CwaDayBlock(
    val startTime: Long,
    val condition: WeatherCondition,
    val temperatureMax: Double?,
    val temperatureMin: Double?,
    val humidity: Double?,
    val dewPoint: Double?,
    val windDirection: WindDirection?,
    val windSpeed: Double?,
    val precipitationProbability: Int?,
    val uvIndex: Double?
)

fun CwaForecastBundle.toDomain(location: Location): Weather {

    val timezone = location.timezone
    val currentTime = getCurrentTimeFor(timezone)

    val shortLoc = shortRange.records?.Locations?.firstOrNull()?.Location?.firstOrNull()
    val weeklyLoc = weekly.records?.Locations?.firstOrNull()?.Location?.firstOrNull()

    val hourPoints = buildHourPoints(shortLoc).sortedBy { it.time }

    val currentIndex = findHourlyIndexForTime(hourPoints.map { it.time }, currentTime)
    val current = hourPoints.getOrNull(currentIndex)

    val dayBlocks = buildDayBlocks(weeklyLoc, timezone).sortedBy { it.startTime }
    val blocksByDay = dayBlocks.groupBy { it.startTime.normalizeToDay(timezone) }
    val dailyDates = blocksByDay.keys.sorted()

    val sunTimings = getSunTimings(dailyDates, timezone, location.latitude, location.longitude)
    val moonTimings = getMoonTimings(dailyDates, timezone, location.latitude, location.longitude)

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = current?.temperature,
            humidity = current?.humidity ?: 0.0,
            windSpeed = current?.windSpeed,
            windDirection = current?.windDirection,
            pressureMsl = null,
            visibility = null,
            cloudCover = null,
            uvIndex = null,
            weatherCondition = current?.condition ?: WeatherCondition.NO_CONDITION_FOUND,
            feelsLike = current?.feelsLike,
            time = currentTime,
            dewPoint = current?.dewPoint,
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
                humidity = it.humidity,
                dewPoint = it.dewPoint,
                weatherCondition = it.condition,
                time = it.time,
                precipitationProbability = it.precipitationProbability
            )
        },
        daily = dailyDates.mapIndexed { index, dayTime ->

            val blocks = blocksByDay[dayTime].orEmpty()

            val condition = computeDailyWeatherCondition(
                blocks.map { it.condition },
                blocks.lastOrNull()?.condition ?: WeatherCondition.NO_CONDITION_FOUND
            )

            val windBlock = blocks
                .filter { it.windSpeed != null && it.windSpeed > 0 }
                .maxByOrNull { it.windSpeed!! }

            val precipitationProbabilityMax = blocks.mapNotNull { it.precipitationProbability }.maxOrNull()

            val humidityAvg = blocks.mapNotNull { it.humidity }.takeIf { it.isNotEmpty() }?.average()
            val dewPointAvg = blocks.mapNotNull { it.dewPoint }.takeIf { it.isNotEmpty() }?.average()
            val uvIndexMax = blocks.mapNotNull { it.uvIndex }.maxOrNull()

            WeatherDaily(
                temperatureMin = blocks.mapNotNull { it.temperatureMin }.minOrNull(),
                temperatureMax = blocks.mapNotNull { it.temperatureMax }.maxOrNull(),
                windSpeed = windBlock?.windSpeed,
                windDirection = windBlock?.windDirection,
                rainSum = 0.0,
                snowfallSum = null,
                uvIndexMax = uvIndexMax,
                weatherCondition = condition,
                time = dayTime,
                precipitationProbabilityMax = precipitationProbabilityMax,
                pressureMsl = null,
                visibility = null,
                humidity = humidityAvg,
                dewPoint = dewPointAvg,
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

private fun buildHourPoints(loc: CwaLocationJson?): List<CwaHourPoint> {
    val tempEl = loc.element("溫度")
    val dewEl = loc.element("露點溫度")
    val humidityEl = loc.element("相對濕度")
    val apparentEl = loc.element("體感溫度")
    val windDirEl = loc.element("風向")
    val windSpeedEl = loc.element("風速")
    val precipEl = loc.element("3小時降雨機率")
    val conditionEl = loc.element("天氣現象")

    // The hourly timeline is defined by whichever point-in-time element has the finest
    // resolution (Temperature, confirmed hourly) - other elements (wind) sample less often
    // and are looked up per-hour via nearest-look-back, same for the 3h interval elements.
    val hourTimes = tempEl?.Time.orEmpty().mapNotNull { it.DataTime.toEpochMillisOrNull() }.distinct().sorted()

    return hourTimes.map { t ->
        CwaHourPoint(
            time = t,
            condition = CwaWeatherConditionMap.getCondition(conditionEl.intervalValueAt(t, "WeatherCode")),
            temperature = tempEl.pointValueAt(t).toSafeDouble(),
            feelsLike = apparentEl.pointValueAt(t, "ApparentTemperature").toSafeDouble(),
            humidity = humidityEl.pointValueAt(t).toSafeDouble(),
            dewPoint = dewEl.pointValueAt(t).toSafeDouble(),
            precipitationProbability = precipEl.intervalValueAt(t, "ProbabilityOfPrecipitation")
                .toSafeProbability(),
            windDirection = cwaWindDirectionFromString(windDirEl.pointValueAt(t)),
            windSpeed = windSpeedEl.pointValueAt(t, "WindSpeed").toSafeDouble()
                ?.let { WindSpeedUnit.MPS.convert(it, WindSpeedUnit.KPH) }
        )
    }
}

private fun buildDayBlocks(loc: CwaLocationJson?, timezone: String): List<CwaDayBlock> {
    val conditionEl = loc.element("天氣現象")
    val maxTempEl = loc.element("最高溫度")
    val minTempEl = loc.element("最低溫度")
    val humidityEl = loc.element("平均相對濕度")
    val dewEl = loc.element("平均露點溫度")
    val windDirEl = loc.element("風向")
    val windSpeedEl = loc.element("風速")
    val precipEl = loc.element("12小時降雨機率")
    val uvEl = loc.element("紫外線指數")

    // Every element in the weekly dataset shares the same 12h block grid for a given location,
    // so the condition element's block boundaries are used as the master list.
    return conditionEl?.Time.orEmpty().mapNotNull { block ->
        val start = block.StartTime.toEpochMillisOrNull() ?: return@mapNotNull null
        val end = block.EndTime.toEpochMillisOrNull() ?: return@mapNotNull null
        val mid = (start + end) / 2

        CwaDayBlock(
            startTime = start,
            condition = CwaWeatherConditionMap.getCondition(block.ElementValue?.firstOrNull()?.get("WeatherCode")),
            temperatureMax = maxTempEl.intervalValueAt(mid, "MaxTemperature").toSafeDouble(),
            temperatureMin = minTempEl.intervalValueAt(mid, "MinTemperature").toSafeDouble(),
            humidity = humidityEl.intervalValueAt(mid, "RelativeHumidity").toSafeDouble(),
            dewPoint = dewEl.intervalValueAt(mid, "DewPoint").toSafeDouble(),
            windDirection = cwaWindDirectionFromString(windDirEl.intervalValueAt(mid, "WindDirection")),
            windSpeed = windSpeedEl.intervalValueAt(mid, "WindSpeed").toSafeDouble()
                ?.let { WindSpeedUnit.MPS.convert(it, WindSpeedUnit.KPH) },
            precipitationProbability = precipEl.intervalValueAt(mid, "ProbabilityOfPrecipitation")
                .toSafeProbability(),
            uvIndex = uvEl.intervalValueAt(mid, "UVIndex").toSafeDouble()
        )
    }
}

private fun CwaLocationJson?.element(name: String): CwaWeatherElementJson? =
    this?.WeatherElement?.firstOrNull { it.ElementName == name }

// Looks up the most recent point-in-time (DataTime) entry at or before targetMillis - used both
// for elements sampled at the same cadence as the hourly timeline and coarser ones (e.g. wind,
// sampled every 3h in the short-range dataset).
private fun CwaWeatherElementJson?.pointValueAt(targetMillis: Long, key: String? = null): String? {
    val entry = this?.Time.orEmpty()
        .mapNotNull { entry -> entry.DataTime.toEpochMillisOrNull()?.let { it to entry } }
        .filter { it.first <= targetMillis }
        .maxByOrNull { it.first }
        ?.second ?: return null

    val values = entry.ElementValue?.firstOrNull() ?: return null
    return if (key != null) values[key] else values.values.firstOrNull()
}

// Looks up the StartTime/EndTime block containing targetMillis.
private fun CwaWeatherElementJson?.intervalValueAt(targetMillis: Long, key: String? = null): String? {
    val entry = this?.Time.orEmpty().firstOrNull { entry ->
        val start = entry.StartTime.toEpochMillisOrNull() ?: return@firstOrNull false
        val end = entry.EndTime.toEpochMillisOrNull() ?: return@firstOrNull false
        targetMillis in start until end
    } ?: return null

    val values = entry.ElementValue?.firstOrNull() ?: return null
    return if (key != null) values[key] else values.values.firstOrNull()
}

private fun String?.toEpochMillisOrNull(): Long? {
    if (this.isNullOrBlank()) return null
    return try {
        OffsetDateTime.parse(this).toInstant().toEpochMilli()
    } catch (e: Exception) {
        null
    }
}

// CWA's precipitation probability is the literal string "-" past day 3 (a forecast-model
// limitation, not missing data) - parsed as null so the UI hides it the same way it already
// does for any other source's null precipitationProbability.
private fun String?.toSafeProbability(): Int? {
    if (this.isNullOrBlank() || this == "-") return null
    return toSafeDouble()?.toInt()
}

// CWA reports wind direction as Chinese compass words, not English abbreviations.
private fun cwaWindDirectionFromString(value: String?): WindDirection? {
    return when (value?.trim()) {
        "北風" -> WindDirection.N
        "東北風" -> WindDirection.NE
        "東風" -> WindDirection.E
        "東南風" -> WindDirection.SE
        "南風" -> WindDirection.S
        "西南風" -> WindDirection.SW
        "西風" -> WindDirection.W
        "西北風" -> WindDirection.NW
        "偏北風" -> WindDirection.N
        "偏東風" -> WindDirection.E
        "偏南風" -> WindDirection.S
        "偏西風" -> WindDirection.W
        else -> null // "不定風向" (variable), blank, or unrecognized
    }
}
