package com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json

// Only class10s is modeled - Gson ignores the JSON's other top-level keys
// (centers/offices/class15s/class20s) automatically since they're not declared here.
data class JmaAreaJson(
    val class10s: Map<String, JmaClass10Json>? = null
)

data class JmaClass10Json(
    val name: String? = null,
    val enName: String? = null,
    val parent: String? = null
)
