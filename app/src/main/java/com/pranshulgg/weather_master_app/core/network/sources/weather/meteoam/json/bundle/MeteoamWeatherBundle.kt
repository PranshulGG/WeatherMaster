package com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.json.bundle

import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.json.MeteoamCurrentWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.json.MeteoamForecastWeatherJson

data class MeteoamWeatherBundle(
    val current: MeteoamCurrentWeatherJson,
    val forecast: MeteoamForecastWeatherJson
)