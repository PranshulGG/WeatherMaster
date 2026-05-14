package com.pranshulgg.weathermaster.core.network.airquality.openmeteo

import android.util.Log
import com.pranshulgg.weathermaster.core.model.domain.AirQuality
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.weather.WeatherResult
import com.pranshulgg.weathermaster.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weathermaster.core.utils.weather.cache.isCurrentAirQualitySafe
import com.pranshulgg.weathermaster.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weathermaster.core.utils.weather.cache.shouldReturnAirQualityCache
import com.pranshulgg.weathermaster.data.local.dao.AirQualityDao
import com.pranshulgg.weathermaster.data.local.entity.AirQualityWithRelations
import com.pranshulgg.weathermaster.data.local.mapper.airquality.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.airquality.toEntity
import com.pranshulgg.weathermaster.data.local.mapper.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OpenMeteoAqiRepository @Inject constructor(
    private val api: OpenMeteoAqiApi,
    private val dao: AirQualityDao
) {

    suspend fun getAirQuality(
        location: Location,
        isManualRefresh: Boolean
    ): AirQualityResult = withContext(Dispatchers.IO) {


        val cache = dao.getAirQualityForLocation(location.id)
        val shouldReturnCache = shouldReturnAirQualityCache(cache, isManualRefresh)


        if (shouldReturnCache) {
            return@withContext AirQualityResult.Success(cache!!.toDomain())
        }

        try {
            val response = api.fetchAirQuality(location.latitude, location.longitude)

            val body = response.body()
                ?: return@withContext AirQualityResult.Error(exception = UnknownHostException())

            val domain = body.toDomain()

            dao.insertCurrentAirQuality(domain.current.toEntity(location.id))

            return@withContext AirQualityResult.Success(cache!!.toDomain())
        } catch (e: Exception) {

            val isCacheSafe = isCurrentAirQualitySafe(cache?.toDomain())

            AirQualityResult.Error(exception = e, if (isCacheSafe) cache?.toDomain() else null)
        }
    }
}

