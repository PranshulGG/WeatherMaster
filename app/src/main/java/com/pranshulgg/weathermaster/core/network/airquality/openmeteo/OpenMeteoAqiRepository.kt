package com.pranshulgg.weathermaster.core.network.airquality.openmeteo

import android.util.Log
import com.pranshulgg.weathermaster.core.model.domain.AirQuality
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.utils.weather.cache.shouldReturnAirQualityCache
import com.pranshulgg.weathermaster.data.local.dao.AirQualityDao
import com.pranshulgg.weathermaster.data.local.entity.AirQualityWithRelations
import com.pranshulgg.weathermaster.data.local.mapper.airquality.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.airquality.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OpenMeteoAqiRepository @Inject constructor(
    private val api: OpenMeteoAqiApi,
    private val dao: AirQualityDao
) {

    suspend fun getAirQuality(
        location: Location,
        isManualRefresh: Boolean
    ): AirQuality? = withContext(Dispatchers.IO) {


        val cache = dao.getAirQualityForLocation(location.id)
        val shouldReturnCache = shouldReturnAirQualityCache(cache, isManualRefresh)


        if (shouldReturnCache) {
            return@withContext cache!!.toDomain()
        }

        try {

            val response = api.fetchAirQuality(location.latitude, location.longitude)

            val body = response.body() ?: return@withContext null

            val domain = body.toDomain()

            dao.insertCurrentAirQuality(domain.current.toEntity(location.id))

            return@withContext domain
        } catch (e: Exception) {

            // TODO: ADD ERROR HANDLING
            return@withContext null
        }
    }
}

