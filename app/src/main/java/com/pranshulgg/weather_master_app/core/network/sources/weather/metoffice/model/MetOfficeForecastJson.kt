package com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.model

import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.json.MetOfficeDailyForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.json.MetOfficeHourlyForecastJson


data class MetOfficeForecastJson(
    val hourly: MetOfficeHourlyForecastJson,
    val daily: MetOfficeDailyForecastJson
)