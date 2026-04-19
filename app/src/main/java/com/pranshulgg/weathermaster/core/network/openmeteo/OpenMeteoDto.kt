package com.pranshulgg.weathermaster.core.network.openmeteo

import com.google.gson.annotations.SerializedName


data class OpenMeteoWeatherDto(
    val latitude: Double,
    val longitude: Double,
    val current: OpenMeteoCurrentDataDto,
    val hourly: OpenMeteoHourlyDataDto,
    val daily: OpenMeteoDailyDataDto
)


data class OpenMeteoCurrentDataDto(
    val time: Long,

    @SerializedName("temperature_2m")
    val temperature: Double,

    val rain: Double,
    val showers: Double,
    val snowfall: Double,

    @SerializedName("weather_code")
    val weatherCode: Int,

    @SerializedName("wind_speed_10m")
    val windSpeed: Double,

    @SerializedName("wind_direction_10m")
    val windDirection: Int,

    @SerializedName("wind_gusts_10m")
    val windGusts: Double,

    @SerializedName("pressure_msl")
    val pressureMsl: Double,

    @SerializedName("relative_humidity_2m")
    val relativeHumidity: Double,

    @SerializedName("is_day")
    val isDay: Int,

    @SerializedName("apparent_temperature")
    val feelsLike: Double,

    @SerializedName("cloud_cover")
    val cloudCover: Double,

    @SerializedName("uv_index")
    val uvIndex: Double
)

data class OpenMeteoHourlyDataDto(
    val time: List<Long>,

    @SerializedName("temperature_2m")
    val temperature: List<Double>,

    val rain: List<Double>,
    val showers: List<Double>,
    val snowfall: List<Double>,

    @SerializedName("weather_code")
    val weatherCode: List<Int>,

    @SerializedName("relative_humidity_2m")
    val relativeHumidity: List<Double>,

    @SerializedName("apparent_temperature")
    val feelsLike: List<Double>,

    val visibility: List<Int>,

    @SerializedName("dew_point_2m")
    val dewPoint: List<Double>,

    @SerializedName("uv_index")
    val uvIndex: List<Double>,

    @SerializedName("wind_speed_10m")
    val windSpeed: List<Double>,

    @SerializedName("wind_direction_10m")
    val windDirection: List<Int>,

    @SerializedName("precipitation_probability")
    val precipitationProbability: List<Int>
)

data class OpenMeteoDailyDataDto(
    val time: List<Long>,

    @SerializedName("temperature_2m_min")
    val temperatureMin: List<Double>,

    @SerializedName("temperature_2m_max")
    val temperatureMax: List<Double>,


    @SerializedName("rain_sum")
    val rainSum: List<Double>,

    @SerializedName("showers_sum")
    val showersSum: List<Double>,

    @SerializedName("snowfall_sum")
    val snowfallSum: List<Double>,

    @SerializedName("weather_code")
    val weatherCode: List<Int>,

    val sunrise: List<Long>,
    val sunset: List<Long>,

    @SerializedName("daylight_duration")
    val daylightDuration: List<Double>,

    @SerializedName("uv_index_max")
    val uvIndexMax: List<Double>,

    @SerializedName("precipitation_hours")
    val precipitationHours: List<Int>,

    @SerializedName("precipitation_probability_max")
    val precipitationProbabilityMax: List<Int>,

    @SerializedName("wind_speed_10m_max")
    val windSpeedMax: List<Double>,

    @SerializedName("wind_direction_10m_dominant")
    val windDirectionDominant: List<Int>
)

