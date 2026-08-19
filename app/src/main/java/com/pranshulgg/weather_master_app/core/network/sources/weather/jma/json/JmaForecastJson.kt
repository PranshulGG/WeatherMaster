package com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json

// getForecast() returns a 2-element List<JmaForecastBlockJson>:
// [0] near-term (~3 days): timeSeries[1] has real "pops" (precip probability, 6h blocks) -
//     that's the only field used from this block, broadcast onto the hourly VPFD timeline.
// [1] weekly (7 days): already one entry per day (no 12h-block grouping needed, unlike CWA) -
//     weatherCodes/pops/reliabilities in one timeSeries, tempsMin/tempsMax in another.
// One shape is reused for both blocks since the fields present just differ per block.
data class JmaForecastBlockJson(
    val timeSeries: List<JmaTimeSeriesJson>? = null
)

data class JmaTimeSeriesJson(
    val timeDefines: List<String>? = null,
    val areas: List<JmaTimeSeriesAreaJson>? = null
)

data class JmaTimeSeriesAreaJson(
    val weatherCodes: List<String>? = null,
    val pops: List<String>? = null,
    val tempsMin: List<String>? = null,
    val tempsMax: List<String>? = null
)
