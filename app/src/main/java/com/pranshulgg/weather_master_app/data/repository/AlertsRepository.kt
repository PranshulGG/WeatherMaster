package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlertsRepository @Inject constructor(
    private val dao: AlertsDao,
) {
    fun getAllLocationsAlerts(): Flow<List<Alert?>> {
        return dao.getAllLocationsAlerts().map { list -> list.map { it?.toDomain() } }
    }

    suspend fun getAlertsForLocation(locationId: String): List<Alert?> {
        return dao.getAlertsForLocation(locationId).map { it?.toDomain() }
    }
}