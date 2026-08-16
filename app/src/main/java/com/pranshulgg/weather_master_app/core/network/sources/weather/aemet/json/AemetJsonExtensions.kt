package com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json

import com.google.gson.JsonElement

// AEMET is inconsistent about whether numeric fields are sent as raw JSON numbers
// or as quoted strings (varies by field and sometimes by municipio/date), so fields
// that have been observed to vary are typed as JsonElement and read through this.
fun JsonElement?.toSafeDouble(): Double? {
    if (this == null || isJsonNull) return null

    return if (isJsonPrimitive && asJsonPrimitive.isNumber) {
        asDouble
    } else {
        asString.toDoubleOrNull()
    }
}
