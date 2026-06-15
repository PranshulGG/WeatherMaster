package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.fmi

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.astro.MoonPhase
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.DwdWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.json.DwdWeatherForecastDataJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.FmiConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.model.FmiWeather
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.model.FmiWeatherMember
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.MetNorwayWeatherConditionMap
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.iso8601TimestampToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.calculations.computeApparentTemperature
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime
import kotlin.collections.firstOrNull
import kotlin.math.roundToInt
import kotlin.text.toDouble


fun FmiWeather.toDomain(location: Location): Weather {

    val forecast = this.data.groupBy { it.time!!.iso8601TimestampToMilliseconds() }
    val current = if (!this.observation.isNullOrEmpty()) {
        this.observation.groupBy { it.parameterName }
    } else {
        null
    }

    Log.d("HEREEE", current.toString())


    val currentHour = findHourlyIndexForTime(
        time = forecast.keys.map { it }
    )

    val hourDataForParam: (Long, String) -> Double? = { hour, param ->
        forecast[hour]?.firstOrNull { data -> data.parameterName == param }?.parameterValue?.toDouble()
    }

    val currentDataForParam: (String) -> Double? = {
        if (current != null) {
            current[it]?.get(0)?.parameterValue?.toDouble()
        } else {
            val correctKey = when (it) {
                "t2m" -> "Temperature"
                "ws_10min" -> "WindSpeedMS"
                "wd_10min" -> "WindDirection"
                "rh" -> "Humidity"
                "td" -> "DewPoint"
                "p_sea" -> "Pressure"
                "vis" -> "Visibility"
                "wawa" -> "WeatherSymbol3"
                else -> ""
            }
            forecast[forecast.keys.sorted()
                .getOrNull(currentHour)]?.firstOrNull { data -> data.parameterName == correctKey }?.parameterValue?.toDouble()
        }
    }


//    val currentTemperature = currentData?.firstOrNull{it.parameterName == "Temperature"}?.parameterValue?.toDouble()
//    val currentHumidity = forecast["Humidity"]?.get(currentHour)?.parameterName
//    val currentVisibility = forecast["Visibility"]?.get(currentHour)?.parameterName
//    val currentWindDirection = forecast["WindDirection"]?.get(currentHour)?.parameterName
//    val currentPressure = forecast["Pressure"]?.get(currentHour)?.parameterName


    val daily = computeDaily(this.data, location)



    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = currentDataForParam("t2m"),
            humidity = currentDataForParam("rh") ?: 0.0,
            windSpeed = WindSpeedUnit.MPS.convert(
                currentDataForParam("ws_10min"),
                WindSpeedUnit.KPH
            ),
            windDirection = WindDirection.toWindDirectionFromDegrees(currentDataForParam("wd_10min")?.toInt()),
            pressureMsl = currentDataForParam("p_sea"),
            visibility = currentDataForParam("vis")?.roundToInt(),
            cloudCover = null,
            uvIndex = null,
            weatherCondition = if (currentDataForParam("wawa") == 0.0) FmiConditionMap.getCondition(
                hourDataForParam(
                    forecast.keys.sorted()[currentHour],
                    "WeatherSymbol3"
                )?.toInt()
            ) else FmiConditionMap.getCurrentCondition(currentDataForParam("wawa")?.toInt()),
            feelsLike = computeApparentTemperature(
                currentDataForParam("t2m"),
                currentDataForParam("rh"),
                currentDataForParam("ws_10min")
            ),
            time = if (current != null) current["t2m"]?.get(0)!!.time!!.iso8601TimestampToMilliseconds() else forecast.keys.sorted()[currentHour],
            dewPoint = currentDataForParam("td"),
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = forecast.keys.mapIndexed { index, time ->

            WeatherHourly(
                temperature = hourDataForParam(time, "Temperature"),
                windSpeed = WindSpeedUnit.MPS.convert(
                    hourDataForParam(time, "WindSpeedMS"),
                    WindSpeedUnit.KPH
                ),
                windDirection = WindDirection.toWindDirectionFromDegrees(
                    hourDataForParam(
                        time,
                        "WindDirection"
                    )?.toInt()
                ),
                rain = hourDataForParam(time, "Precipitation1h") ?: 0.0,
                snowfall = null,
                uvIndex = null,
                pressureMsl = hourDataForParam(time, "Pressure"),
                visibility = hourDataForParam(time, "Visibility")?.roundToInt(),
                humidity = hourDataForParam(time, "Humidity"),
                dewPoint = hourDataForParam(time, "DewPoint"),
                weatherCondition = FmiConditionMap.getCondition(
                    hourDataForParam(
                        time,
                        "WeatherSymbol3"
                    )?.toInt()
                ),
                time = time,
                precipitationProbability = hourDataForParam(time, "PoP")?.roundToInt()
            )
        },
        daily = daily
    )
}

