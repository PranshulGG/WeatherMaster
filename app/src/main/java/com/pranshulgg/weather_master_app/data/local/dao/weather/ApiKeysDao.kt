package com.pranshulgg.weather_master_app.data.local.dao.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.data.local.entity.weather.ApiKeyEntity

@Dao
interface ApiKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApiKeyForSource(apiKeyEntity: ApiKeyEntity)

    @Query("SELECT * FROM api_keys WHERE source = :source")
    suspend fun getApiKeyForSource(source: WeatherSource): ApiKeyEntity?

    @Query("SELECT * FROM api_keys")
    suspend fun getAllApiKeys(): List<ApiKeyEntity>

    @Query("UPDATE api_keys SET apiKey = :apiKey, savedAt = :savedAt WHERE source = :source")
    suspend fun updateApiKeyForSource(source: WeatherSource, apiKey: String, savedAt: Long)
}