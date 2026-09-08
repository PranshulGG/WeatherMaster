package com.pranshulgg.weather_master_app.data.repository.capability

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityDataPack
import com.pranshulgg.weather_master_app.core.model.weather.airquality.FinishedAirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertsDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.FinishedAlertsResult
import com.pranshulgg.weather_master_app.data.repository.airquality.AirQualityCacheModel
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertCacheModel

interface AirQualityCapability {

    suspend fun fetchAndProcess(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        airQualityCacheModel: AirQualityCacheModel
    ): AlertsDataPack

    suspend fun saveToDb(
        data: AirQualityDataPack,
        airQualityCacheModel: AirQualityCacheModel
    )

    fun finishedResult(
        data: AirQualityDataPack
    ): FinishedAirQualityResult

    suspend fun saveAdditionalDataToDb(
        pack: AirQualityDataPack
    ) = Unit

}