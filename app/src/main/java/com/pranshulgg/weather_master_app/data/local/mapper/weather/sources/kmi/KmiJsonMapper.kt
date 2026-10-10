package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.kmi

import com.pranshulgg.weather_master_app.core.model.astro.MoonPhase
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.kmi.KmiWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.kmi.json.KmiDayJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.kmi.json.KmiHourJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.kmi.json.KmiWeatherJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.formatters.getCurrentTimeFor
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private fun getFixedHourlyTimings(data: List<KmiHourJson>, zoneId: String): List<Long> {
    var day = LocalDate.now(safeZoneId(zoneId))

    return data.mapIndexed { index, hour ->
        if (index > 0 && hour.dateShow != null) {
            day = day.plusDays(1)
        }

        val iso = LocalDateTime
            .of(
                day,
                LocalTime.of(hour.hour.toSafeDouble()!!.toInt(), 0)
            )
            .atZone(safeZoneId(zoneId))
            .withZoneSameInstant(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT)

        iso.iso8601TimestampToMilliseconds()
    }
}


fun KmiWeatherJson.toDomain(location: Location): Weather {
    val zoneId = location.timezone

    val currentTime = getCurrentTimeFor(zoneId)

    val hourlyTimes = getFixedHourlyTimings(data = forecast.hourly, zoneId)

    val currentHourlyIndex = forecast.hourly[findHourlyIndexForTime(
        hourlyTimes,
        startMilli = currentTime
    )]

    val obsTimestamp = OffsetDateTime.parse(obs.timestamp).toLocalDateTime()

    val resolvedDailyList = mutableListOf<KmiDayJson>()
    val day1 = forecast.daily.getOrNull(0)
    val tonight = forecast.daily.find { it.dayName?.en == "Tonight" }

    // Merge today and tonight into 1 day
    forecast.daily.forEach {

        if (it.period == "1" && tonight != null) {
            return@forEach
        }
        if (it.dayName?.en == "Tonight" && day1 != null) {
            val day = KmiDayJson(
                period = "1",
                tempMin = it.tempMin,
                tempMax = day1.tempMax,
                ww1 = day1.ww1,
                ww2 = it.ww1,
                windDirectionText = it.windDirectionText, // BOTH are usually same
                wind = it.wind, // BOTH are usually same,
                precipChance = listOf(it.precipChance ?: 0.0, day1.precipChance ?: 0.0).max(),
                precipQuantity = it.precipQuantity.plus(day1.precipQuantity),
                dayName = day1.dayName
            )

            resolvedDailyList.add(day)
        } else {
            resolvedDailyList.add(it)
        }
    }

    val dailyTimings = List(resolvedDailyList.size) { index ->
        obsTimestamp.plusDays(index.toLong()).atZone(safeZoneId(zoneId))
            .withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
            .iso8601TimestampToMilliseconds()
    }

    val sunTimings =
        getSunTimings(dailyTimings, location.timezone, location.latitude, location.longitude)
    val moonTimings =
        getMoonTimings(dailyTimings, location.timezone, location.latitude, location.longitude)

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = obs.temp ?: currentHourlyIndex.temp,
            humidity = null,
            windSpeed = currentHourlyIndex.windSpeedKm,
            windDirection = WindDirection.toWindDirectionFromString(currentHourlyIndex.windDirectionText.en),
            pressureMsl = currentHourlyIndex.pressure,
            visibility = null,
            cloudCover = null,
            uvIndex = module.firstOrNull { it.type == "uv" }?.data?.levelValue,
            weatherCondition = KmiWeatherConditionMap.getCondition(currentHourlyIndex.ww?.toInt()),
            feelsLike = null,
            dewPoint = null,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = forecast.hourly.mapIndexed { index, item ->

            val time = hourlyTimes[index]

            WeatherHourly(
                temperature = item.temp,
                windSpeed = item.windSpeedKm,
                windDirection = WindDirection.toWindDirectionFromString(item.windDirectionText.en),
                rain = item.precipQuantity ?: 0.0,
                snowfall = null,
                uvIndex = null,
                pressureMsl = item.pressure,
                visibility = null,
                humidity = null,
                dewPoint = null,
                weatherCondition = KmiWeatherConditionMap.getCondition(item.ww?.toInt()),
                time = time,
                precipitationProbability = item.precipChance.toSafeDouble()?.roundToInt()
            )
        },
        daily = resolvedDailyList.mapIndexed { index, day ->

            val time = obsTimestamp.plusDays(index.toLong()).atZone(safeZoneId(zoneId))
                .withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
                .iso8601TimestampToMilliseconds()

            val condition =
                computeDailyWeatherCondition(List(12) { KmiWeatherConditionMap.getCondition(day.ww1?.toInt()) } +
                        KmiWeatherConditionMap.getCondition(day.ww2?.toInt()),
                    WeatherCondition.NO_CONDITION_FOUND)


            WeatherDaily(
                temperatureMin = day.tempMin,
                temperatureMax = day.tempMax,
                windSpeed = day.wind.speed,
                windDirection = WindDirection.toWindDirectionFromString(day.windDirectionText.en),
                rainSum = day.precipQuantity.toSafeDouble() ?: 0.0,
                snowfallSum = null,
                uvIndexMax = null,
                weatherCondition = condition,
                time = time,
                precipitationProbabilityMax = day.precipChance?.roundToInt() ?: 0,
                pressureMsl = null,
                visibility = null,
                humidity = null,
                dewPoint = null,
                sunrise = sunTimings.getOrNull(index)?.sunrise,
                sunset = sunTimings.getOrNull(index)?.sunset,
                dawn = sunTimings.getOrNull(index)?.dawn,
                dusk = sunTimings.getOrNull(index)?.dusk,
                moonrise = moonTimings.getOrNull(index)?.moonrise,
                moonset = moonTimings.getOrNull(index)?.moonset,
                moonPhase = moonTimings.getOrNull(index)?.phase ?: MoonPhase.UNKNOWN,
            )
        }
    )

}