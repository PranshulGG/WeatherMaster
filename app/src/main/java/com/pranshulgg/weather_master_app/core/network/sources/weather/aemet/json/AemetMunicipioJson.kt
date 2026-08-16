package com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json

// One entry of /maestro/municipios (~8,000 entries). `id` (e.g. "id28079") is the code AEMET's
// forecast endpoints expect once the "id" prefix is stripped. Do not confuse with `id_old`,
// a different legacy code that does not work against the forecast endpoints.
data class AemetMunicipioJson(
    val id: String?,
    val nombre: String?,
    val latitud_dec: String?,
    val longitud_dec: String?
)
