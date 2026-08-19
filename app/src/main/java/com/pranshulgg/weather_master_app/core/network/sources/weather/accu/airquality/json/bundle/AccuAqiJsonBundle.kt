package com.pranshulgg.weather_master_app.core.network.sources.weather.accu.airquality.json.bundle

import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.airquality.json.AccuAqiForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.airquality.json.AccuAqiJson

data class AccuAqiJsonBundle(
    val current: AccuAqiJson,
    val forecast: AccuAqiForecastJson
)