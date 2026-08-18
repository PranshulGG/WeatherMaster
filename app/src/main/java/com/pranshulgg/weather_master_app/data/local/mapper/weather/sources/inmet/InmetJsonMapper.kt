package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.inmet

import com.pranshulgg.weather_master_app.core.model.astro.MoonPhase
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.InmetWeatherConditionMap
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json.InmetDayJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json.InmetHourlyEntryJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json.InmetPeriodJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.normalizeToDay
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weather_master_app.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weather_master_app.core.utils.weather.calculations.computeApparentTemperature
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


data class InmetWeatherBundle(
    val forecast: Map<String, Map<String, InmetDayJson>>,
    val hourlyObservations: List<InmetHourlyEntryJson>?
)


fun InmetWeatherBundle.toDomain(location: Location): Weather {

    val ibgeCode = forecast.keys.firstOrNull()
    val days = ibgeCode?.let { forecast[it] } ?: emptyMap()

    val zone = safeZoneId(location.timezone)

    val dailyEntries = mutableListOf<WeatherDaily>()
    val hourlyEntries = mutableListOf<WeatherHourly>()

    val dayTimeMillis = mutableListOf<Long>()

    for ((dateStr, dayJson) in days) {
        val dayMillis = parseInmetDate(dateStr, zone)
        dayTimeMillis.add(dayMillis)

        val periods = listOfNotNull(dayJson.manha, dayJson.tarde, dayJson.noite)

        if (periods.isNotEmpty()) {
            for ((periodJson, hourOffset) in listOf(
                dayJson.manha to 9,
                dayJson.tarde to 15,
                dayJson.noite to 21
            )) {
                periodJson?.let {
                    hourlyEntries.add(
                        createHourlyFromPeriod(it, dayMillis, hourOffset, zone)
                    )
                }
            }

            val tempMin = periods.minOfOrNull { it.tempMin ?: Int.MAX_VALUE }
                ?.takeIf { it != Int.MAX_VALUE }?.toDouble()
            val tempMax = periods.maxOfOrNull { it.tempMax ?: Int.MIN_VALUE }
                ?.takeIf { it != Int.MIN_VALUE }?.toDouble()

            val windSpeed = periods.mapNotNull { parseWindIntensityToKmh(it.intVento) }.averageOrNull()
            val windDir = periods.firstNotNullOfOrNull { parseWindDirection(it.dirVento) }

            val condition = periods.firstNotNullOfOrNull {
                InmetWeatherConditionMap.getCondition(it.codIcone, it.resumo)
            } ?: WeatherCondition.NO_CONDITION_FOUND

            dailyEntries.add(
                WeatherDaily(
                    temperatureMin = tempMin,
                    temperatureMax = tempMax,
                    windSpeed = windSpeed,
                    windDirection = windDir,
                    rainSum = 0.0,
                    snowfallSum = null,
                    uvIndexMax = null,
                    weatherCondition = condition,
                    time = dayMillis,
                    precipitationProbabilityMax = null,
                    pressureMsl = null,
                    visibility = null,
                    humidity = null,
                    dewPoint = null,
                    sunrise = -1L,
                    sunset = -1L,
                    moonrise = -1L,
                    moonset = -1L,
                    moonPhase = MoonPhase.NEW_MOON,
                    dawn = -1L,
                    dusk = -1L
                )
            )
        } else {
            val tempMin = dayJson.tempMin?.toDouble()
            val tempMax = dayJson.tempMax?.toDouble()
            val windSpeed = parseWindIntensityToKmh(dayJson.intVento)
            val windDir = parseWindDirection(dayJson.dirVento)
            val condition = InmetWeatherConditionMap.getCondition(dayJson.codIcone, dayJson.resumo)

            dailyEntries.add(
                WeatherDaily(
                    temperatureMin = tempMin,
                    temperatureMax = tempMax,
                    windSpeed = windSpeed,
                    windDirection = windDir,
                    rainSum = 0.0,
                    snowfallSum = null,
                    uvIndexMax = null,
                    weatherCondition = condition,
                    time = dayMillis,
                    precipitationProbabilityMax = null,
                    pressureMsl = null,
                    visibility = null,
                    humidity = null,
                    dewPoint = null,
                    sunrise = -1L,
                    sunset = -1L,
                    moonrise = -1L,
                    moonset = -1L,
                    moonPhase = MoonPhase.NEW_MOON,
                    dawn = -1L,
                    dusk = -1L
                )
            )
        }
    }

    if (hourlyObservations != null && hourlyObservations.isNotEmpty()) {
        hourlyEntries.clear()
        hourlyEntries.addAll(hourlyObservations.map { createHourlyFromObservation(it, zone) })
    }

    val current = if (hourlyObservations != null && hourlyObservations.isNotEmpty()) {
        createCurrentFromObservation(hourlyObservations.last(), location)
    } else {
        createCurrentFromForecast(days, zone, location)
    }

    val sunTimings = getSunTimings(
        dayTimeMillis,
        location.timezone,
        location.latitude,
        location.longitude
    )
    val moonTimings = getMoonTimings(
        dayTimeMillis,
        location.timezone,
        location.latitude,
        location.longitude
    )

    dailyEntries.forEachIndexed { index, day ->
        if (index < sunTimings.size && index < moonTimings.size) {
            val sun = sunTimings[index]
            val moon = moonTimings[index]
            dailyEntries[index] = day.copy(
                sunrise = sun.sunrise ?: -1L,
                sunset = sun.sunset ?: -1L,
                moonrise = moon.moonrise ?: -1L,
                moonset = moon.moonset ?: -1L,
                moonPhase = moon.phase,
                dawn = sun.dawn ?: -1L,
                dusk = sun.dusk ?: -1L
            )
        }
    }

    return Weather(
        location = location,
        current = current,
        hourly = hourlyEntries,
        daily = dailyEntries
    )
}


