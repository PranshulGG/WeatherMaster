package com.pranshulgg.weather_master_app.core.network.sources.weather.kmi.json

import com.google.gson.annotations.SerializedName

data class KmiWeatherJson(
    val obs: KmiCurrentWeatherJson,
    @SerializedName("for") val forecast: KmiForecastJson
)

data class KmiCurrentWeatherJson(
    val temp: Double?,
    val ww: Double?,
)

data class KmiForecastJson(
    val daily: List<KmiDayJson>,
    val hourly: List<KmiHourJson>,
    val warning: List<KmiWarningJson>
)

data class KmiDayJson(
    val period: String?,
    val tempMin: Double?,
    val tempMax: Double?,
    val ww1: Double?,
    val ww2: Double?,
    @SerializedName("ddText") val windDirectionText: KmiDayWindDirectionJson,
    val wind: KmiDayWindJson,
    val precipChance: Double?,
    val precipQuantity: String?
)

data class KmiDayWindDirectionJson(
    val en: String?
)

data class KmiDayWindJson(
    val speed: Double?
)

data class KmiHourJson(
    val hour: String?,
    val temp: Double?,
    val ww: Double?,
    val precipChance: String?,
    val precipQuantity: Double?,
    val pressure: Double?,
    val windSpeedKm: Double?,
    val windDirectionText: KmiHourWindDirectionJson,
    val dateShow: String?,
)

data class KmiHourWindDirectionJson(
    val en: String?
)

data class KmiWarningJson(
    val warningType: KmiWarningTypeJson,
    val warningLevel: Double?,
    val fromTimestamp: String?,
    val toTimestamp: String?,
    val text: KmiWarningTextJson
)


data class KmiWarningTypeJson(
    val en: String?
)

data class KmiWarningTextJson(
    val en: String?
)