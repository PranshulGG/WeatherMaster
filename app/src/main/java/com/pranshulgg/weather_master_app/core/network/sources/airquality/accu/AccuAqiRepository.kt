package com.pranshulgg.weather_master_app.core.network.sources.airquality.accu

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResultType
import com.pranshulgg.weather_master_app.core.network.sources.airquality.accu.json.bundle.AccuAqiJsonBundle
import com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo.OpenMeteoAqiApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.AccuApi
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isCurrentAirQualitySafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAirQualityCache
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.airquality.accu.AccuDao
import com.pranshulgg.weather_master_app.data.local.entity.airquality.accu.AccuEntity
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.sources.accu.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.accu.toDomain
import com.pranshulgg.weather_master_app.data.repository.AirQualityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject


class AccuAqiRepository @Inject constructor(
    private val api: AccuAqiApi,
    private val dao: AirQualityDao,
    private val accuDao: AccuDao,
    private val accuApi: AccuApi,
) : AirQualityRepository {

    override suspend fun getAirQuality(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): AirQualityResult = withContext(Dispatchers.IO) {


        val cache = dao.getAirQualityForLocation(location.id)
        val shouldReturnCache = shouldReturnAirQualityCache(cache, isManualRefresh, isForceRefresh)

        when (shouldReturnCache) {
            AirQualityResultType.RETURN_CACHE -> return@withContext AirQualityResult.Success(cache!!.toDomain())
            else -> {}
        }

        return@withContext try {

            val locationKey = accuDao.getCityKeyForLocation(location.id)?.toDomain()?.cityKey
                ?: accuApi.getLocationKey("${location.latitude},${location.longitude}").body()?.key
                ?: return@withContext AirQualityResult.Error(exception = AppException.Unknown())


            val responseCurrent = api.fetchCurrent(locationKey)

            val bodyCurrent = responseCurrent.body()
                ?: return@withContext AirQualityResult.Error(exception = UnknownHostException())

            val responseForecast = api.fetchForecast(locationKey)

            val bodyForecast = responseForecast.body()
                ?: return@withContext AirQualityResult.Error(exception = UnknownHostException())

            val final = AccuAqiJsonBundle(
                current = bodyCurrent,
                forecast = bodyForecast
            )

            val domain = final.toDomain()

            accuDao.insertCityKey(
                AccuEntity(
                    locationId = location.id,
                    cityKey = locationKey
                )
            )

            dao.insertAirQuality(
                domain.current.toEntity(location.id),
                domain.hourly.map { it.toEntity(location.id) },
                location.id
            )

            AirQualityResult.Success(domain)
        } catch (e: Exception) {

            val isCacheSafe = isCurrentAirQualitySafe(cache?.toDomain())

            AirQualityResult.Error(exception = e, if (isCacheSafe) cache?.toDomain() else null)
        }
    }
}

