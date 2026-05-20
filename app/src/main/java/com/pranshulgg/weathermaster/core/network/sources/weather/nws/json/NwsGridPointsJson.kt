package com.pranshulgg.weathermaster.core.network.sources.weather.nws.json

import com.google.gson.annotations.SerializedName


data class NwsGridPointsJson(
    @SerializedName("properties")
    val points: NwsGridPointsValuesJson
)

data class NwsGridPointsValuesJson(
    @SerializedName("gridId")
    val officeId: String,
    val gridX: Long,
    val gridY: Long
)

// ---------------------------- GRID POINTS DATA ----------------------------

data class NwsGridPointDataJson(
    val properties: NwsGridPointDataPropertiesJson
)

data class NwsGridPointDataPropertiesJson(
    val quantitativePrecipitation: NwsGridPointDataQuantitativePrecipitationJson,
    val snowfallAmount: NwsGridPointDataSnowfallAmountJson,
    val maxTemperature: NwsGridPointDataMaxTemperatureJson,
    val minTemperature: NwsGridPointDataMinTemperatureJson
)

data class NwsGridPointDataQuantitativePrecipitationJson(
    val values: List<NwsGridPointDataQuantitativePrecipitationValuesJson>,
)

data class NwsGridPointDataQuantitativePrecipitationValuesJson(
    val validTime: String,
    val value: Double
)

data class NwsGridPointDataSnowfallAmountJson(
    val values: List<NwsGridPointDataSnowfallAmountValuesJson>,
)

data class NwsGridPointDataSnowfallAmountValuesJson(
    val validTime: String,
    val value: Double
)

data class NwsGridPointDataMaxTemperatureJson(
    val values: List<NwsGridPointDataMaxTemperatureValuesJson>
)

data class NwsGridPointDataMinTemperatureJson(
    val values: List<NwsGridPointDataMinTemperatureValuesJson>
)

data class NwsGridPointDataMaxTemperatureValuesJson(
    val validTime: String,
    val value: Double
)

data class NwsGridPointDataMinTemperatureValuesJson(
    val validTime: String,
    val value: Double
)