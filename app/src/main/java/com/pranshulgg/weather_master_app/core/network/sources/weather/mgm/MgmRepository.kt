package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.MetOfficeApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.bundle.MgmBundle
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.mgm.toDomain
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherAdditionalData
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import javax.inject.Inject

private data class ID(val id: String?)

class MgmRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: MgmApi,
    val apiKeysDao: ApiKeysDao,
    val locationKeysDao: LocationKeysDao
) : BaseRepository() {
    override val weatherSource = Source.MGM
    override val alertSource = Source.MGM
    override val airQualitySource = Source.NONE

    override fun weatherCapability(): WeatherCapability? {
        return object : WeatherCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {

                val stationIds = locationKeysDao.getCityKeyForLocation(location.id)?.cityKey?.let {
                    separateIds(it)
                } ?: safeApiCall {
                    api.fetchLocation(
                        location.latitude,
                        location.longitude
                    )
                }.getOrThrow().let {
                    separateIds("${it.currentStationId}, ${it.hourlyStationId}, ${it.dailyStationId}")
                }

                val currentStationId = stationIds.first().id.toSafeDouble()?.toLong()
                val hourlyStationId = stationIds[1].id.toSafeDouble()?.toLong()
                val dailyStationId = stationIds[2].id.toSafeDouble()?.toLong()

                val current = currentStationId?.let {
                    safeApiCall {
                        api.fetchCurrent(stationId = it)
                    }.getOrThrow()
                } ?: throw AppException.NotFound()

                val hourly = hourlyStationId?.let {
                    safeApiCall {
                        api.fetchHourly(stationId = it)
                    }.getOrThrow()
                } ?: throw AppException.NotFound()

                val daily = dailyStationId?.let {
                    safeApiCall {
                        api.fetchDaily(stationId = it)
                    }.getOrElse { emptyList() }
                } ?: emptyList()

                if (hourly.firstOrNull() == null || daily.firstOrNull() == null) {
                    throw AppException.EmptyResponseBody()
                }

                val final = MgmBundle(
                    current = current.firstOrNull(),
                    hourly = hourly.first().forecast ?: emptyList(),
                    daily = daily.first()
                )

                return WeatherDataPack(
                    weather = final.toDomain(location),
                    additionalData = WeatherAdditionalData(
                        locationKey = "${currentStationId}, ${hourlyStationId}, $dailyStationId"
                    )
                )
            }

            override suspend fun saveToDb(data: WeatherDataPack, cacheModel: CacheModel) {
                useGenericSaveImplementationForWeather(
                    existingHourly = cacheModel.cachedHourly,
                    data.weather,
                    weatherDao
                )
            }

            override fun finishedResult(data: Weather): FinishedWeatherResult {
                return FinishedWeatherResult(weather = data)
            }
        }
    }

    override fun alertCapability(): AlertCapability? {
        TODO("Not yet implemented")
    }
}

private fun separateIds(ids: String): List<ID> {

    val parts = ids.split(",")
    val current = parts.firstOrNull()
    val hourly = parts.getOrNull(1)
    val daily = parts.getOrNull(2)

    return listOf(ID(current), ID(hourly), ID(daily))
}