private fun parseInmetDate(dateStr: String, zone: ZoneId): Long {
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        LocalDate.parse(dateStr.trim(), formatter)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()
    } catch (e: Exception) {
        System.currentTimeMillis().normalizeToDay(zone.id)
    }
}


private fun parseObservationTimestamp(dateStr: String?, timeStr: String?, zone: ZoneId): Long {
    return try {
        val datePart = dateStr?.trim() ?: return System.currentTimeMillis()
        val timePart = timeStr?.trim() ?: "0000"

        val hours = timePart.padStart(4, '0').substring(0, 2).toInt()
        val minutes = timePart.padStart(4, '0').substring(2, 4).toInt()

        LocalDate.parse(datePart, DateTimeFormatter.ISO_LOCAL_DATE)
            .atTime(hours, minutes)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}


private fun parseWindDirection(dirVento: String?): WindDirection? {
    if (dirVento.isNullOrBlank()) return null
    val firstDir = dirVento.split("-", "/").firstOrNull()?.trim()
    return WindDirection.toWindDirectionFromString(firstDir)
}


private fun parseWindIntensityToKmh(intVento: String?): Double? {
    if (intVento.isNullOrBlank()) return null
    val lower = intVento.lowercase().trim()
    return when {
        lower.contains("calma") -> 0.0
        lower.contains("muito fraco") || lower.contains("muito fracas") -> 5.0
        lower.contains("fraco") || lower.contains("fracas") -> 10.0
        lower.contains("moderado") || lower.contains("moderadas") -> 20.0
        lower.contains("muito forte") || lower.contains("muito fortes") -> 45.0
        lower.contains("forte") || lower.contains("fortes") -> 30.0
        else -> null
    }
}


private fun parseDoubleSafe(value: String?): Double? {
    return value?.trim()?.replace(",", ".")?.toDoubleOrNull()
}


private fun createHourlyFromPeriod(
    period: InmetPeriodJson,
    dayMillis: Long,
    hourOffset: Int,
    zone: ZoneId
): WeatherHourly {
    val timeMilli = try {
        Instant.ofEpochMilli(dayMillis)
            .atZone(zone)
            .toLocalDate()
            .atTime(hourOffset, 0)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()
    } catch (e: Exception) {
        dayMillis
    }

    val temp = listOfNotNull(period.tempMax, period.tempMin).averageOrNull()

    return WeatherHourly(
        temperature = temp,
        windSpeed = parseWindIntensityToKmh(period.intVento),
        windDirection = parseWindDirection(period.dirVento),
        rain = 0.0,
        snowfall = null,
        uvIndex = null,
        pressureMsl = null,
        visibility = null,
        humidity = null,
        dewPoint = null,
        weatherCondition = InmetWeatherConditionMap.getCondition(period.codIcone, period.resumo),
        time = timeMilli,
        precipitationProbability = null
    )
}


private fun createHourlyFromObservation(
    entry: InmetHourlyEntryJson,
    zone: ZoneId
): WeatherHourly {
    val timeMilli = parseObservationTimestamp(entry.dtMedicao, entry.hrMedicao, zone)
    val windMs = parseDoubleSafe(entry.ventVel)
    val windKmh = windMs?.let { WindSpeedUnit.MPS.convert(it, WindSpeedUnit.KPH) }
    val windDir = parseDoubleSafe(entry.ventDir)?.toInt()?.let {
        WindDirection.toWindDirectionFromDegrees(it)
    }

    return WeatherHourly(
        temperature = parseDoubleSafe(entry.tempAr),
        windSpeed = windKmh,
        windDirection = windDir,
        rain = parseDoubleSafe(entry.precipitacaoTotal) ?: 0.0,
        snowfall = null,
        uvIndex = null,
        pressureMsl = parseDoubleSafe(entry.pressAtmEst),
        visibility = null,
        humidity = parseDoubleSafe(entry.umidRelAr),
        dewPoint = null,
        weatherCondition = WeatherCondition.NO_CONDITION_FOUND,
        time = timeMilli,
        precipitationProbability = null
    )
}


private fun createCurrentFromObservation(
    entry: InmetHourlyEntryJson,
    location: Location
): WeatherCurrent {
    val zone = safeZoneId(location.timezone)
    val timeMilli = parseObservationTimestamp(entry.dtMedicao, entry.hrMedicao, zone)
    val temp = parseDoubleSafe(entry.tempAr)
    val humidity = parseDoubleSafe(entry.umidRelAr) ?: 0.0
    val windMs = parseDoubleSafe(entry.ventVel)
    val windKmh = windMs?.let { WindSpeedUnit.MPS.convert(it, WindSpeedUnit.KPH) }
    val windDir = parseDoubleSafe(entry.ventDir)?.toInt()?.let {
        WindDirection.toWindDirectionFromDegrees(it)
    }
    val pressure = parseDoubleSafe(entry.pressAtmEst)

    return WeatherCurrent(
        temperature = temp,
        humidity = humidity,
        windSpeed = windKmh,
        windDirection = windDir,
        pressureMsl = pressure,
        visibility = null,
        cloudCover = null,
        uvIndex = null,
        weatherCondition = WeatherCondition.NO_CONDITION_FOUND,
        feelsLike = computeApparentTemperature(temp, humidity, windMs),
        time = timeMilli,
        dewPoint = null,
        utcOffsetSeconds = null,
        lastUpdatedInMilli = System.currentTimeMillis()
    )
}


private fun createCurrentFromForecast(
    days: Map<String, InmetDayJson>,
    zone: ZoneId,
    location: Location
): WeatherCurrent {
    val todayEntry = days.entries.firstOrNull()
    val dayJson = todayEntry?.value
    val dayMillis = todayEntry?.key?.let { parseInmetDate(it, zone) } ?: System.currentTimeMillis()

    val periods = listOfNotNull(dayJson?.manha, dayJson?.tarde, dayJson?.noite)
    val nowHour = Instant.ofEpochMilli(System.currentTimeMillis())
        .atZone(zone)
        .hour

    val currentPeriod = when {
        periods.isEmpty() -> null
        nowHour < 12 -> periods.firstOrNull()
        nowHour < 18 -> periods.getOrNull(1) ?: periods.firstOrNull()
        else -> periods.getOrNull(2) ?: periods.firstOrNull()
    }

    val temp = currentPeriod?.let {
        listOfNotNull(it.tempMax, it.tempMin).averageOrNull()
    } ?: dayJson?.let {
        listOfNotNull(it.tempMax, it.tempMin).averageOrNull()
    }

    val humidity = 0.0
    val windKmh = currentPeriod?.intVento?.let { parseWindIntensityToKmh(it) }
        ?: dayJson?.intVento?.let { parseWindIntensityToKmh(it) }
    val windMs = windKmh?.let { WindSpeedUnit.KPH.convert(it, WindSpeedUnit.MPS) }
    val windDir = currentPeriod?.dirVento?.let { parseWindDirection(it) }
        ?: dayJson?.dirVento?.let { parseWindDirection(it) }

    val condition = currentPeriod?.let {
        InmetWeatherConditionMap.getCondition(it.codIcone, it.resumo)
    } ?: dayJson?.let {
        InmetWeatherConditionMap.getCondition(it.codIcone, it.resumo)
    } ?: WeatherCondition.NO_CONDITION_FOUND

    return WeatherCurrent(
        temperature = temp,
        humidity = humidity,
        windSpeed = windKmh,
        windDirection = windDir,
        pressureMsl = null,
        visibility = null,
        cloudCover = null,
        uvIndex = null,
        weatherCondition = condition,
        feelsLike = computeApparentTemperature(temp, humidity, windMs),
        time = System.currentTimeMillis(),
        dewPoint = null,
        utcOffsetSeconds = null,
        lastUpdatedInMilli = System.currentTimeMillis()
    )
}


@JvmName("averageDoubleOrNull")
private fun List<Double>.averageOrNull(): Double? {
    return if (isEmpty()) null else average()
}

@JvmName("averageIntOrNull")
private fun List<Int>.averageOrNull(): Double? {
    return if (isEmpty()) null else average()
}
