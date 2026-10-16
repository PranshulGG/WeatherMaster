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
import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.json.EcccDailyWeatherItemJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.json.EcccHourlyWeatherItemJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.json.EcccWeatherJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.formatters.getCurrentTimeFor
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


    val isNight = { item: EcccDailyWeatherItemJson ->
        item.periodLabel == "Night" || item.periodLabel == "Tonight"
    }

    val today = getCurrentTimeFor(location.timezone).normalizeToDay(location.timezone)

    /**
     * ECCC drops a day's daytime period once it has passed, so in the evening the forecast starts
     * with "Tonight". Pair day and night periods by date (not by position, which shifted every low
     * by a day) and keep today with only its night period, otherwise today is missing from daily.
     * Days without a night period (the last one) are skipped like before
     */
    val daily = this.dailyFcst.daily
        // A period whose date can't be parsed is skipped instead of failing the whole forecast
        .mapNotNull { period ->
            runCatching { dateToMillis(period.date, location.timezone) }.getOrNull()
                ?.let { it to period }
        }
        .groupBy({ it.first }, { it.second })
        .mapNotNull { (time, periods) ->
            // If a date ever has two night periods, use the later one (the night that follows the day)
            val night = periods.lastOrNull(isNight) ?: return@mapNotNull null
            val day = periods.firstOrNull { !isNight(it) }

            if (day == null && time != today) null else Triple(time, day, night)
        }

    // Only needed once today's daytime period is gone: highest temperature observed or still forecast today
    val todayHigh by lazy {
        val isToday = { millis: Long -> millis.normalizeToDay(location.timezone) == today }

        listOf(
            this.pastHourly?.hours.orEmpty().filterNotNull()
                .filter { item ->
                    item.timeStamp?.let { runCatching { isToday(it.iso8601TimestampToMilliseconds()) }.getOrNull() } == true
                }
                .mapNotNull { it.temperature.toSafeDouble() },
            hourly
                .filter { isToday(it.epochTime.secondsToMilliseconds()) }
                .mapNotNull { it.temperature.metric?.toSafeDouble() },
            listOfNotNull(current.temperature.metric?.toSafeDouble())
        ).flatten().maxOrNull()
    }

    val sunTimings = getSunTimings(
        daily.map { it.first },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        daily.map { it.first },
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
        daily = daily.mapIndexed { index, (time, day, night) ->

            val period = day ?: night


            WeatherDaily(
                temperatureMin = night.temperature.metric?.toSafeDouble(),
                temperatureMax = if (day != null) day.temperature.metric?.toSafeDouble() else todayHigh,
                windSpeed = null,
                windDirection = null,
                rainSum = 0.0,
                snowfallSum = null,
                uvIndexMax = null,
                weatherCondition = EcccConditionMap.getCondition(period.iconCode),
                time = time,
                precipitationProbabilityMax = period.precipProbability?.toSafeDouble()?.roundToInt()
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