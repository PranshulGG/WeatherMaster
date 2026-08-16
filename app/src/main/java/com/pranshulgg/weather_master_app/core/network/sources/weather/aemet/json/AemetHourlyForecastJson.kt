package com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json

// Root of the hourly "datos" response is a one-element array, typically covering ~3 days.
data class AemetHourlyForecastRootJson(
    val nombre: String?,
    val prediccion: AemetHourlyPredictionJson?
)

data class AemetHourlyPredictionJson(
    val dia: List<AemetHourlyDayJson>?
)

data class AemetHourlyDayJson(
    val fecha: String?, // midnight of that date, e.g. "2026-08-14T00:00:00"
    val orto: String?, // sunrise, "HH:mm"
    val ocaso: String?, // sunset, "HH:mm"
    val estadoCielo: List<AemetHourlyEntryJson>?,
    val precipitacion: List<AemetHourlyEntryJson>?,
    val probPrecipitacion: List<AemetHourlyEntryJson>?,
    val probTormenta: List<AemetHourlyEntryJson>?,
    val nieve: List<AemetHourlyEntryJson>?,
    val probNieve: List<AemetHourlyEntryJson>?,
    val temperatura: List<AemetHourlyEntryJson>?,
    val sensTermica: List<AemetHourlyEntryJson>?,
    val humedadRelativa: List<AemetHourlyEntryJson>?,
    // Heterogeneous array: entries are either a wind reading (direccion/velocidad populated)
    // or a gust reading (value populated), distinguished by which fields are non-null.
    val vientoAndRachaMax: List<AemetHourlyWindOrGustJson>?
)

// value is consistently a quoted string for every per-hour field observed (e.g. "31", "0.1", "" when unresolved)
data class AemetHourlyEntryJson(
    val value: String?,
    val periodo: String?, // hour of day, "00".."23"
    val descripcion: String? = null // only present on estadoCielo entries
)

data class AemetHourlyWindOrGustJson(
    val periodo: String?,
    val direccion: List<String>?,
    val velocidad: List<String>?,
    val value: String? // gust speed, when this entry is a gust reading rather than a wind reading
)
