package com.pranshulgg.weather_master_app.core.network.sources.weather.aemet

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json.AemetMunicipioJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.model.AemetForecastJson
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.aemet.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AemetRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: AemetApi,
    val apiKeysDao: ApiKeysDao,
    val locationKeysDao: LocationKeysDao
) : WeatherRepository {

    override suspend fun getWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): WeatherResult = withContext(Dispatchers.IO) {

        val cache = dao.getWeatherDataForLocation(location.id)
        val shouldReturnCache = shouldReturnWeatherCache(cache, isManualRefresh, isForceRefresh)
        val existingHourly = weatherDao.getHourlyDataForLocation(location.id)

        when (shouldReturnCache) {
            WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable
            WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain())
            else -> {}
        }

        val isCacheSafe = isWeatherCacheSafe(cache)

        val apiKey = apiKeysDao.getApiKeyForSource(location.source)
        if (apiKey?.apiKey.isNullOrBlank()) {
            return@withContext WeatherResult.Error(
                exception = AppException.NoApiKeyError(),
                if (isCacheSafe) cache?.toDomain() else null
            )
        }
        val key = apiKey.apiKey

        return@withContext try {

            val municipio = locationKeysDao.getCityKeyForLocation(location.id)?.cityKey
                ?: resolveMunicipio(location, key)
                ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())

            val dailyEnvelope = api.fetchDailyForecastEnvelope(municipio, key).body()
                ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())
            val dailyDatosUrl = dailyEnvelope.datos
                ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())
            val daily = api.fetchDailyForecastData(dailyDatosUrl).body()?.firstOrNull()
                ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())

            val hourlyEnvelope = api.fetchHourlyForecastEnvelope(municipio, key).body()
                ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())
            val hourlyDatosUrl = hourlyEnvelope.datos
                ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())
            val hourly = api.fetchHourlyForecastData(hourlyDatosUrl).body()?.firstOrNull()
                ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())

            val domain = AemetForecastJson(daily = daily, hourly = hourly).toDomain(location)

            locationKeysDao.insertCityKey(
                LocationKeyEntity(
                    locationId = location.id,
                    cityKey = municipio
                )
            )

            val mergedHourly = mergeHourlyWeather(
                existing = existingHourly,
                incoming = domain.hourly.toHourlyWeatherEntity(location.id)
            )
            weatherDao.insertWeather(
                domain.current.toCurrentWeatherEntity(location.id),
                mergedHourly,
                domain.daily.toDailyWeatherEntity(location.id),
                location.id
            )

            WeatherResult.Success(domain)

        } catch (e: Exception) {
            WeatherResult.Error(
                exception = e,
                if (isCacheSafe) cache?.toDomain() else null
            )
        }
    }

    // Lightweight live check that an entered key is actually accepted by AEMET, used when a
    // user saves a key in the API key config screen - hits only the small step-1 envelope,
    // not the full municipios list.
    suspend fun validateApiKey(key: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.fetchMunicipiosEnvelope(key)
            response.isSuccessful && response.body()?.estado == 200
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun resolveMunicipio(location: Location, apiKey: String): String? {
        val envelope = api.fetchMunicipiosEnvelope(apiKey).body() ?: return null
        val datosUrl = envelope.datos ?: return null
        val municipios = api.fetchMunicipiosData(datosUrl).body() ?: return null

        return getClosestMunicipio(municipios, location)?.removePrefix("id")
    }
}

private fun getClosestMunicipio(municipios: List<AemetMunicipioJson>, location: Location): String? {
    var closestDistance = Float.MAX_VALUE
    var closestId: String? = null

    for (municipio in municipios) {
        val lat = municipio.latitud_dec.toSafeDouble()
        val lon = municipio.longitud_dec.toSafeDouble()
        val id = municipio.id

        if (lat != null && lon != null && id != null) {
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
