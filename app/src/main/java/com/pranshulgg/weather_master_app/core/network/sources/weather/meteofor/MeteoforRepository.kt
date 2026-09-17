package com.pranshulgg.weather_master_app.core.network.sources.weather.meteofor

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.GismeteoApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.findClosestLocation
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.parseXml
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.gismeteo.toDomain
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherAdditionalData
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import javax.inject.Inject

/**
 * Meteofor (Ukraine) weather source implemented by https://github.com/reveler-hub
 *
 * Meteofor is Gismeteo's Ukraine-facing rebrand (same company, same backend -
 * gismeteo.ua has redirected to meteofor.com since June 2023) - reuses
 * GismeteoApi/GismeteoRepository's parsing as-is, under its own Source
 * identity so Ukraine gets an appropriately-branded recommended source
 * instead of "Gismeteo (Russia)".
 */
class MeteoforRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: GismeteoApi,
    val locationKeysDao: LocationKeysDao
) : BaseRepository() {

    override val weatherSource = Source.METEOFOR
    override val alertSource = Source.NONE
    override val airQualitySource = Source.NONE

    override fun weatherCapability(): WeatherCapability? {
        return object : WeatherCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {
                var locationId = locationKeysDao.getCityKeyForLocation(location.id)
                    ?.cityKey.toSafeDouble()
                    ?.toLong()

                if (locationId == null) {
                    locationId = safeApiCall {
                        api.fetchLocations(location.latitude, location.longitude)
                    }.getOrThrow().byteStream().use { stream ->
                        findClosestLocation(location, stream)
                    }
                }

                if (locationId == null) throw AppException.EmptyResponseBody()

                val response = api.fetchForecast(id = locationId)

                val body = response.body()?.byteStream()?.use { stream ->
                    parseXml(stream)
                } ?: throw AppException.EmptyResponseBody()

                return WeatherDataPack(
                    weather = body.toDomain(location),
                    additionalData = WeatherAdditionalData(
                        locationKey = locationId.toString()
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

            override suspend fun saveAdditionalDataToDb(pack: WeatherDataPack) {
                locationKeysDao.insertCityKey(
                    LocationKeyEntity(
                        locationId = pack.weather.location.id,
                        cityKey = pack.additionalData?.locationKey.toString()
                    )
                )
            }

            override fun finishedResult(data: Weather): FinishedWeatherResult {
                return FinishedWeatherResult(weather = data)
            }
        }
    }

    override fun airQualityCapability(): AirQualityCapability? = null
    override fun alertCapability(): AlertCapability? = null
}
