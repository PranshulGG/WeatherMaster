package com.pranshulgg.weather_master_app.core.network.sources.weather.ipma

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.json.IpmaLocationsJson
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.ipma.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.weather.BaseWeatherRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherAdditionalData
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IpmaRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: IpmaApi,
    val locationKeysDao: LocationKeysDao
) : BaseWeatherRepository() {

    override val weatherSource = Source.IPMA

    override suspend fun fetchAndProcessWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): Weather {

        val locationId = locationKeysDao.getCityKeyForLocation(location.id)?.cityKey.toSafeDouble()
            ?.toLong()
            ?: getClosestLocation(api.fetchLocations().body(), location)
            ?: throw AppException.EmptyResponseBody()


        val forecast = safeApiCall { api.fetchForecast(locationId) }.getOrThrow()

        setAdditionalData(
            locationKey = locationId.toString()
        )

        return forecast.toDomain(location)

    }

    override suspend fun saveAdditionalData(additionalData: WeatherAdditionalData, data: Weather) {
        locationKeysDao.insertCityKey(
            LocationKeyEntity(
                locationId = data.location.id,
                cityKey = additionalData.locationKey.toString()
            )
        )
    }

    override suspend fun saveWeatherToDb(data: Weather, cacheModel: CacheModel) {

        val mergedHourly = mergeHourlyWeather(
            existing = cacheModel.cachedHourly,
            incoming = data.hourly.toHourlyWeatherEntity(data.location)
        )
        weatherDao.insertWeather(
            data.current.toCurrentWeatherEntity(data.location.id),
            mergedHourly,
            data.daily.toDailyWeatherEntity(data.location.id),
            data.location.id
        )
    }

    override fun finishedWeatherResult(data: Weather): WeatherResult {
        return WeatherResult.Success(weather = data)
    }
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