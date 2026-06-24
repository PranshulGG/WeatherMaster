package com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.json.bundle

import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.json.BmkgCurrentForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.json.BmkgForecastJson


data class BmkgForecastBundle(
    val current: BmkgCurrentForecastJson,
    val forecast: BmkgForecastJson
)