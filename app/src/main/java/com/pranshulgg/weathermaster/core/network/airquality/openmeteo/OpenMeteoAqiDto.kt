package com.pranshulgg.weathermaster.core.network.airquality.openmeteo

import com.google.gson.annotations.SerializedName


// TODO: ADD HOURLY-DAILY FORECAST
data class OpenMeteoAqiDto(
    val current: OpenMeteoAqiCurrentDto
)

data class OpenMeteoAqiCurrentDto(
    val time: Long,

    @SerializedName("us_aqi")
    val usAqi: Int,
    val pm10: Double,

    @SerializedName("pm2_5")
    val pm25: Double,

    @SerializedName("carbon_monoxide")
    val carbonMonoxide: Double,

    @SerializedName("nitrogen_dioxide")
    val nitrogenDioxide: Double,

    @SerializedName("sulphur_dioxide")
    val sulphurDioxide: Double,
    val ozone: Double
)

