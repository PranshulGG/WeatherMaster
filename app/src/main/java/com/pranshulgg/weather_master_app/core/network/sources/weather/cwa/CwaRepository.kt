package com.pranshulgg.weather_master_app.core.network.sources.weather.cwa

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.json.CwaDatasetJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.json.CwaLocationJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.model.CwaForecastBundle
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.cwa.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

// The cached "city key" for this source packs two values CWA needs into one delimited string,
// since LocationKeyEntity only has a single cityKey column: "{shortRangeEndpointId}|{townshipName}".
private const val CACHE_KEY_DELIMITER = "|"

class CwaRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: CwaApi,
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
        val existingHourly = weatherDao.getHourlyDataForLocation(location.id, location.source)

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

            val (shortRangeId, townshipName) = resolveLocation(location, key)
                ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())

            val weeklyId = CwaCountyEndpoints.byShortRangeId(shortRangeId)?.weeklyId
                ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())

            val shortRangeForecast = api.fetchDataset(shortRangeId, key, locationName = townshipName).bodyOrThrow()
            val weeklyForecast = api.fetchDataset(weeklyId, key, locationName = townshipName).bodyOrThrow()

            val domain = CwaForecastBundle(shortRange = shortRangeForecast, weekly = weeklyForecast)
                .toDomain(location)

            locationKeysDao.insertCityKey(
                LocationKeyEntity(
                    locationId = location.id,
                    cityKey = "$shortRangeId$CACHE_KEY_DELIMITER$townshipName"
                )
            )

            val mergedHourly = mergeHourlyWeather(
                existing = existingHourly,
                incoming = domain.hourly.toHourlyWeatherEntity(location)
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

    // Returns (shortRangeEndpointId, townshipName), either from the cached composite key or by
    // resolving fresh: nationwide county-centroid lookup, then that county's township list.
    private suspend fun resolveLocation(location: Location, apiKey: String): Pair<String, String>? {
        val cached = locationKeysDao.getCityKeyForLocation(location.id)?.cityKey
        if (cached != null) {
            val parts = cached.split(CACHE_KEY_DELIMITER, limit = 2)
            if (parts.size == 2) return parts[0] to parts[1]
        }

        val counties = api.fetchDataset(CwaCountyEndpoints.NATIONWIDE_SHORT_RANGE, apiKey).bodyOrThrow()
            .records?.Locations?.firstOrNull()?.Location.orEmpty()
        val nearestCounty = getClosest(counties, location) ?: return null
        val countyEndpoint = CwaCountyEndpoints.ALL.firstOrNull { it.countyName == nearestCounty.LocationName }
            ?: return null

        val townships = api.fetchDataset(countyEndpoint.shortRangeId, apiKey).bodyOrThrow()
            .records?.Locations?.firstOrNull()?.Location.orEmpty()
        val nearestTownship = getClosest(townships, location) ?: return null
        val townshipName = nearestTownship.LocationName ?: return null

        return countyEndpoint.shortRangeId to townshipName
    }
}

// An invalid/missing key returns HTTP 401 with a plain-text body (not JSON) - checked here,
// before .body() is touched, so it flows through the same WeatherResult.Error -> AppException ->
// Snackbar path as every other error, rather than surfacing as a raw Gson parse exception.
private fun Response<CwaDatasetJson>.bodyOrThrow(): CwaDatasetJson {
    if (code() == 401) throw AppException.ApiKeyRejectedError()
    return body() ?: throw AppException.Unknown()
}

private fun getClosest(locations: List<CwaLocationJson>, location: Location): CwaLocationJson? {
    var closestDistance = Float.MAX_VALUE
    var closest: CwaLocationJson? = null

    for (candidate in locations) {
        val lat = candidate.Latitude.toSafeDouble()
        val lon = candidate.Longitude.toSafeDouble()

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
                closest = candidate
            }
        }
    }

    return closest
}
