package com.pranshulgg.weather_master_app.core.network.sources.weather.imd.model

import com.pranshulgg.weather_master_app.core.network.sources.weather.imd.json.ImdForecastJson


data class ImdForecastModel(
    val forecast1hr: ImdForecastJson?,
    val forecast3hr: ImdForecastJson?,
    val forecast6hr: ImdForecastJson?,
    val timeStamp1: String,
    val timeStamp2: String,
    val timeStamp3: String,


    )