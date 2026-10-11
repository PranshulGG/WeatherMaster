package com.pranshulgg.weather_master_app.data.repository.capability

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertsDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.FinishedAlertsResult
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertCacheModel

interface AlertCapability {

    suspend fun fetchAndProcess(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        alertCacheModel: AlertCacheModel
    ): AlertsDataPack

    suspend fun saveToDb(
        data: AlertsDataPack,
        alertCacheModel: AlertCacheModel
    )

    fun finishedResult(
        data: List<Alert>
    ): FinishedAlertsResult

    suspend fun saveAdditionalDataToDb(
        pack: AlertsDataPack
    ) = Unit
}