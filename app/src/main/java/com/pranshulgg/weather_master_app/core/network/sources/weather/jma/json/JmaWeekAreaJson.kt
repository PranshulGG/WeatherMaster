package com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json

// Top-level response is Map<class10Code, List<JmaWeekAreaEntryJson>> - no wrapper class needed.
// "amedas" links a class10 region to its representative AMeDAS station id, used both for
// current-conditions lookup and (via amedastable.json) as that region's resolution centroid.
data class JmaWeekAreaEntryJson(
    val srf: String? = null,
    val week: String? = null,
    val amedas: String? = null
)
