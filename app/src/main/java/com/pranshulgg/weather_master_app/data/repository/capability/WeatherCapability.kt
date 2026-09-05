package com.pranshulgg.weather_master_app.data.repository.capability

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.model.weather.nws.NwsGridPoints
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherAdditionalData

interface WeatherCapability {

    suspend fun fetchAndProcess(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): WeatherDataPack

    suspend fun saveToDb(
        data: Weather,
        cacheModel: CacheModel
    )

    fun finishedResult(
        data: Weather
    ): FinishedWeatherResult


    fun saveAdditionalDataToDb(
        pack: WeatherDataPack
    ) = Unit
}