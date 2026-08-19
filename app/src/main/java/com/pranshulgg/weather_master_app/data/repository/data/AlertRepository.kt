package com.pranshulgg.weather_master_app.data.repository.data

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult

interface AlertRepository {
    val alertSource: Source
    suspend fun getAlerts(

        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean = false
    ): AlertResult
}