package com.pranshulgg.weather_master_app.core.network.sources.weather.jma

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertsDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.FinishedAlertsResult
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaAmedasCurrentJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.model.JmaForecastBundle
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.jma.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.jma.toDomain
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertCacheModel
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherAdditionalData
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
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
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: JmaApi,
    val locationKeysDao: LocationKeysDao,
    val alertsDao: AlertsDao
) : BaseRepository() {

    override val weatherSource = Source.JMA
    override val alertSource = Source.JMA
    override val airQualitySource = Source.NONE

    override fun weatherCapability(): WeatherCapability? {
        return object : WeatherCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {
                val (class10Code, officeCode, amedasId) = resolveLocation(location)
                    ?: throw AppException.Unknown()


                val hourly = api.getHourly(class10Code)
                val forecast = api.getForecast(officeCode)

                // Current conditions are a nice-to-have on top of the forecast, not required for it -
                // a failed AMeDAS fetch shouldn't fail the whole refresh.
                val current = fetchAmedasCurrent(amedasId)


                val domain =
                    JmaForecastBundle(hourly = hourly, forecast = forecast, current = current)
                        .toDomain(location)

                return WeatherDataPack(
                    weather = domain,
                    additionalData = WeatherAdditionalData(
                        locationKey = "$class10Code$CACHE_KEY_DELIMITER$officeCode$CACHE_KEY_DELIMITER$amedasId"
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
                    entity = LocationKeyEntity(
                        locationId = pack.weather.location.id,
                        cityKey = pack.additionalData?.locationKey!!
                    )
                )
            }

            override fun finishedResult(data: Weather): FinishedWeatherResult {
                return FinishedWeatherResult(weather = data)
            }
        }
    }

    override fun alertCapability(): AlertCapability? {
        return object : AlertCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                alertCacheModel: AlertCacheModel
            ): AlertsDataPack {
                val (class10Code, officeCode, _) = resolveLocation(location)
                    ?: throw AppException.Unknown()

                val domain = api.getWarnings(officeCode).toDomain(location.id, class10Code)

                return AlertsDataPack(alerts = domain, location)
            }

            override suspend fun saveToDb(data: AlertsDataPack, alertCacheModel: AlertCacheModel) {
                useGenericSaveImplementationForAlerts(data, alertsDao, dao)
            }

            override fun finishedResult(data: List<Alert>): FinishedAlertsResult {
                return FinishedAlertsResult(alerts = data)
            }
        }
    }

    override fun airQualityCapability(): AirQualityCapability? = null

    suspend fun resolveLocation(location: Location): Triple<String, String, String>? {
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
