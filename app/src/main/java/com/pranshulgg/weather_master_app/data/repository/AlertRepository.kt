package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult

interface AlertRepository {
    suspend fun getAlerts(
        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean
    ): AlertResult
}