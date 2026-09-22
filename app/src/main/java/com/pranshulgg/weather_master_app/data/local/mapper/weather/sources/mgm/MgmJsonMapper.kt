package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.mgm

import com.pranshulgg.weather_master_app.core.model.astro.MoonPhase
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.model.GismeteoModelHourly
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.MgmWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmDailyJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmHourlyForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.bundle.MgmBundle
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.data.local.mapper.utils.WeatherUtils.safeAverage
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.hours

private fun Double?.takeIfValid(): Double? {
    return this?.takeIf { it > -9000.0 }
}

private val ISTANBUL_ZONE = ZoneId.of("Europe/Istanbul")
private val MGM_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

private fun String?.toMilliseconds(): Long? {
    if (this.isNullOrBlank()) {
        return null
    }
    return LocalDateTime.parse(this, MGM_TIME_FORMATTER)
        .atZone(ISTANBUL_ZONE)
        .toInstant()
        .toEpochMilli()

}

data class DailyItem(
    val time: String?,
    val minTemp: Double?,
    val maxTemp: Double?,
    val windSpeedMs: Double?,
    val windDirection: Double?,
    val humidityMin: Double?,
    val humidityMax: Double?,
    val condition: String?
)

fun MgmBundle.toDomain(location: Location): Weather {

    val timezone = location.timezone

    val daily = getDaily(daily)

    val dailyTimings = daily.mapNotNull { it.time.toMilliseconds()?.normalizeToDay(timezone) }

    val sunTimings =
        getSunTimings(dailyTimings, location.timezone, location.latitude, location.longitude)
    val moonTimings =
        getMoonTimings(dailyTimings, location.timezone, location.latitude, location.longitude)

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = current?.temperature.takeIfValid(),
            humidity = current?.humidity.takeIfValid(),
            windSpeed = WindSpeedUnit.MPS.convert(
                current?.windSpeedMs.takeIfValid(),
                WindSpeedUnit.KPH
            ),
            windDirection = WindDirection.toWindDirectionFromDegrees(
                current?.windDirection.takeIfValid()?.toInt()
            ),
            pressureMsl = current?.pressureMsl.takeIfValid(),
            visibility = current?.visibility.takeIfValid()?.toInt(),
            cloudCover = null,
            uvIndex = null,
            weatherCondition = MgmWeatherConditionMap.getCondition(current?.condition),
            feelsLike = current?.feelsLike.takeIfValid(),
            dewPoint = null,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = hourly.filter { it?.time != null }.map {
            WeatherHourly(
                temperature = it?.temperature.takeIfValid(),
                windSpeed = WindSpeedUnit.MPS.convert(
                    it?.windSpeed.takeIfValid(),
                    WindSpeedUnit.KPH
                ),
                windDirection = WindDirection.toWindDirectionFromDegrees(
                    it?.windDirection.takeIfValid()?.toInt()
                ),
                rain = 0.0,
                snowfall = null,
                uvIndex = null,
                pressureMsl = null,
                visibility = null,
                humidity = it?.humidity.takeIfValid(),
                dewPoint = null,
                weatherCondition = MgmWeatherConditionMap.getCondition(it?.condition),
                time = it?.time.toMilliseconds()!!,
                precipitationProbability = null
            )
        },
        daily = daily.mapIndexed { index, it ->

            val humidityAvg = listOf(it.humidityMin, it.humidityMax).safeAverage()

            val time = it.time.toMilliseconds()!!.normalizeToDay(timezone)

            val oneDay = 24.hours

            val hourlyConditions = hourly.filter {
                it?.time?.toMilliseconds()?.let { hourlyTime ->
                    hourlyTime >= time && hourlyTime < time + oneDay.inWholeMilliseconds
                } == true
            }.map {
                MgmWeatherConditionMap.getCondition(it?.condition)
            }

            val weatherCondition = computeDailyWeatherCondition(
                hourlyConditions,
                MgmWeatherConditionMap.getCondition(it.condition)
            )
            WeatherDaily(
                temperatureMin = it.minTemp.takeIfValid(),
                temperatureMax = it.maxTemp.takeIfValid(),
                windSpeed = WindSpeedUnit.MPS.convert(
                    it.windSpeedMs.takeIfValid(),
                    WindSpeedUnit.KPH
                ),
                windDirection = WindDirection.toWindDirectionFromDegrees(
                    it.windDirection.takeIfValid()?.toInt()
                ),
                rainSum = 0.0,
                snowfallSum = null,
                uvIndexMax = null,
                weatherCondition = weatherCondition,
                time = time,
                precipitationProbabilityMax = null,
                pressureMsl = null,
                visibility = null,
                humidity = humidityAvg,
                dewPoint = null,
                sunrise = sunTimings.getOrNull(index)?.sunrise,
                sunset = sunTimings.getOrNull(index)?.sunset,
                dawn = sunTimings.getOrNull(index)?.dawn,
                dusk = sunTimings.getOrNull(index)?.dusk,
                moonrise = moonTimings.getOrNull(index)?.moonrise,
                moonset = moonTimings.getOrNull(index)?.moonset,
                moonPhase = moonTimings.getOrNull(index)?.phase ?: MoonPhase.UNKNOWN,
            )
        },
    )

}

