package com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json

// getHourly() response - the real 3-hourly numeric endpoint (bosai/jmatile/data/wdist/VPFD/).
data class JmaHourlyJson(
    val areaTimeSeries: JmaAreaTimeSeriesJson? = null,
    val pointTimeSeries: JmaPointTimeSeriesJson? = null
)

data class JmaAreaTimeSeriesJson(
    val timeDefines: List<JmaTimeDefineJson>? = null,
    val weather: List<String>? = null,
    val wind: List<JmaWindJson>? = null
)

data class JmaTimeDefineJson(
    val dateTime: String? = null
)

// speed is a level number, not directly usable - range is the real "min max" m/s pair for
// that level (e.g. "3 5"), use the midpoint.
data class JmaWindJson(
    val direction: String? = null,
    val range: String? = null
)

// (maxTemperature/minTemperature also exist in the raw response as sparse daily markers, but
// aren't modeled - real daily min/max comes from the weekly forecast block instead.)
data class JmaPointTimeSeriesJson(
    val pointNameEN: String? = null,
    val timeDefines: List<JmaTimeDefineJson>? = null,
    val temperature: List<Int>? = null
)
