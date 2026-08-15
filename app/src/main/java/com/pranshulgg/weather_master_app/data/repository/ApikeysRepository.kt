package com.pranshulgg.weather_master_app.data.repository

import android.content.Context
import com.pranshulgg.weather_master_app.core.model.domain.weather.ApiKey
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json.NominatimRepository
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.entity.weather.ApiKeyEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject


class ApiKeysRepository @Inject constructor(
    private val dao: ApiKeysDao
) {

    suspend fun getAllApiKeys(): List<ApiKey> {
        return dao.getAllApiKeys().map {
            ApiKey(
                id = it.id,
                source = it.source,
                apiKey = it.apiKey,
                savedAt = it.savedAt
            )
        }
    }

    suspend fun updateApiKeyForSource(source: WeatherSource, apiKey: String) {
        val entity = dao.getApiKeyForSource(source)
        val savedAt = System.currentTimeMillis()

        if (entity == null) {
            dao.insertApiKeyForSource(
                ApiKeyEntity(
                    source = source,
                    apiKey = apiKey,
                    savedAt = savedAt
                )
            )
        } else {
            dao.updateApiKeyForSource(source, apiKey, savedAt)
        }
    }
}