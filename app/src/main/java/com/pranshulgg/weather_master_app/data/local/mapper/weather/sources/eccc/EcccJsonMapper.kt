package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.eccc

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
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
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale
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
            humidity = current.humidity?.toSafeDouble() ?: 0.0,
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
            time = current.timeStamp.iso8601TimestampToMilliseconds(),
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
                sunrise = sunTimings[index].sunrise ?: -0L,
                sunset = sunTimings[index].sunset ?: -0L,
                moonrise = moonTimings[index].moonrise ?: -0L,
                moonset = moonTimings[index].moonset ?: -0L,
                moonPhase = moonTimings[index].phase,
                dawn = sunTimings[index].dawn ?: 0L,
                dusk = sunTimings[index].dusk ?: 0L,
                pressureMsl = null,
                visibility = null,
                humidity = null,
                dewPoint = null
            )
        }
    )
}

private fun dateToMillis(dateStr: String, zoneId: String): Long {
    val dateWithYear = "$dateStr ${Year.now().value}"

    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)

    val localDate = LocalDate.parse(dateWithYear, formatter)

    return localDate.atStartOfDay(safeZoneId(zoneId)).toInstant().toEpochMilli()
}

private fun getMaxPrecipitationProbability(
    hourly: List<EcccHourlyWeatherItemJson>,
    time: Long
): Int? {
    val startIndex =
        hourly.indexOfFirst { it.epochTime.secondsToMilliseconds() >= time }.takeIf { it != -1 }
            ?: 0


    val data = hourly.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.FMI.hourlyAggregationLimitHours)

    if (data.map { it.precipProbability }.all { it == null }) {
        return null
    }

    val maxProbability = data.mapNotNull { it.precipProbability }.maxOf { it.toDouble() }

    return maxProbability.roundToInt()
}