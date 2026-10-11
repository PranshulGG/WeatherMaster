package com.pranshulgg.weather_master_app.core.network.sources.weather.ipma

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.json.IpmaLocationsJson
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.ipma.toDomain
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherAdditionalData
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import javax.inject.Inject

class IpmaRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: IpmaApi,
    val locationKeysDao: LocationKeysDao
) : BaseRepository() {

    override val weatherSource = Source.IPMA
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
                val locationId =
                    locationKeysDao.getCityKeyForLocation(location.id)?.cityKey.toSafeDouble()
                        ?.toLong()
                        ?: getClosestLocation(api.fetchLocations().body(), location)
                        ?: throw AppException.EmptyResponseBody()


                val forecast = safeApiCall { api.fetchForecast(locationId) }.getOrThrow()


                return WeatherDataPack(
                    weather = forecast.toDomain(location),
                    additionalData = WeatherAdditionalData(
                        locationKey = locationId.toString()
                    )
                )
            }

            override suspend fun saveToDb(data: WeatherDataPack, cacheModel: CacheModel) {
                useGenericSaveImplementationForWeather(
                    cacheModel.cachedHourly,
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

    override fun alertCapability(): AlertCapability? = null
    override fun airQualityCapability(): AirQualityCapability? = null
}

private fun getClosestLocation(locations: List<IpmaLocationsJson>?, location: Location): Long? {
    var closestDistance = Float.MAX_VALUE

    if (locations == null) return null

    var closestId: Long? = null
    for (i in locations) {


        val lat = i.latitude.toSafeDouble()
        val lon = i.longitude.toSafeDouble()
        val id = i.globalIdLocal

        if (lat != null && lon != null) {
            val results = FloatArray(1)

            android.location.Location.distanceBetween(
                location.latitude,
                location.longitude,
                lat,
                lon,
                results
            )

            if (results[0] < closestDistance) {
                closestDistance = results[0]
                closestId = id
            }
        }
    }

    return closestId
}