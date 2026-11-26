package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertsDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.FinishedAlertsResult
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.MetOfficeApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.bundle.MgmBundle
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.mgm.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.mgm.toDomain
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertCacheModel
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.AlertsAdditionalData
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
    val locationKeysDao: LocationKeysDao,
    val alertsDao: AlertsDao
) : BaseRepository() {
    override val weatherSource = Source.MGM
    override val alertSource = Source.MGM
    override val airQualitySource = Source.NONE

    override fun weatherCapability(): WeatherCapability {
        return object : WeatherCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {

                val (currentStationId, hourlyStationId, dailyStationId) =
                    resolveStationIds(location)


                val current = currentStationId?.let {
                    safeApiCall {
                        api.fetchCurrent(stationId = it)
                    }.getOrThrow()
                } ?: throw AppException.EmptyResponseBody()

                // No hourly station even after the province fallback: show the rest instead of failing
                val hourly = hourlyStationId?.let {
                    safeApiCall {
                        api.fetchHourly(stationId = it)
                    }.getOrThrow()
                } ?: emptyList()

                val daily = dailyStationId?.let {
                    safeApiCall {
                        api.fetchDaily(stationId = it)
                    }.getOrElse { emptyList() }
                } ?: throw AppException.EmptyResponseBody()

                if (daily.firstOrNull() == null) {
                    throw AppException.EmptyResponseBody()
                }

                val final = MgmBundle(
                    current = current.firstOrNull(),
                    hourly = hourly.firstOrNull()?.forecast ?: emptyList(),
                    daily = daily.first()
                )

                return WeatherDataPack(
                    weather = final.toDomain(location),
                    additionalData = WeatherAdditionalData(
                        locationKey = "${currentStationId}, ${hourlyStationId}, $dailyStationId"
                    )
                )
            }

            override suspend fun saveAdditionalDataToDb(pack: WeatherDataPack) {

                locationKeysDao.insertCityKey(
                    LocationKeyEntity(
                        locationId = pack.weather.location.id,
                        cityKey = pack.additionalData?.locationKey!!
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
        return object : AlertCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                alertCacheModel: AlertCacheModel
            ): AlertsDataPack {

                val (resolvedCurrentStationId, hourlyStationId, dailyStationId) =
                    resolveStationIds(location)

                val currentStationId = resolvedCurrentStationId ?: throw AppException.NotFound()

                val today = "today"
                val tomorrow = "tomorrow"

                val alertsForToday =
                    safeApiCall { api.fetchAlerts(day = today) }.getOrElse { emptyList() }

                val alertsForTomorrow =
                    safeApiCall { api.fetchAlerts(day = tomorrow) }.getOrElse { emptyList() }

                val alerts = alertsForToday + alertsForTomorrow


                return AlertsDataPack(
                    alerts = alerts.toDomain(
                        locationId = location.id,
                        currentStationId = currentStationId
                    ),
                    location = location,
                    additionalData = AlertsAdditionalData(
                        locationKey = "${currentStationId}, ${hourlyStationId}, $dailyStationId"
                    )
                )
            }

            override suspend fun saveToDb(data: AlertsDataPack, alertCacheModel: AlertCacheModel) {
                useGenericSaveImplementationForAlerts(data, alertsDao, dao)
            }

            override suspend fun saveAdditionalDataToDb(pack: AlertsDataPack) {
                locationKeysDao.insertCityKey(
                    LocationKeyEntity(
                        locationId = pack.location.id,
                        cityKey = pack.additionalData?.locationKey!!
                    )
                )
            }


            override fun finishedResult(data: List<Alert>): FinishedAlertsResult {
                return FinishedAlertsResult(alerts = data)
            }
        }
    }

    override fun airQualityCapability(): AirQualityCapability? = null

    /**
     * Returns the current, hourly and daily station IDs, from the saved key when it has an
     * hourly station, otherwise from a fresh lookup.
     *
     * Most districts have no hourly station (`saatlikTahminIstNo` is null), so the
     * province centre's hourly station is used for those. Keys saved before this fallback
     * existed have "null" for hourly, so they get looked up again once.
     */
    private suspend fun resolveStationIds(location: Location): Triple<Long?, Long?, Long?> {
        val cached = locationKeysDao.getCityKeyForLocation(location.id)?.cityKey?.let {
            separateIds(it).map { id -> id.id.toSafeDouble()?.toLong() }
        }

        if (cached != null && cached[1] != null) {
            return Triple(cached[0], cached[1], cached[2])
        }

        val lookup = safeApiCall {
            api.fetchLocation(
                location.latitude,
                location.longitude
            )
        }.getOrThrow()

        val hourlyStationId = lookup.hourlyStationId
            ?: provinceHourlyStationId(lookup.province)

        return Triple(lookup.currentStationId, hourlyStationId, lookup.dailyStationId)
    }

    private suspend fun provinceHourlyStationId(province: String?): Long? {
        if (province.isNullOrBlank()) return null

        return safeApiCall { api.fetchProvinceCentres() }
            .getOrNull()
            ?.firstOrNull { it.province == province }
            ?.hourlyStationId
    }
}

private fun separateIds(ids: String): List<ID> {

    val parts = ids.split(",")
    val current = parts.firstOrNull()
    val hourly = parts.getOrNull(1)
    val daily = parts.getOrNull(2)

    return listOf(ID(current), ID(hourly), ID(daily))
}


