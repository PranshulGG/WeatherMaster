package com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json

import com.google.gson.annotations.SerializedName

data class AccuCurrentWeatherJson(
    @SerializedName("EpochTime") val time: Long,
    @SerializedName("WeatherIcon") val weatherIcon: Int,
    @SerializedName("Temperature") val temperature: AccuCurrentWeatherTemperatureJson,
    @SerializedName("RealFeelTemperature") val feelsLike: AccuCurrentWeatherFeelsLikeJson,
    @SerializedName("RelativeHumidity") val humidity: Int?,
    @SerializedName("DewPoint") val dewPoint: AccuCurrentWeatherDewPointJson,
    @SerializedName("Wind") val wind: AccuCurrentWeatherWindJson,
    @SerializedName("UVIndexFloat") val uvIndex: Double?,
    @SerializedName("Pressure") val pressure: AccuCurrentWeatherPressureJson,
    @SerializedName("Visibility") val visibility: AccuCurrentWeatherVisibilityJson
)


data class AccuCurrentWeatherVisibilityJson(
    @SerializedName("Metric") val metric: AccuCurrentWeatherVisibilityValueJson
)

data class AccuCurrentWeatherVisibilityValueJson(
    @SerializedName("Value") val value: Double?
)

data class AccuCurrentWeatherTemperatureJson(
    @SerializedName("Metric") val metric: AccuCurrentWeatherTemperatureValueJson
)

data class AccuCurrentWeatherTemperatureValueJson(
    @SerializedName("Value") val value: Double
)

data class AccuCurrentWeatherFeelsLikeJson(
    @SerializedName("Metric") val metric: AccuCurrentWeatherFeelsLikeValueJson
)

data class AccuCurrentWeatherFeelsLikeValueJson(
    @SerializedName("Value") val value: Double?
)

data class AccuCurrentWeatherDewPointJson(
    @SerializedName("Metric") val metric: AccuCurrentWeatherDewPointValueJson
)

data class AccuCurrentWeatherDewPointValueJson(
    @SerializedName("Value") val value: Double?
)

data class AccuCurrentWeatherWindJson(
    @SerializedName("Direction") val direction: AccuCurrentWeatherWindDirection,
    @SerializedName("Speed") val speed: AccuCurrentWeatherWindSpeedJson
)

data class AccuCurrentWeatherWindSpeedJson(
    @SerializedName("Metric") val metric: AccuCurrentWeatherWindSpeedValueJson
)

data class AccuCurrentWeatherWindSpeedValueJson(
    @SerializedName("Value") val value: Double?
)

data class AccuCurrentWeatherWindDirection(
    @SerializedName("Degrees") val degrees: Int?
)

data class AccuCurrentWeatherPressureJson(
    @SerializedName("Metric") val metric: AccuCurrentWeatherPressureValueJson
)

data class AccuCurrentWeatherPressureValueJson(
    @SerializedName("Value") val value: Double?
)

// HOURLY

data class AccuHourlyWeatherJson(
    @SerializedName("EpochDateTime") val time: Long,
    @SerializedName("WeatherIcon") val weatherIcon: Int,
    @SerializedName("Temperature") val temperature: AccuHourlyWeatherTemperatureValueJson,
    @SerializedName("RealFeelTemperature") val feelsLike: AccuHourlyWeatherFeelsLikeTemperatureValueJson,
    @SerializedName("DewPoint") val dewPoint: AccuHourlyWeatherDewPointValueJson,
    @SerializedName("Wind") val wind: AccuHourlyWeatherWindJson,
    @SerializedName("RelativeHumidity") val humidity: Int?,
    @SerializedName("Visibility") val visibility: AccuHourlyWeatherVisibilityValueJson,
    @SerializedName("UVIndexFloat") val uvIndex: Double?,
    @SerializedName("PrecipitationProbability") val precipitationProbability: Int?,
    @SerializedName("Rain") val rain: AccuHourlyWeatherRainValueJson,
    @SerializedName("Snow") val snowCm: AccuHourlyWeatherSnowValueJson
)

data class AccuHourlyWeatherTemperatureValueJson(
    @SerializedName("Value") val value: Double
)

data class AccuHourlyWeatherFeelsLikeTemperatureValueJson(
    @SerializedName("Value") val value: Double?
)

data class AccuHourlyWeatherDewPointValueJson(
    @SerializedName("Value") val value: Double?
)

data class AccuHourlyWeatherWindJson(
    @SerializedName("Speed") val speed: AccuHourlyWeatherWindSpeedValueJson,
    @SerializedName("Direction") val direction: AccuHourlyWeatherWindDirectionValueJson
)

data class AccuHourlyWeatherWindSpeedValueJson(
    @SerializedName("Value") val value: Double?
)

data class AccuHourlyWeatherWindDirectionValueJson(
    @SerializedName("Degrees") val degrees: Int?
)

data class AccuHourlyWeatherVisibilityValueJson(
    @SerializedName("Value") val value: Double?
)

data class AccuHourlyWeatherRainValueJson(
    @SerializedName("Value") val value: Double?
)

data class AccuHourlyWeatherSnowValueJson(
    @SerializedName("Value") val value: Double?
)

// DAILY

data class AccuDailyWeatherJson(
    @SerializedName("DailyForecasts") val daily: List<AccuDailyWeatherItemJson>
)

data class AccuDailyWeatherItemJson(
    @SerializedName("EpochDate") val time: Long,
    @SerializedName("Temperature") val temperature: AccuDailyWeatherTemperatureJson,
    @SerializedName("Day") val day: AccuDailyWeatherDataJson,
    @SerializedName("Night") val night: AccuDailyWeatherDataJson

)


data class AccuDailyWeatherTemperatureJson(
    @SerializedName("Minimum") val min: AccuDailyWeatherTemperatureMinValueJson,
    @SerializedName("Maximum") val max: AccuDailyWeatherTemperatureMaxValueJson,
)

data class AccuDailyWeatherTemperatureMinValueJson(
    @SerializedName("Value") val value: Double
)

data class AccuDailyWeatherTemperatureMaxValueJson(
    @SerializedName("Value") val value: Double
)

data class AccuDailyWeatherDataJson(
    @SerializedName("Icon") val icon: Int,
    @SerializedName("PrecipitationProbability") val precipitationProbability: Int?,
    @SerializedName("Wind") val wind: AccuDailyWeatherWindJson,
    @SerializedName("Rain") val rain: AccuDailyWeatherRainValueJson,
    @SerializedName("Snow") val snowCm: AccuDailyWeatherSnowValueJson,
    @SerializedName("RelativeHumidity") val humidity: AccuDailyWeatherHumidityValueJson,
    @SerializedName("UVIndexFloat") val uvIndex: AccuDailyWeatherUvIndexValueJson

)

data class AccuDailyWeatherWindJson(
    @SerializedName("Speed") val speed: AccuDailyWeatherWindSpeedValueJson,
    @SerializedName("Direction") val direction: AccuDailyWeatherWindDirectionValueJson
)

data class AccuDailyWeatherWindSpeedValueJson(
    @SerializedName("Value") val value: Double?
)

data class AccuDailyWeatherWindDirectionValueJson(
    @SerializedName("Degrees") val degrees: Double?
)

data class AccuDailyWeatherRainValueJson(
    @SerializedName("Value") val value: Double?
)

data class AccuDailyWeatherSnowValueJson(
    @SerializedName("Value") val value: Double?
)

data class AccuDailyWeatherHumidityValueJson(
    @SerializedName("Average") val value: Int?
)


data class AccuDailyWeatherUvIndexValueJson(
    @SerializedName("Maximum") val value: Double?
)
