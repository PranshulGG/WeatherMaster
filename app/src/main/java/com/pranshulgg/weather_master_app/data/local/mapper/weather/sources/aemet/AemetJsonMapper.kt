package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.aemet

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.AemetWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json.AemetHourlyDayJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json.AemetHourlyEntryJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json.toSafeDouble
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.model.AemetForecastJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.formatters.getCurrentTimeFor
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.computing.computeDailyWeatherCondition
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime
import java.time.LocalDate

// AEMET reports wind speed in km/h already, matching this app's canonical unit - no conversion needed.

private data class AemetHourPoint(
    val time: Long,
    val condition: WeatherCondition,
    val temperature: Double?,
    val feelsLike: Double?,
    val humidity: Double?,
    val rain: Double,
    val snowfall: Double?,
    val precipitationProbability: Int?,
    val windDirection: WindDirection?,
    val windSpeed: Double?
)

fun AemetForecastJson.toDomain(location: Location): Weather {

    val timezone = location.timezone
    val currentTime = getCurrentTimeFor(timezone)

    val hourPoints = hourly.prediccion?.dia.orEmpty()
        .flatMap { day -> buildHourPointsForDay(day, timezone) }
        .sortedBy { it.time }

    val hourPointsByDay = hourPoints.groupBy { it.time.normalizeToDay(timezone) }

    val currentIndex = findHourlyIndexForTime(hourPoints.map { it.time }, currentTime)
    val current = hourPoints.getOrNull(currentIndex)

    val dailyEntries = daily.prediccion?.dia.orEmpty()

    // map (not mapNotNull) to keep this positionally aligned with dailyEntries below,
    // since sunTimings/moonTimings are indexed by position, not matched by date
    val dailyDates = dailyEntries.map { aemetDateToMillis(it.fecha, timezone) ?: 0L }

    val sunTimings = getSunTimings(dailyDates, timezone, location.latitude, location.longitude)
    val moonTimings = getMoonTimings(dailyDates, timezone, location.latitude, location.longitude)

    return Weather(
        location = location,
        current = WeatherCurrent(
            temperature = current?.temperature,
            humidity = current?.humidity,
            windSpeed = current?.windSpeed,
            windDirection = current?.windDirection,
            pressureMsl = null,
            visibility = null,
            cloudCover = null,
            uvIndex = null,
            weatherCondition = current?.condition ?: WeatherCondition.NO_CONDITION_FOUND,
            feelsLike = current?.feelsLike,
            time = currentTime,
            dewPoint = null,
            utcOffsetSeconds = null,
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = hourPoints.map {
            WeatherHourly(
                temperature = it.temperature,
                windSpeed = it.windSpeed,
                windDirection = it.windDirection,
                rain = it.rain,
                snowfall = it.snowfall,
                uvIndex = null,
                pressureMsl = null,
                visibility = null,
                humidity = it.humidity,
                dewPoint = null,
                weatherCondition = it.condition,
                time = it.time,
                precipitationProbability = it.precipitationProbability
            )
        },
        daily = dailyEntries.mapIndexedNotNull { index, day ->

            val dayTime = aemetDateToMillis(day.fecha, timezone) ?: return@mapIndexedNotNull null
            val dayHours = hourPointsByDay[dayTime].orEmpty()

            val rainSum = dayHours.sumOf { it.rain }
            val snowSum = dayHours.sumOf { it.snowfall ?: 0.0 }

            val condition = computeDailyWeatherCondition(
                dayHours.map { it.condition },
                dayHours.lastOrNull()?.condition ?: WeatherCondition.NO_CONDITION_FOUND
            )

            val windEntry = day.viento.orEmpty()
                .mapNotNull { w ->
                    val speed = w.velocidad.toSafeDouble()
                    if (speed != null && speed > 0 && !w.direccion.isNullOrBlank()) w to speed else null
                }
                .maxByOrNull { it.second }

            val precipitationProbabilityMax = day.probPrecipitacion.orEmpty()
                .mapNotNull { it.value.toSafeDouble() }
                .maxOrNull()

            val humidityAvg = listOfNotNull(
                day.humedadRelativa?.maxima.toSafeDouble(),
                day.humedadRelativa?.minima.toSafeDouble()
            ).takeIf { it.isNotEmpty() }?.average()

            WeatherDaily(
                temperatureMin = day.temperatura?.minima.toSafeDouble(),
                temperatureMax = day.temperatura?.maxima.toSafeDouble(),
                windSpeed = windEntry?.second,
                windDirection = aemetWindDirectionFromString(windEntry?.first?.direccion),
                rainSum = rainSum,
                snowfallSum = snowSum,
                uvIndexMax = day.uvMax?.toDouble(),
                weatherCondition = condition,
                time = dayTime,
                precipitationProbabilityMax = precipitationProbabilityMax?.toInt(),
                pressureMsl = null,
                visibility = null,
                humidity = humidityAvg,
                dewPoint = null,
                sunrise = sunTimings.getOrNull(index)?.sunrise ?: 0L,
                sunset = sunTimings.getOrNull(index)?.sunset ?: 0L,
                moonrise = moonTimings.getOrNull(index)?.moonrise ?: 0L,
                moonset = moonTimings.getOrNull(index)?.moonset ?: 0L,
                moonPhase = moonTimings.getOrNull(index)?.phase
                    ?: com.pranshulgg.weather_master_app.core.model.astro.MoonPhase.NEW_MOON,
                dawn = sunTimings.getOrNull(index)?.dawn ?: 0L,
                dusk = sunTimings.getOrNull(index)?.dusk ?: 0L
            )
        }
    )
}

private fun buildHourPointsForDay(day: AemetHourlyDayJson, timezone: String): List<AemetHourPoint> {
    val conditionByHour = day.estadoCielo.orEmpty().byPeriodo()
    val temperatureByHour = day.temperatura.orEmpty().byPeriodo()
    val feelsLikeByHour = day.sensTermica.orEmpty().byPeriodo()
    val humidityByHour = day.humedadRelativa.orEmpty().byPeriodo()
    val precipitationByHour = day.precipitacion.orEmpty().byPeriodo()
    val precipitationProbabilityByHour = day.probPrecipitacion.orEmpty().byPeriodo()
    val snowByHour = day.nieve.orEmpty().byPeriodo()
    val windByHour = day.vientoAndRachaMax.orEmpty()
        .filter { !it.direccion.isNullOrEmpty() && !it.velocidad.isNullOrEmpty() && it.periodo != null }
        .associateBy { it.periodo!! }

    val hours = conditionByHour.keys +
            temperatureByHour.keys +
            precipitationByHour.keys +
            windByHour.keys

    return hours.mapNotNull { hour ->
        val time =
            aemetDateToMillis(day.fecha, timezone, hour.toIntOrNull() ?: return@mapNotNull null)
                ?: return@mapNotNull null

        val wind = windByHour[hour]

        AemetHourPoint(
            time = time,
            condition = AemetWeatherConditionMap.getCondition(conditionByHour[hour]?.value),
            temperature = temperatureByHour[hour]?.value.toSafeDouble(),
            feelsLike = feelsLikeByHour[hour]?.value.toSafeDouble(),
            humidity = humidityByHour[hour]?.value.toSafeDouble(),
            rain = precipitationByHour[hour]?.value.toSafeDouble() ?: 0.0,
            snowfall = snowByHour[hour]?.value.toSafeDouble(),
            precipitationProbability = precipitationProbabilityByHour[hour]?.value.toSafeDouble()
                ?.toInt(),
            windDirection = aemetWindDirectionFromString(wind?.direccion?.firstOrNull()),
            windSpeed = wind?.velocidad?.firstOrNull().toSafeDouble()
        )
    }
}

private fun List<AemetHourlyEntryJson>.byPeriodo(): Map<String, AemetHourlyEntryJson> =
    filter { it.periodo != null }.associateBy { it.periodo!! }

private fun aemetDateToMillis(fecha: String?, zoneId: String, hourOfDay: Int = 0): Long? {
    if (fecha.isNullOrBlank()) return null

    return try {
        LocalDate.parse(fecha.substringBefore("T"))
            .atStartOfDay(safeZoneId(zoneId))
            .plusHours(hourOfDay.toLong())
            .toInstant()
            .toEpochMilli()
    } catch (e: Exception) {
        null
    }
}

// AEMET uses Spanish abbreviations (SO/O/NO/C) rather than the SW/W/NW used elsewhere in the app.
private fun aemetWindDirectionFromString(value: String?): WindDirection? {
    return when (value?.trim()?.uppercase()) {
        "N" -> WindDirection.N
        "NE" -> WindDirection.NE
        "E" -> WindDirection.E
        "SE" -> WindDirection.SE
        "S" -> WindDirection.S
        "SO" -> WindDirection.SW
        "O" -> WindDirection.W
        "NO" -> WindDirection.NW
        else -> null // "C" (calm), blank, or unrecognized
    }
}
