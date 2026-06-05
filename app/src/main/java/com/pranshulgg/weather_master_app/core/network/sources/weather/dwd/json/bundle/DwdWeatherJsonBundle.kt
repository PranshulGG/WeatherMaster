package com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.json.bundle

import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.json.DwdCurrentWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.json.DwdWeatherForecastJson

data class DwdWeatherJsonBundle(
    val current: DwdCurrentWeatherJson,
    val forecastJson: DwdWeatherForecastJson
)