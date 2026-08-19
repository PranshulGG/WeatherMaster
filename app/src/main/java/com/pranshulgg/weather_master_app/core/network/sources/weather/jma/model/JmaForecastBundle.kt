package com.pranshulgg.weather_master_app.core.network.sources.weather.jma.model

import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaAmedasCurrentJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaForecastBlockJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaHourlyJson

// forecast[0] = near-term (pops), forecast[1] = weekly (daily). current is null if the AMeDAS
// fetch failed - the rest of the forecast is still usable without it.
data class JmaForecastBundle(
    val hourly: JmaHourlyJson,
    val forecast: List<JmaForecastBlockJson>,
    val current: JmaAmedasCurrentJson?
)
