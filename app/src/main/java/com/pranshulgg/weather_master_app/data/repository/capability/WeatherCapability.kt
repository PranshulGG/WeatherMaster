package com.pranshulgg.weather_master_app.data.repository.capability

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel

interface WeatherCapability {

    suspend fun fetchAndProcess(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): WeatherDataPack

    suspend fun saveToDb(
        data: WeatherDataPack,
        cacheModel: CacheModel
    )

    fun finishedResult(
        data: Weather
    ): FinishedWeatherResult


    suspend fun saveAdditionalDataToDb(
        pack: WeatherDataPack
    ) = Unit
}