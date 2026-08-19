package com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.airquality

import com.google.gson.annotations.SerializedName


// TODO: ADD HOURLY-DAILY FORECAST
data class OpenMeteoAqiDto(
    val current: OpenMeteoAqiCurrentDto,
    val hourly: OpenMeteoAqiHourlyDto
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


data class OpenMeteoAqiHourlyDto(
    val time: List<Long>,


    val pm10: List<Double?>,

    @SerializedName("pm2_5")
    val pm25: List<Double?>,

    @SerializedName("carbon_monoxide")
    val carbonMonoxide: List<Double?>,

    @SerializedName("nitrogen_dioxide")
    val nitrogenDioxide: List<Double?>,

    @SerializedName("sulphur_dioxide")
    val sulphurDioxide: List<Double?>,
    val ozone: List<Double?>
)

