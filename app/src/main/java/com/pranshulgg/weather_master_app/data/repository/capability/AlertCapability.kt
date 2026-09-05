package com.pranshulgg.weather_master_app.data.repository.capability

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.alerts.FinishedAlertsResult
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertCacheModel

interface AlertCapability {

    suspend fun fetchAndProcess(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        alertCacheModel: AlertCacheModel
    ): List<Alert>

    suspend fun saveToDb(
        data: List<Alert>,
        location: Location,
        alertCacheModel: AlertCacheModel
    )

    fun finishedResult(
        data: List<Alert>
    ): FinishedAlertsResult


}