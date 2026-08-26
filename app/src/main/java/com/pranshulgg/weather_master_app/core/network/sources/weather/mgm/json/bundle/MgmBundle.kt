package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.bundle

import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmCurrentJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmDailyJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmHourlyForecastJson


data class MgmBundle(
    val current: MgmCurrentJson?,
    val daily: MgmDailyJson?,
    val hourly: List<MgmHourlyForecastJson>?
)
