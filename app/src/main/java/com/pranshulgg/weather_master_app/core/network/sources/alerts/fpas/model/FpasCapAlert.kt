package com.pranshulgg.weather_master_app.core.network.sources.alerts.fpas.model

data class FpasCapAlert(
    val language: String?,
    val event: String?,
    val severity: String?,
    val effective: String?,
    val expires: String?,
    val senderName: String?,
    val headline: String?,
    val description: String?
)