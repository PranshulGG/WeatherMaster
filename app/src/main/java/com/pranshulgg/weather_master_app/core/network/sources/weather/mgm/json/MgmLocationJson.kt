package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json

import com.google.gson.annotations.SerializedName

data class MgmLocationJson(
    @SerializedName("merkezId") val currentStationId: Long?,
    @SerializedName("gunlukTahminIstNo") val dailyStationId: Long?,
    @SerializedName("saatlikTahminIstNo") val hourlyStationId: Long?,
)
