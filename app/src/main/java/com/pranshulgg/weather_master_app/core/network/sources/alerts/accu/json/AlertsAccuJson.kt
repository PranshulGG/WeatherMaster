package com.pranshulgg.weather_master_app.core.network.sources.alerts.accu.json

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


data class AlertsAccuJson(
    @SerializedName("Description") val event: AlertsAccuEventJson,
    @SerializedName("AlarmLevel") val alarmLevel: String?,
    @SerializedName("Source") val source: String?,
    @SerializedName("Area") val area: List<AlertsAccuAreaJson>,
)

data class AlertsAccuEventJson(
    @SerializedName("English") val english: String,
    @SerializedName("Localized") val localized: String
)

data class AlertsAccuAreaJson(
    @SerializedName("EpochStartTime") val epochStartTimeSeconds: Long,
    @SerializedName("EpochEndTime") val epochEndTimeSeconds: Long,
    @SerializedName("Text") val text: String,

    )