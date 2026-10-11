package com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertsDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.FinishedAlertsResult
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.sources.weatherapi.toDomain
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertCacheModel
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import javax.inject.Inject


class AlertsWeatherApiRepository @Inject constructor(
    private val api: AlertsWeatherApi,
    private val dao: AlertsDao,
    private val weatherContextDao: WeatherContextDao
) : BaseRepository() {

    override val alertSource = Source.WEATHER_API
    override val weatherSource = Source.NONE
    override val airQualitySource = Source.NONE

    override fun alertCapability(): AlertCapability? {
        return object : AlertCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                alertCacheModel: AlertCacheModel
            ): AlertsDataPack {
                val response = safeApiCall {
                    api.fetchAlerts("${location.latitude},${location.longitude}")
                }.getOrThrow()


                val domain = response.toDomain(location.id)

                return AlertsDataPack(domain, location)
            }

            override suspend fun saveToDb(data: AlertsDataPack, alertCacheModel: AlertCacheModel) {
                useGenericSaveImplementationForAlerts(data, alertsDao = dao, weatherContextDao)
            }

            override fun finishedResult(data: List<Alert>): FinishedAlertsResult {
                return FinishedAlertsResult(alerts = data)
            }
        }
    }

    override fun weatherCapability(): WeatherCapability? = null
    override fun airQualityCapability(): AirQualityCapability? = null
}