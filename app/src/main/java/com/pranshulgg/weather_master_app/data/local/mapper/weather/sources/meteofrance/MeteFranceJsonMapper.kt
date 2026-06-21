package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.meteofrance

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.MeteoFranceConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.json.MeteoFranceForecastItemJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.json.MeteoFranceForecastJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.calculations.computeApparentTemperature
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime


fun MeteoFranceForecastJson.toDomain(location: Location): Weather {
    val forecast = this.properties.forecast
    val daily =
        computeDaily(
            forecast.filter { it.temperature != null },
            location
        ) // We don't use "this.properties.daily" cuz too complicated

    val currentHour = findHourlyIndexForTime(
        forecast.map { it.time.secondsToMilliseconds() }
    )

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = forecast[currentHour].temperature,
            humidity = forecast[currentHour].humidity?.toDouble() ?: 0.0,
            windSpeed = WindSpeedUnit.MPS.convert(
                forecast[currentHour].windSpeed?.toDouble(),
                WindSpeedUnit.KPH
            ),
            windDirection = WindDirection.toWindDirectionFromDegrees(forecast[currentHour].windDirection),
            pressureMsl = forecast[currentHour].pressureMsl,
            visibility = null,
            cloudCover = null, // NOT USED IN THE APP
            uvIndex = null, // Only daily
            weatherCondition = MeteoFranceConditionMap.getCondition(forecast[currentHour].icon),
            feelsLike = computeApparentTemperature(
                forecast[currentHour].temperature, forecast[currentHour].humidity?.toDouble(),
                forecast[currentHour].windSpeed?.toDouble()
            ),
            time = forecast[currentHour].time.secondsToMilliseconds(),
            dewPoint = null,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = forecast.filter { it.temperature != null }.map {
            WeatherHourly(
                temperature = it.temperature,
                windSpeed = WindSpeedUnit.MPS.convert(
                    it.windSpeed?.toDouble(),
                    WindSpeedUnit.KPH
                ),
                windDirection = WindDirection.toWindDirectionFromDegrees(it.windDirection),
                rain = it.rain ?: 0.0,
                snowfall = it.snow,
                uvIndex = null,
                pressureMsl = it.pressureMsl,
                visibility = null,
                humidity = it.humidity?.toDouble(),
                dewPoint = null,
                weatherCondition = MeteoFranceConditionMap.getCondition(it.icon),
                time = it.time.secondsToMilliseconds(),
                precipitationProbability = null
            )
        },
        daily = daily
    )
}

private fun computeDaily(
    data: List<MeteoFranceForecastItemJson>,
    location: Location
): List<WeatherDaily> {


    val zoneId = location.timezone

    val groupedByDay = data.groupBy {
        it.time.secondsToMilliseconds().normalizeToDay(zoneId)

    }

    val sunTimings = getSunTimings(
        groupedByDay.map {
            it.key
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        groupedByDay.map {
            it.key
        },
        location.timezone,
        location.latitude,
        location.longitude
    )

    return groupedByDay.map { dailyIt ->

        val minTemperature = dailyIt.value.mapNotNull { it.temperature }.min()
        val maxTemperature = dailyIt.value.mapNotNull { it.temperature }.max()
        val windSpeed = dailyIt.value
            .mapNotNull { it.windSpeed }
            .maxOrNull()

        val windDirection =
            dailyIt.value.mapNotNull { it.windDirection }.maxOrNull()

        val rainSum =
            dailyIt.value.sumOf { it.rain ?: 0.0 }

        val snowSum =
            dailyIt.value.sumOf { it.snow ?: 0.0 }

        val time = dailyIt.key
        val icon = dailyIt.value.map { it.icon }.groupingBy { it }
            .eachCount().entries.maxByOrNull { it.value }


        val condition = computeDailyWeatherCondition(
            getHourlyConditionsForDay(dailyIt.value, time),
            MeteoFranceConditionMap.getCondition(icon?.key)
        )


        val index = groupedByDay.keys.indexOf(dailyIt.key)

        val avgHumidity = dailyIt.value.map { it.humidity?.toDouble() ?: -1.0 }.average()
        val avgPressure = dailyIt.value.map { it.pressureMsl ?: -1.0 }.average()



        WeatherDaily(
            temperatureMin = minTemperature,
            temperatureMax = maxTemperature,
            windSpeed = WindSpeedUnit.MPS.convert(
                windSpeed?.toDouble(),
                WindSpeedUnit.KPH
            ),
            windDirection = WindDirection.toWindDirectionFromDegrees(windDirection),
            rainSum = rainSum,
            snowfallSum = snowSum,
            uvIndexMax = null,
            weatherCondition = condition,
            time = time,
            precipitationProbabilityMax = null,
            sunrise = sunTimings[index].sunrise ?: -0L,
            sunset = sunTimings[index].sunset ?: -0L,
            moonrise = moonTimings[index].moonrise ?: -0L,
            moonset = moonTimings[index].moonset ?: -0L,
            moonPhase = moonTimings[index].phase,
            dawn = sunTimings[index].dawn ?: 0L,
            dusk = sunTimings[index].dusk ?: 0L,
            pressureMsl = avgPressure,
            visibility = null,
            humidity = avgHumidity,
            dewPoint = null
        )
    }
}

private fun getHourlyConditionsForDay(
    data: List<MeteoFranceForecastItemJson>,
    time: Long
): List<WeatherCondition> {
    val startIndex =
        data.indexOfFirst { it.time >= time }
            .takeIf { it != -1 } ?: 0


    val conditions = data.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.METEO_FRANCE.hourlyAggregationLimitHours)
        .map {
            MeteoFranceConditionMap.getCondition(it.icon)
        }

    return conditions
}

