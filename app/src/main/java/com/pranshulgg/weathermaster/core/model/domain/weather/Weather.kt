package com.pranshulgg.weathermaster.core.model.domain.weather

import com.pranshulgg.weathermaster.core.model.astro.MoonPhase
import com.pranshulgg.weathermaster.core.model.domain.location.Location
import com.pranshulgg.weathermaster.core.model.weather.WeatherCondition
import com.pranshulgg.weathermaster.core.model.weather.wind.WindDirection

data class Weather(
    val location: Location,
    val current: WeatherCurrent,
    val hourly: List<WeatherHourly>,
    val daily: List<WeatherDaily>
)

data class WeatherCurrent(
    val temperature: Double?, // NOTE: ALWAYS C
    val humidity: Double,
    val windSpeed: Double?, // NOTE: ALWAYS KMH
    val windDirection: WindDirection?, // NOTE: ALWAYS DOMINANT
    val pressureMsl: Double?, // NOTE: ALWAYS HPA
    val visibility: Int?,  // NOTE: ALWAYS METERS
    val cloudCover: Double?,
    val uvIndex: Double?,
    val weatherCondition: WeatherCondition,
    val feelsLike: Double?,
    val time: Long, // NOTE: ALWAYS MILLISECONDS
    val dewPoint: Double?,
    val utcOffsetSeconds: Long?,
    val lastUpdatedInMilli: Long
) {

    fun isWindSpeedValid(): Boolean {
        return windSpeed != null
    }

    fun isWindDirectionValid(): Boolean {
        return windDirection != null
    }

    fun isPressureValid(): Boolean {
        return pressureMsl != null
    }

    fun isVisibilityValid(): Boolean {
        return visibility != null
    }

    fun isDewPointValid(): Boolean {
        return dewPoint != null
    }

    fun isUvIndexValid(): Boolean {
        return uvIndex != null
    }
}

data class WeatherHourly(
    val temperature: Double, // NOTE: ALWAYS C
    val windSpeed: Double?, // NOTE: ALWAYS KMH
    val windDirection: WindDirection?, // NOTE: ALWAYS DOMINANT
    val rain: Double,  // NOTE: ALWAYS MM
    val snowfall: Double?, // NOTE: ALWAYS CM
    val uvIndex: Double?,
    val weatherCondition: WeatherCondition,
    val time: Long,  // NOTE: ALWAYS MILLISECONDS
    val precipitationProbability: Int?
) {
    fun isWindSpeedValid(): Boolean {
        return windSpeed != null
    }

    fun isWindDirectionValid(): Boolean {
        return windDirection != null
    }

    fun isUvIndexValid(): Boolean {
        return uvIndex != null
    }
}

data class WeatherDaily(
    val temperatureMin: Double, // NOTE: ALWAYS C
    val temperatureMax: Double, // NOTE: ALWAYS C
    val windSpeed: Double?, // NOTE: ALWAYS KMH
    val windDirection: WindDirection?, // NOTE: ALWAYS DOMINANT
    val rainSum: Double,  // NOTE: ALWAYS MM
    val snowfallSum: Double?, // NOTE: ALWAYS CM
    val uvIndexMax: Double?,
    val weatherCondition: WeatherCondition,
    val time: Long, // NOTE: ALWAYS MILLISECONDS
    val precipitationProbabilityMax: Int?,
    val sunrise: Long, // NOTE: ALWAYS MILLISECONDS
    val sunset: Long, // NOTE: ALWAYS MILLISECONDS
    val moonrise: Long, // NOTE: ALWAYS MILLISECONDS
    val moonset: Long, // NOTE: ALWAYS MILLISECONDS
    val moonPhase: MoonPhase
) {

    fun isWindSpeedValid(): Boolean {
        return windSpeed != null
    }

    fun isWindDirectionValid(): Boolean {
        return windDirection != null
    }

    fun isPrecipitationProbabilityMaxValid(): Boolean {
        return precipitationProbabilityMax != null
    }

    fun isUvIndexMaxValid(): Boolean {
        return uvIndexMax != null
    }
}
