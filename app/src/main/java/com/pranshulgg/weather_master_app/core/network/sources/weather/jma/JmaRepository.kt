package com.pranshulgg.weather_master_app.core.network.sources.weather.jma

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaAmedasCurrentJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.model.JmaForecastBundle
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAlertsCache
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.jma.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.jma.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.data.repository.data.AlertRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


/**
 * Initial JMA integration implemented by https://github.com/reveler-hub
 */


// The cached "city key" packs three values into one delimited string (LocationKeyEntity only
// has a single cityKey column), so a warm-cache read needs zero extra network calls:
// "{class10Code}|{officeCode}|{amedasId}".
private const val CACHE_KEY_DELIMITER = "|"

class JmaRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: JmaApi,
    val locationKeysDao: LocationKeysDao,
    val alertsDao: AlertsDao
) : WeatherRepository, AlertRepository {

    override val weatherSource = Source.JMA
    override val alertSource = Source.JMA

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

        return@withContext try {

            val (class10Code, officeCode, amedasId) = resolveLocation(location)
                ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())

            val hourly = api.getHourly(class10Code)
            val forecast = api.getForecast(officeCode)
            // Current conditions are a nice-to-have on top of the forecast, not required for it -
            // a failed AMeDAS fetch shouldn't fail the whole refresh.
            val current = fetchAmedasCurrent(amedasId)

            locationKeysDao.insertCityKey(
                LocationKeyEntity(
                    locationId = location.id,
                    cityKey = "$class10Code$CACHE_KEY_DELIMITER$officeCode$CACHE_KEY_DELIMITER$amedasId"
                )
            )

            val domain = JmaForecastBundle(hourly = hourly, forecast = forecast, current = current)
                .toDomain(location)

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

    override suspend fun getAlerts(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): AlertResult = withContext(Dispatchers.IO) {

        val cache = alertsDao.getAlertsForLocation(location.id)
        val shouldReturnCache = shouldReturnAlertsCache(
            cache,
            isManualRefresh,
            isForceRefresh,
            location.alertsLastFetchedAt
        )

        when (shouldReturnCache) {
            AlertResultType.RETURN_CACHE -> return@withContext AlertResult.Success(cache.map { it!!.toDomain() })
            else -> {}
        }

        return@withContext try {
            val (class10Code, officeCode, _) = resolveLocation(location)
                ?: return@withContext AlertResult.Error(exception = AppException.Unknown())

            val domain = api.getWarnings(officeCode).toDomain(location.id, class10Code)

            alertsDao.insertAlerts(domain.map { it.toEntity(location.id) }, location.id)
            dao.updateAlertsLastFetchedAt(location.id, System.currentTimeMillis())

            AlertResult.Success(domain)

        } catch (e: Exception) {
            AlertResult.Error(exception = e, cacheAlerts = cache.map { it!!.toDomain() })
        }
    }

    // Returns (class10Code, officeCode, amedasId), either from the cached composite key or by
    // resolving fresh: nearest-match the user's coordinates against all 142 class10 regions,
    // using each region's linked AMeDAS station (week_area.json) as its centroid.
    private suspend fun resolveLocation(location: Location): Triple<String, String, String>? {
        val cached = locationKeysDao.getCityKeyForLocation(location.id)?.cityKey
        if (cached != null) {
            val parts = cached.split(CACHE_KEY_DELIMITER)
            if (parts.size == 3) return Triple(parts[0], parts[1], parts[2])
        }

        val class10s = api.getAreas().class10s.orEmpty()
        val weekAreas = api.getWeekArea()
        val amedasTable = api.getAmedasTable()

        var closestCode: String? = null
        var closestDistance = Float.MAX_VALUE

        for ((code, info) in class10s) {
            val amedasId = weekAreas[code]?.firstOrNull()?.amedas ?: continue
            val station = amedasTable[amedasId] ?: continue
            val lat = station.lat.toDecimalDegrees() ?: continue
            val lon = station.lon.toDecimalDegrees() ?: continue

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
                closestCode = code
            }
        }

        val class10Code = closestCode ?: return null
        val officeCode = class10s[class10Code]?.parent ?: return null
        val amedasId = weekAreas[class10Code]?.firstOrNull()?.amedas ?: return null

        return Triple(class10Code, officeCode, amedasId)
    }

    // AMeDAS per-station files are only published at 3-hour boundaries (00/03/06/09/12/15/18/21
    // JST) and keep accumulating 10-min entries under that same file name until the next
    // boundary, so the request needs to floor to it rather than use the literal current hour.
    // The just-started bucket can also 404 for a few minutes right after the boundary, before
    // JMA publishes its first entry - fall back one bucket further back in that case.
    private suspend fun fetchAmedasCurrent(amedasId: String): JmaAmedasCurrentJson? {
        val now = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"))
        val flooredHour = (now.hour / 3) * 3
        val boundary = now.withHour(flooredHour).withMinute(0).withSecond(0).withNano(0)

        for (bucket in listOf(boundary, boundary.minusHours(3))) {
            val path = bucket.format(DateTimeFormatter.ofPattern("yyyyMMdd_HH"))
            try {
                val result =
                    api.getAmedasCurrent(amedasId, path).entries.maxByOrNull { it.key }?.value
                if (result != null) return result
            } catch (e: Exception) {
                // try the previous bucket
            }
        }
        return null
    }
}

private fun List<Double>?.toDecimalDegrees(): Double? {
    if (this == null || size < 2) return null
    return this[0] + this[1] / 60.0
}
