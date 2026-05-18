package com.pranshulgg.weathermaster.core.network.sources.airquality.openmeteo

import com.pranshulgg.weathermaster.core.model.domain.location.Location
import com.pranshulgg.weathermaster.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weathermaster.core.model.weather.airquality.AirQualityResultType
import com.pranshulgg.weathermaster.core.utils.weather.cache.isCurrentAirQualitySafe
import com.pranshulgg.weathermaster.core.utils.weather.cache.shouldReturnAirQualityCache
import com.pranshulgg.weathermaster.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weathermaster.data.local.mapper.airquality.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.airquality.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
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

        when (shouldReturnCache) {
            AirQualityResultType.RETURN_CACHE -> return@withContext AirQualityResult.Success(cache!!.toDomain())
            else -> {}
        }

        return@withContext try {
            val response = api.fetchAirQuality(location.latitude, location.longitude)

            val body = response.body()
                ?: return@withContext AirQualityResult.Error(exception = UnknownHostException())

            val domain = body.toDomain()

            dao.insertCurrentAirQuality(domain.current.toEntity(location.id))

            AirQualityResult.Success(domain)
        } catch (e: Exception) {

            val isCacheSafe = isCurrentAirQualitySafe(cache?.toDomain())

            AirQualityResult.Error(exception = e, if (isCacheSafe) cache?.toDomain() else null)
        }
    }
}

