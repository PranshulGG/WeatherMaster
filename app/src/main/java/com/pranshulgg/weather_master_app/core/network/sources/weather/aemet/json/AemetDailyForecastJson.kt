package com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json

import com.google.gson.JsonElement

// Root of the daily "datos" response is a one-element array.
data class AemetDailyForecastRootJson(
    val nombre: String?,
    val provincia: String?,
    val prediccion: AemetDailyPredictionJson?
)

data class AemetDailyPredictionJson(
    val dia: List<AemetDailyDayJson>?
)

data class AemetDailyDayJson(
    val fecha: String?, // e.g. "2026-08-14T00:00:00", no offset/zone
    val estadoCielo: List<AemetPeriodStringJson>?,
    val probPrecipitacion: List<AemetPeriodValueJson>?,
    val rachaMax: List<AemetPeriodValueJson>?,
    val viento: List<AemetDailyWindJson>?,
    val temperatura: AemetDailyMinMaxJson?,
    val sensTermica: AemetDailyMinMaxJson?,
    val humedadRelativa: AemetDailyMinMaxJson?,
    val uvMax: Int?
)

// estadoCielo entries: value is always a quoted string (sky code, or "" if unresolved for that period)
data class AemetPeriodStringJson(
    val value: String?,
    val periodo: String?
)

// probPrecipitacion/rachaMax entries: value's JSON type varies (raw number or quoted string) by field/date
data class AemetPeriodValueJson(
    val value: JsonElement?,
    val periodo: String?
)

// daily wind: velocidad's JSON type has been observed as both a raw number and empty string
data class AemetDailyWindJson(
    val direccion: String?,
    val velocidad: JsonElement?,
    val periodo: String?
)

// temperatura/sensTermica/humedadRelativa: maxima/minima observed as raw numbers
data class AemetDailyMinMaxJson(
    val maxima: JsonElement?,
    val minima: JsonElement?
)
