package com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.bundle

import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsCurrentForecastJson
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsForecastJson
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsGridPointDataJson
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsHourlyForecastDewPointJson
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsHourlyForecastJson

data class NwsWeatherJsonBundle(
    val forecast: NwsForecastJson,
    val current: NwsCurrentForecastJson,
    val hourly: NwsHourlyForecastJson,
    val gridPointsData: NwsGridPointDataJson
)