private fun getDaily(daily: MgmDailyJson): List<DailyItem> {

    val days = listOf(
        DailyItem(
            time = daily.dateDay0,
            minTemp = daily.minTempDay0.takeIfValid(),
            maxTemp = daily.maxTempDay0.takeIfValid(),
            windSpeedMs = daily.windSpeedDay0.takeIfValid(),
            windDirection = daily.windDirectionDay0.takeIfValid(),
            humidityMin = daily.minHumidityDay0.takeIfValid(),
            humidityMax = daily.maxHumidityDay0.takeIfValid(),
            condition = daily.conditionDay0
        ),
        DailyItem(
            time = daily.dateDay1,
            minTemp = daily.minTempDay1.takeIfValid(),
            maxTemp = daily.maxTempDay1.takeIfValid(),
            windSpeedMs = daily.windSpeedDay1.takeIfValid(),
            windDirection = daily.windDirectionDay1.takeIfValid(),
            humidityMin = daily.minHumidityDay1.takeIfValid(),
            humidityMax = daily.maxHumidityDay1.takeIfValid(),
            condition = daily.conditionDay1
        ),
        DailyItem(
            time = daily.dateDay2,
            minTemp = daily.minTempDay2.takeIfValid(),
            maxTemp = daily.maxTempDay2.takeIfValid(),
            windSpeedMs = daily.windSpeedDay2.takeIfValid(),
            windDirection = daily.windDirectionDay2.takeIfValid(),
            humidityMin = daily.minHumidityDay2.takeIfValid(),
            humidityMax = daily.maxHumidityDay2.takeIfValid(),
            condition = daily.conditionDay2
        ),
        DailyItem(
            time = daily.dateDay3,
            minTemp = daily.minTempDay3.takeIfValid(),
            maxTemp = daily.maxTempDay3.takeIfValid(),
            windSpeedMs = daily.windSpeedDay3.takeIfValid(),
            windDirection = daily.windDirectionDay3.takeIfValid(),
            humidityMin = daily.minHumidityDay3.takeIfValid(),
            humidityMax = daily.maxHumidityDay3.takeIfValid(),
            condition = daily.conditionDay3
        ),
        DailyItem(
            time = daily.dateDay4,
            minTemp = daily.minTempDay4.takeIfValid(),
            maxTemp = daily.maxTempDay4.takeIfValid(),
            windSpeedMs = daily.windSpeedDay4.takeIfValid(),
            windDirection = daily.windDirectionDay4.takeIfValid(),
            humidityMin = daily.minHumidityDay4.takeIfValid(),
            humidityMax = daily.maxHumidityDay4.takeIfValid(),
            condition = daily.conditionDay4
        ),
        DailyItem(
            time = daily.dateDay5,
            minTemp = daily.minTempDay5.takeIfValid(),
            maxTemp = daily.maxTempDay5.takeIfValid(),
            windSpeedMs = daily.windSpeedDay5.takeIfValid(),
            windDirection = daily.windDirectionDay5.takeIfValid(),
            humidityMin = daily.minHumidityDay5.takeIfValid(),
            humidityMax = daily.maxHumidityDay5.takeIfValid(),
            condition = daily.conditionDay5
        ),
    )

    return days.distinctBy { it.time }

}

private fun hourlyForDay(
    data: List<MgmHourlyForecastJson?>,
    time: Long
): List<MgmHourlyForecastJson?> {
    val startIndex =
        data.indexOfFirst { it!!.time.toMilliseconds()!! >= time }
            .takeIf { it != -1 }
            ?: 0

    val data = data.toList().drop(maxOf(0, startIndex))
        .take(24)

    return data
}