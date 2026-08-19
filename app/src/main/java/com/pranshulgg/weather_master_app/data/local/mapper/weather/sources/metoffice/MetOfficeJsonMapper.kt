package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.metoffice

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.MetOfficeWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.json.MetOfficeHourlyForecastTimeSeriesJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.model.MetOfficeForecastJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.formatters.getCurrentTimeFor
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime
import kotlin.math.max
import kotlin.math.roundToInt


fun MetOfficeForecastJson.toDomain(location: Location): Weather {

    val timezone = location.timezone

    val currentTime = getCurrentTimeFor(timezone)

    val currentHourIndex = findHourlyIndexForTime(
        this.hourly.features[0].properties.timeSeries.map { it.time.iso8601TimestampToMilliseconds() },
        currentTime
    )


    val hourly = this.hourly.features[0].properties.timeSeries
    val daily = this.daily.features[0].properties.timeSeries

    val sunTimings = getSunTimings(
        daily.map {
            it.time.iso8601TimestampToMilliseconds().normalizeToDay(timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        daily.map {
            it.time.iso8601TimestampToMilliseconds().normalizeToDay(timezone)
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = hourly[currentHourIndex].screenTemperature,
            humidity = hourly[currentHourIndex].screenRelativeHumidity,
            windSpeed = WindSpeedUnit.MPS.convert(
                hourly[currentHourIndex].windSpeed10mMs,
                WindSpeedUnit.KPH
            ),
            windDirection = WindDirection.toWindDirectionFromDegrees(hourly[currentHourIndex].windDirectionFrom10m),
            pressureMsl = hourly[currentHourIndex].pressurePa?.div(100.0),
            visibility = hourly[currentHourIndex].visibilityM?.toInt(),
            cloudCover = null,
            uvIndex = hourly[currentHourIndex].uvIndex,
            weatherCondition = MetOfficeWeatherConditionMap.getCondition(hourly[currentHourIndex].significantWeatherCode),
            feelsLike = hourly[currentHourIndex].feelsLikeTemperature,
            time = currentTime,
            dewPoint = hourly[currentHourIndex].screenDewPointTemperature,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = hourly.map {
            WeatherHourly(
                temperature = it.screenTemperature,
                windSpeed = WindSpeedUnit.MPS.convert(
                    it.windSpeed10mMs,
                    WindSpeedUnit.KPH
                ),
                windDirection = WindDirection.toWindDirectionFromDegrees(it.windDirectionFrom10m),
                rain = it.totalPrecipAmount ?: 0.0,
                snowfall = it.totalSnowAmount,
                uvIndex = it.uvIndex,
                pressureMsl = it.pressurePa?.div(100.0),
                visibility = it.visibilityM?.toInt(),
                humidity = it.screenRelativeHumidity,
                dewPoint = it.screenDewPointTemperature,
                weatherCondition = MetOfficeWeatherConditionMap.getCondition(it.significantWeatherCode),
                time = it.time.iso8601TimestampToMilliseconds(),
                precipitationProbability = it.probOfPrecipitation.roundToInt()
            )
        },
        daily = daily.mapIndexed { index, day ->

            val avgWindSpeed = ((day.midday10mWindSpeedMs ?: 0.0)
                    + (day.midnight10mWindSpeedMs ?: 0.0)).div(2.0)

            val windDirection = when {
                day.midday10mWindSpeedMs == null ->
                    day.midnight10mWindDirection

                day.midnight10mWindSpeedMs == null ->
                    day.midday10mWindDirection

                day.midday10mWindSpeedMs >= day.midnight10mWindSpeedMs ->
                    day.midday10mWindDirection

                else ->
                    day.midnight10mWindDirection
            }

            val hourly = hourlyForDay(hourly, day.time.iso8601TimestampToMilliseconds())

            val rainSum = hourly.sumOf { it.totalPrecipAmount ?: 0.0 }
            val snowSum = hourly.sumOf { it.totalSnowAmount ?: 0.0 }
            val uvIndex = hourly.maxOf { it.uvIndex ?: -1.0 }.takeIf { it != -1.0 }

            val condition = computeDailyWeatherCondition(
                hourly.map { MetOfficeWeatherConditionMap.getCondition(it.significantWeatherCode) },
                MetOfficeWeatherConditionMap.getCondition(day.nightSignificantWeatherCode)
            )

            val precipitationProbabilityMax = max(
                day.nightProbabilityOfPrecipitation ?: -1.0,
                day.nightProbabilityOfPrecipitation ?: -1.0
            ).takeIf { it != -1.0 }

            val pressureAvg =
                (((day.middayMslpPa ?: 0) + (day.midnightMslpPa ?: 0)).div(2.0)).div(100)

            val humidityAvg =
                hourly.map { it.screenRelativeHumidity ?: -1.0 }.average().takeIf { it != -1.0 }
            val dewPointAvg =
                hourly.map { it.screenDewPointTemperature ?: -1.0 }.average().takeIf { it != -1.0 }

            WeatherDaily(
                temperatureMin = day.dayMaxScreenTemperature,
                temperatureMax = day.nightMinScreenTemperature,
                windSpeed = WindSpeedUnit.MPS.convert(
                    avgWindSpeed,
                    WindSpeedUnit.KPH
                ),
                windDirection = WindDirection.toWindDirectionFromDegrees(windDirection),
                rainSum = rainSum,
                snowfallSum = snowSum,
                uvIndexMax = uvIndex,
                weatherCondition = condition,
                time = day.time.iso8601TimestampToMilliseconds().normalizeToDay(timezone),
                precipitationProbabilityMax = precipitationProbabilityMax?.toInt(),
                pressureMsl = pressureAvg,
                visibility = hourly.minOf { it.visibilityM?.toDouble() ?: -1.0 }.toInt()
                    .takeIf { it != -1 },
                humidity = humidityAvg,
                dewPoint = dewPointAvg,
                sunrise = sunTimings[index].sunrise ?: -0L,
                sunset = sunTimings[index].sunset ?: -0L,
                moonrise = moonTimings[index].moonrise ?: -0L,
                moonset = moonTimings[index].moonset ?: -0L,
                moonPhase = moonTimings[index].phase,
                dawn = sunTimings[index].dawn ?: 0L,
                dusk = sunTimings[index].dusk ?: 0L,
            )
        }
    )
}

private fun hourlyForDay(
    data: List<MetOfficeHourlyForecastTimeSeriesJson>,
    time: Long
): List<MetOfficeHourlyForecastTimeSeriesJson> {
    val startIndex =
        data.indexOfFirst { it.time.iso8601TimestampToMilliseconds() >= time }.takeIf { it != -1 }
            ?: 0

    val data = data.toList().drop(maxOf(0, startIndex))
        .take(24)

    return data
}