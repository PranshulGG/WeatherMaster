package com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.json

import com.google.gson.annotations.SerializedName


data class MetOfficeDailyForecastJson(
    val features: List<MetOfficeDailyForecastFeatureJson>
)

data class MetOfficeDailyForecastFeatureJson(
    val properties: MetOfficeDailyForecastPropertiesJson
)

data class MetOfficeDailyForecastPropertiesJson(
    val timeSeries: List<MetOfficeDailyForecastTimeSeriesJson>
)

data class MetOfficeDailyForecastTimeSeriesJson(
    val time: String,

    @SerializedName("midday10MWindSpeed")
    val midday10mWindSpeedMs: Double?,

    @SerializedName("midnight10MWindSpeed")
    val midnight10mWindSpeedMs: Double?,

    @SerializedName("midday10MWindDirection")
    val midday10mWindDirection: Int?,

    @SerializedName("midnight10MWindDirection")
    val midnight10mWindDirection: Int?,

    @SerializedName("midday10MWindGust")
    val midday10mWindGustMs: Double?,

    @SerializedName("midnight10MWindGust")
    val midnight10mWindGustMs: Double?,

    @SerializedName("middayVisibility")
    val middayVisibilityM: Long?,

    @SerializedName("midnightVisibility")
    val midnightVisibilityM: Long?,

    @SerializedName("middayRelativeHumidity")
    val middayRelativeHumidity: Double?,

    @SerializedName("midnightRelativeHumidity")
    val midnightRelativeHumidity: Double?,

    @SerializedName("middayMslp")
    val middayMslpPa: Long?,

    @SerializedName("midnightMslp")
    val midnightMslpPa: Long?,

    val nightSignificantWeatherCode: Int?,

    val dayMaxScreenTemperature: Double?,
    val nightMinScreenTemperature: Double?,

    val dayUpperBoundMaxTemp: Double?,
    val nightUpperBoundMinTemp: Double?,

    val dayLowerBoundMaxTemp: Double?,
    val nightLowerBoundMinTemp: Double?,

    val nightMinFeelsLikeTemp: Double?,
    val dayUpperBoundMaxFeelsLikeTemp: Double?,
    val nightUpperBoundMinFeelsLikeTemp: Double?,
    val dayLowerBoundMaxFeelsLikeTemp: Double?,
    val nightLowerBoundMinFeelsLikeTemp: Double?,

    val nightProbabilityOfPrecipitation: Double?,
    val nightProbabilityOfSnow: Double?,
    val nightProbabilityOfHeavySnow: Double?,
    val nightProbabilityOfRain: Double?,
    val nightProbabilityOfHeavyRain: Double?,
    val nightProbabilityOfHail: Double?,
    val nightProbabilityOfSferics: Double?
)