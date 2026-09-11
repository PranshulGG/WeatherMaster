package com.pranshulgg.weather_master_app.core.network.sources.weather.nws

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
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsCurrentForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsStationsListJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.bundle.NwsWeatherJsonBundle
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.nws.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.nws.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.nws.toEntity
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertCacheModel
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherAdditionalData
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import javax.inject.Inject


class NwsRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val nwsDao: NwsDao,
    val api: NwsApi,
    val alertsDao: AlertsDao
) : BaseRepository() {

    override val weatherSource = Source.NWS
    override val alertSource = Source.NWS
    override val airQualitySource = Source.NONE

    override fun weatherCapability(): WeatherCapability {
        return object : WeatherCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {
                /**
                 * NWS has everything as a separate endpoints
                 * Makes it annoying to get the data, but we'll still do it cuz why not
                 * Sequential flow, cache the annoying data (e.g. grid points and station, but we'll still update it time to time)
                 */
                var currentObservation: NwsCurrentForecastJson? = null
                val cachedGridPointsData = nwsDao.getGridPointsForLocation(location.id)


                val nwsStationsDomain = if (cachedGridPointsData != null) {
                    cachedGridPointsData.toDomain()
                } else {

                    val gridPoint = safeApiCall {
                        api.fetchGridPoints(
                            location.latitude,
                            location.longitude
                        )
                    }.getOrThrow()


                    val gridPointsDomain = gridPoint.toDomain(location, stationIdentifier = null)

                    val nwsStations = safeApiCall {
                        api.fetchStations(
                            gridPointsDomain.officeId,
                            gridPointsDomain.gridX,
                            gridPointsDomain.gridY
                        )
                    }.getOrThrow()


                    // Get all the stations
                    val stations = nwsStations.features

                    val station = getValidObservationAndStation(stations, api)


                    // New domain with stationIdentifier
                    val domain = gridPointsDomain.copy(
                        stationIdentifier = station?.first
                    )

                    if (domain.stationIdentifier == null) {
                        throw AppException.EmptyResponseBody()
                    }

                    currentObservation = station?.second

                    domain
                }

                // GET DAILY
                val nwsForecast = safeApiCall {
                    api.fetchForecast(
                        nwsStationsDomain.officeId,
                        nwsStationsDomain.gridX,
                        nwsStationsDomain.gridY
                    )
                }.getOrThrow()

                // GET CURRENT
                val nwsCurrentForecastBody = currentObservation
                    ?: safeApiCall {
                        api.fetchCurrentForecast(nwsStationsDomain.stationIdentifier!!)
                    }.getOrThrow()


                // GET HOURLY
                val nwsHourlyForecast =
                    safeApiCall {
                        api.fetchHourlyForecast(
                            nwsStationsDomain.officeId,
                            nwsStationsDomain.gridX,
                            nwsStationsDomain.gridY
                        )
                    }.getOrThrow()

                // USING FOR QuantitativePrecipitation and Snowfall
                val nwsGridPointData = safeApiCall {
                    api.fetchGridPointData(
                        nwsStationsDomain.officeId,
                        nwsStationsDomain.gridX,
                        nwsStationsDomain.gridY
                    )
                }.getOrThrow()

                // PUT EVERYTHING TOGETHER IN A BUNDLE
                val final = NwsWeatherJsonBundle(
                    current = nwsCurrentForecastBody,
                    forecast = nwsForecast,
                    hourly = nwsHourlyForecast,
                    gridPointsData = nwsGridPointData
                )


                return WeatherDataPack(
                    weather = final.toDomain(location),
                    additionalData = WeatherAdditionalData(
                        nwsGridPoints = nwsStationsDomain
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
                nwsDao.insertLocationGridPoints(pack.additionalData?.nwsGridPoints!!.toEntity(pack.weather.location))
            }

            override fun finishedResult(data: Weather): FinishedWeatherResult {
                return FinishedWeatherResult(weather = data)
            }
        }
    }

    override fun alertCapability(): AlertCapability {
        return object : AlertCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                alertCacheModel: AlertCacheModel
            ): AlertsDataPack {
                val point = "${location.latitude},${location.longitude}"

                val response = safeApiCall {
                    api.fetchActiveAlerts(point)
                }.getOrThrow()


                val domain = response.toDomain(location.id)

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

}


/**
 * Find a working station
 * Sometimes NWS returns empty pages or "Not Found" error
 */
private suspend fun getValidObservationAndStation(
    stations: List<NwsStationsListJson>,
    api: NwsApi
): Pair<String, NwsCurrentForecastJson?>? {


    for (feature in stations) {
        val stationId = feature.properties.stationIdentifier

        try {
            val response = api.fetchCurrentForecast(stationId)

            if (response.isSuccessful && response.body() != null) {
                return Pair(stationId, response.body())
            }
        } catch (_: Exception) {
        }
    }

    return null
}