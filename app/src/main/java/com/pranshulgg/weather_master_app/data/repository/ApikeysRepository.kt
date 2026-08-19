package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.domain.weather.ApiKey
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.entity.weather.ApiKeyEntity
import jakarta.inject.Inject


class ApiKeysRepository @Inject constructor(
    private val dao: ApiKeysDao
) {

    suspend fun getAllApiKeys(): List<ApiKey> {
        return dao.getAllApiKeys().map {
            ApiKey(
                id = it.id,
                source = it.source,
                apiKey = it.apiKey
            )
        }
    }

    suspend fun updateApiKeyForSource(source: Source, apiKey: String) {
        val entity = dao.getApiKeyForSource(source)

        if (entity == null) {
            dao.insertApiKeyForSource(
                ApiKeyEntity(
                    source = source,
                    apiKey = apiKey
                )
            )
        } else {
            dao.updateApiKeyForSource(source, apiKey)
        }
    }
}