package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.pirateweather

import com.pranshulgg.weather_master_app.core.model.astro.SunTimings
import com.pranshulgg.weather_master_app.core.model.astro.MoonTimings
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.PirateWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.json.PirateWeatherCurrentJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.json.PirateWeatherDailyDataJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.json.PirateWeatherDataPointJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.json.PirateWeatherJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import kotlin.math.roundToInt

fun PirateWeatherJson.toDomain(location: Location): Weather {
    val utcOffsetSeconds = (offset * 3600).toLong()

    val dailyTimesMilli = daily.data.map { it.time.secondsToMilliseconds() }

    val sunTimings = getSunTimings(
        dailyTimesMilli,
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        dailyTimesMilli,
        location.timezone,
        location.latitude,
        location.longitude
    )

    return Weather(
        location = location,
        current = currently.toWeatherCurrent(utcOffsetSeconds),
        hourly = hourly.data.take(168).map { it.toWeatherHourly() },
        daily = daily.data.take(7).mapIndexed { index, it ->
            it.toWeatherDaily(index, sunTimings, moonTimings)
        }
    )
}

private fun PirateWeatherCurrentJson.toWeatherCurrent(
    utcOffsetSeconds: Long
): WeatherCurrent {
    return WeatherCurrent(
        temperature = temperature,
        humidity = (humidity * 100),
        windSpeed = windSpeed.times(3.6),
        windDirection = WindDirection.toWindDirectionFromDegrees(windBearing),
        pressureMsl = pressure,
        visibility = (visibility * 1000).roundToInt(),
        cloudCover = cloudCover,
        uvIndex = uvIndex,
        weatherCondition = PirateWeatherConditionMap.getCondition(icon),
        feelsLike = apparentTemperature,
        time = time.secondsToMilliseconds(),
        dewPoint = dewPoint,
        utcOffsetSeconds = utcOffsetSeconds,
        lastUpdatedInMilli = System.currentTimeMillis()
    )
}

private fun PirateWeatherDataPointJson.toWeatherHourly(): WeatherHourly {
    val (rain, snow) = separatePrecipitation(precipIntensity ?: 0.0, precipType)

    return WeatherHourly(
        temperature = temperature,
        windSpeed = windSpeed?.times(3.6),
        windDirection = WindDirection.toWindDirectionFromDegrees(windBearing),
        rain = rain,
        snowfall = snow,
        uvIndex = uvIndex,
        pressureMsl = pressure,
        visibility = visibility?.let { (it * 1000).roundToInt() },
        humidity = (humidity?.times(100)),
        dewPoint = dewPoint,
        weatherCondition = PirateWeatherConditionMap.getCondition(icon),
        time = time.secondsToMilliseconds(),
        precipitationProbability = (precipProbability?.times(100))?.roundToInt()
    )
}

private fun PirateWeatherDailyDataJson.toWeatherDaily(
    index: Int,
    sunTimings: List<SunTimings>,
    moonTimings: List<MoonTimings>
): WeatherDaily {
    val (rain, snow) = separatePrecipitation(precipAccumulation ?: 0.0, precipType)

    return WeatherDaily(
        temperatureMin = temperatureMin ?: temperatureLow,
        temperatureMax = temperatureMax ?: temperatureHigh,
        windSpeed = windSpeed?.times(3.6),
        windDirection = WindDirection.toWindDirectionFromDegrees(windBearing),
        rainSum = rain,
        snowfallSum = snow,
        uvIndexMax = uvIndex,
        weatherCondition = PirateWeatherConditionMap.getCondition(icon),
        time = time.secondsToMilliseconds(),
        precipitationProbabilityMax = (precipProbability?.times(100))?.roundToInt(),
        sunrise = sunTimings[index].sunrise ?: 0L,
        sunset = sunTimings[index].sunset ?: 0L,
        moonrise = moonTimings[index].moonrise ?: 0L,
        moonset = moonTimings[index].moonset ?: 0L,
        moonPhase = moonTimings[index].phase,
        dawn = sunTimings[index].dawn ?: 0L,
        dusk = sunTimings[index].dusk ?: 0L,
        pressureMsl = pressure,
        visibility = visibility?.let { (it * 1000).roundToInt() },
        humidity = (humidity?.times(100)),
        dewPoint = dewPoint
    )
}

private fun separatePrecipitation(amount: Double, type: String?): Pair<Double, Double?> {
    return when (type) {
        "rain" -> Pair(amount, null)
        "snow" -> Pair(0.0, amount / 10.0)
        "sleet" -> Pair(amount / 2, amount / 20.0)
        else -> Pair(0.0, null)
    }
}
