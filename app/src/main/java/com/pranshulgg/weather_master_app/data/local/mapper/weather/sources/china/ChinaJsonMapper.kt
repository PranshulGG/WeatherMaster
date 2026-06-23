package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.china

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.china.ChinaWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.china.json.ChinaForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.china.json.ChinaHourlyForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.json.bundle.DwdWeatherJsonBundle
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.NwsWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsHourlyForecastPeriodsJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import kotlin.math.roundToInt


fun ChinaForecastJson.toDomain(location: Location): Weather {
    val current = this.current
    val hourly = this.forecastHourly
    val daily = this.forecastDaily

    val time = daily.pubTime.iso8601TimestampToMilliseconds()

    val msDay = 24L * 60 * 60 * 1000

    val sunTimings = getSunTimings(
        List(daily.temperature.value.size) {
            (time + (it * msDay)).normalizeToDay(location.timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        List(daily.temperature.value.size) {
            (time + (it * msDay)).normalizeToDay(location.timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = current.temperature.value.toSafeDouble(),
            humidity = current.humidity.value.toSafeDouble() ?: 0.0,
            windSpeed = current.wind.speed.value.toSafeDouble(),
            windDirection = WindDirection.toWindDirectionFromDegrees(
                current.wind.direction.value.toSafeDouble()?.roundToInt()
            ),
            pressureMsl = current.pressure.value.toSafeDouble(),
            visibility = DistanceUnit.KM.convert(
                current.visibility.value.toSafeDouble(),
                DistanceUnit.M
            )?.roundToInt(),
            cloudCover = null, // NOT USED IN THE APP
            uvIndex = current.uvIndex.toSafeDouble(),
            weatherCondition = ChinaWeatherConditionMap.getCondition(
                current.weather.toSafeDouble()?.toInt()
            ),
            feelsLike = current.feelsLike.value.toSafeDouble(),
            time = current.pubTime.iso8601TimestampToMilliseconds(),
            dewPoint = null,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = List(hourly.wind.value.size) {
            WeatherHourly(
                temperature = hourly.temperature.value[it].toDouble(),
                windSpeed = hourly.wind.value[it].speed.toSafeDouble(),
                windDirection = WindDirection.toWindDirectionFromDegrees(
                    hourly.wind.value[it].direction.toSafeDouble()?.roundToInt()
                ),
                rain = 0.0,
                snowfall = null,
                uvIndex = null,
                pressureMsl = null,
                visibility = null,
                humidity = null,
                dewPoint = null,
                weatherCondition = ChinaWeatherConditionMap.getCondition(hourly.weather.value[it]),
                time = hourly.wind.value[it].datetime.iso8601TimestampToMilliseconds(),
                precipitationProbability = null
            )
        },
        daily = List(daily.temperature.value.size) {

            val dailyTime = time + (it * msDay)
            val avgWindSpeed = listOf(
                daily.wind.speed.value[it].from.toSafeDouble() ?: -1.0,
                daily.wind.speed.value[it].to.toSafeDouble() ?: -1.0
            ).average()

            val windDirection = listOf(
                daily.wind.direction.value[it].from.toSafeDouble() ?: -1.0,
                daily.wind.direction.value[it].to.toSafeDouble() ?: -1.0
            ).average()


            WeatherDaily(
                temperatureMin = daily.temperature.value[it].min.toSafeDouble(),
                temperatureMax = daily.temperature.value[it].max.toSafeDouble(),
                windSpeed = if (avgWindSpeed >= 0.0) avgWindSpeed else null,
                windDirection = WindDirection.toWindDirectionFromDegrees(windDirection.roundToInt()),
                rainSum = 0.0,
                snowfallSum = null,
                uvIndexMax = null,
                weatherCondition = ChinaWeatherConditionMap.getCondition(
                    daily.weather.value[it].from?.toSafeDouble()?.toInt()
                ),
                time = dailyTime.normalizeToDay(location.timezone),
                precipitationProbabilityMax = daily.precipitationProbability.value.getOrNull(it)
                    .toSafeDouble()
                    ?.roundToInt(),
                pressureMsl = null,
                visibility = null,
                humidity = null,
                dewPoint = null,
                sunrise = sunTimings[it].sunrise ?: -1L,
                sunset = sunTimings[it].sunset ?: -1L,
                moonrise = moonTimings[it].moonrise ?: -1L,
                moonset = moonTimings[it].moonset ?: -1L,
                moonPhase = moonTimings[it].phase,
                dawn = sunTimings[it].dawn ?: -1L,
                dusk = sunTimings[it].dusk ?: -1L
            )

        }
    )
}
