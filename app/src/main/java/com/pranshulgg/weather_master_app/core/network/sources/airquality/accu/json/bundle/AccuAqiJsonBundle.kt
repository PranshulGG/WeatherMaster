package com.pranshulgg.weather_master_app.core.network.sources.airquality.accu.json.bundle

import com.pranshulgg.weather_master_app.core.network.sources.airquality.accu.json.AccuAqiForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.airquality.accu.json.AccuAqiJson

data class AccuAqiJsonBundle(
    val current: AccuAqiJson,
    val forecast: AccuAqiForecastJson
)