package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.eccc

import android.util.Log
import androidx.core.text.isDigitsOnly
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.EcccConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.json.EcccHourlyWeatherItemJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.json.EcccWeatherJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.calculations.computeApparentTemperature
import java.time.LocalDate
import java.time.MonthDay
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt


fun EcccWeatherJson.toDomain(location: Location): Weather {
    val current = this.observation
    val hourly = this.hourlyFcst.hourly


    val dailyNight =
        this.dailyFcst.daily.filter { it.periodLabel == "Night" || it.periodLabel == "Tonight" }

    val daily =
        this.dailyFcst.daily.filter { it.periodLabel != "Night" && it.periodLabel != "Tonight" }
            .take(dailyNight.size)

    val sunTimings = getSunTimings(
        daily.map {
            dateToMillis(it.date, location.timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        daily.map {
            dateToMillis(it.date, location.timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = current.temperature.metric?.toSafeDouble(),
            humidity = current.humidity?.toSafeDouble(),
            windSpeed = current.windSpeed.metric?.toSafeDouble(),
            windDirection = WindDirection.toWindDirectionFromString(current.windDirection),
            pressureMsl = PressureUnit.INHG.convert(
                current.pressure.imperial?.toSafeDouble(),
                PressureUnit.HPA
            ),
            visibility = DistanceUnit.KM.convert(
                current.visibility.metric?.toSafeDouble(),
                DistanceUnit.M
            )?.roundToInt(),
            cloudCover = null, // NOT USED IN THE APP
            uvIndex = null,
            weatherCondition = EcccConditionMap.getCondition(current.iconCode),
            feelsLike = current.feelsLike.metric?.toSafeDouble() ?: computeApparentTemperature(
                current.temperature.metric?.toSafeDouble(),
                current.humidity?.toSafeDouble(),
                WindSpeedUnit.KPH.convert(
                    current.windSpeed.metric?.toSafeDouble(),
                    WindSpeedUnit.MPS
                )
            ),
            dewPoint = current.dewpoint.metric?.toSafeDouble(),
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = hourly.map {
            WeatherHourly(
                temperature = it.temperature.metric?.toSafeDouble(),
                windSpeed = it.windSpeed.metric?.toSafeDouble(),
                windDirection = WindDirection.toWindDirectionFromString(it.windDir),
                rain = 0.0, // NULL
                snowfall = null,
                uvIndex = null,
                pressureMsl = null,
                visibility = null,
                humidity = null,
                dewPoint = null,
                weatherCondition = EcccConditionMap.getCondition(it.iconCode),
                time = it.epochTime.secondsToMilliseconds(),
                precipitationProbability = it.precipProbability?.toIntOrNull()
            )
        },
        daily = daily.mapIndexed { index, it ->

            val time = dateToMillis(it.date, location.timezone)


            WeatherDaily(
                temperatureMin = dailyNight[index].temperature.metric?.toSafeDouble(),
                temperatureMax = it.temperature.metric?.toSafeDouble(),
                windSpeed = null,
                windDirection = null,
                rainSum = 0.0,
                snowfallSum = null,
                uvIndexMax = null,
                weatherCondition = EcccConditionMap.getCondition(it.iconCode),
                time = time,
                precipitationProbabilityMax = it.precipProbability?.toSafeDouble()?.roundToInt()
                    ?: getMaxPrecipitationProbability(hourly, time),
                sunrise = sunTimings[index].sunrise,
                sunset = sunTimings[index].sunset,
                moonrise = moonTimings[index].moonrise,
                moonset = moonTimings[index].moonset,
                moonPhase = moonTimings[index].phase,
                dawn = sunTimings[index].dawn,
                dusk = sunTimings[index].dusk,
                pressureMsl = null,
                visibility = null,
                humidity = null,
                dewPoint = null
            )
        }
    )
}

/**
 * ECCC dates have no year (e.g. "Fri, 1 Jan"), so use the year that puts the date closest to today.
 * The weekday is ignored
 * From https://github.com/PranshulGG/WeatherMaster/pull/1202 by https://github.com/GaijinSystems
 */
private fun dateToMillis(dateStr: String, zoneId: String): Long {

    val zone = safeZoneId(zoneId)
    val formatter = DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH)
    val today = LocalDate.now(zone)

    val monthDay = MonthDay.parse(dateStr.substringAfter(",").trim(), formatter)

    return (-1..1).map {
        monthDay.atYear(today.year + it)
    }.minBy { abs(ChronoUnit.DAYS.between(today, it)) }.atStartOfDay(zone).toInstant()
        .toEpochMilli()
}

private fun getMaxPrecipitationProbability(
    hourly: List<EcccHourlyWeatherItemJson>,
    time: Long
): Int? {
    val startIndex =
        hourly.indexOfFirst { it.epochTime.secondsToMilliseconds() >= time }.takeIf { it != -1 }
            ?: 0


    val data = hourly.drop(maxOf(0, startIndex - 1))
        .take(Source.FMI.hourlyAggregationLimitHours)

    if (data.map { it.precipProbability }.all { it == null }) {
        return null
    }

    val maxProbability = data.mapNotNull { it.precipProbability }.maxOf { it.toDouble() }

    return maxProbability.roundToInt()
}