private fun computeDaily(
    data: List<FmiWeatherMember>,
    location: Location
): List<WeatherDaily> {


    val zoneId = location.timezone

    val groupedByDay = data.groupBy {
        it.time!!.iso8601TimestampToMilliseconds().normalizeToDay(zoneId)

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

//    val dataForParam: (Long) -> List<FmiWeatherMember>? = { time ->
//        groupedByDay[time]
//    }


    val dataForParam: (Long, String) -> List<FmiWeatherMember>? = { hour, param ->
        groupedByDay[hour]?.filter { it.parameterName == param } ?: emptyList()
    }
    return groupedByDay.map { dailyIt ->

        val minTemperature =
            dataForParam(dailyIt.key, "Temperature")?.mapNotNull { it.parameterValue?.toDouble() }
                ?.minOf { it }
        val maxTemperature =
            dataForParam(dailyIt.key, "Temperature")?.mapNotNull { it.parameterValue?.toDouble() }
                ?.maxOf { it }


        val windDirection =
            dataForParam(dailyIt.key, "WindDirection")?.mapNotNull { it.parameterValue }
                ?.maxOrNull()

        val rainSum = dataForParam(dailyIt.key, "Precipitation1h")?.sumOf {
            it.parameterValue?.toDouble() ?: 0.0
        } ?: 0.0

        val windSpeed = dataForParam(dailyIt.key, "WindSpeedMS")
            ?.mapNotNull { it.parameterValue?.toDouble() }
            ?.maxOrNull()


        val icon = dataForParam(dailyIt.key, "WeatherSymbol3")?.map { it.parameterValue }
            ?.groupingBy { it }
            ?.eachCount()?.entries?.maxByOrNull { it.value }


        val condition = computeDailyWeatherCondition(
            getHourlyConditionsForDay(dataForParam(dailyIt.key, "WeatherSymbol3")!!, dailyIt.key),
            FmiConditionMap.getCondition(icon?.key?.toDouble()?.toInt())
        )

        val precipitationProbabilityMax =
            dataForParam(dailyIt.key, "PoP")?.mapNotNull { it.parameterValue?.toDouble()?.toInt() }
                ?.maxOrNull()


        val index = groupedByDay.keys.indexOf(dailyIt.key)

        WeatherDaily(
            temperatureMin = minTemperature,
            temperatureMax = maxTemperature,
            windSpeed = windSpeed,
            windDirection = WindDirection.toWindDirectionFromDegrees(
                windDirection?.toDouble()?.toInt()
            ),
            rainSum = rainSum,
            snowfallSum = null,
            uvIndexMax = null,
            weatherCondition = condition,
            time = dailyIt.key,
            precipitationProbabilityMax = precipitationProbabilityMax,
            sunrise = sunTimings[index].sunrise ?: -0L,
            sunset = sunTimings[index].sunset ?: -0L,
            moonrise = moonTimings[index].moonrise ?: -0L,
            moonset = moonTimings[index].moonset ?: -0L,
            moonPhase = moonTimings[index].phase,
            dawn = sunTimings[index].dawn ?: 0L,
            dusk = sunTimings[index].dusk ?: 0L
        )
    }
}

private fun getHourlyConditionsForDay(
    data: List<FmiWeatherMember>,
    time: Long
): List<WeatherCondition> {
    val startIndex =
        data.indexOfFirst { it.time!!.iso8601TimestampToMilliseconds() >= time }
            .takeIf { it != -1 } ?: 0


    val conditions = data.drop(maxOf(0, startIndex - 1))
        .take(WeatherSource.FMI.hourlyAggregationLimitHours)
        .map {
            FmiConditionMap.getCondition(it.parameterValue?.toDouble()?.toInt())
        }

    return conditions
}
