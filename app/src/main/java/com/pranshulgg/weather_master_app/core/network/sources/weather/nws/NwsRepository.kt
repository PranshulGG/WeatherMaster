package com.pranshulgg.weather_master_app.core.network.sources.weather.nws

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsCurrentForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsStationsListJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.bundle.NwsWeatherJsonBundle
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAlertsCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.nws.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.nws.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.nws.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertRepository
import com.pranshulgg.weather_master_app.data.repository.weather.BaseWeatherRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherAdditionalData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class NwsRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val nwsDao: NwsDao,
    val api: NwsApi,
    val alertsDao: AlertsDao
) : BaseWeatherRepository(), AlertRepository {

    override val weatherSource = Source.NWS
    override val alertSource = Source.NWS

    override suspend fun fetchAndProcessWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): Weather {
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

        setAdditionalData(
            nwsGridPoints = nwsStationsDomain
        )

        return final.toDomain(location)

    }

    override suspend fun saveAdditionalData(additionalData: WeatherAdditionalData, data: Weather) {
        nwsDao.insertLocationGridPoints(additionalData.nwsGridPoints!!.toEntity(data.location))
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

    override fun finishedWeatherResult(data: Weather): FinishedWeatherResult {
        return FinishedWeatherResult(weather = data)
    }

    /**
     * Initial NWS alerts integration implemented by https://github.com/reveler-hub
     */
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

        if (shouldReturnCache == AlertResultType.RETURN_CACHE) {
            return@withContext AlertResult.Success(cache.map { it!!.toDomain() })
        }

        val point = "${location.latitude},${location.longitude}"

        safeApiCall { api.fetchActiveAlerts(point) }.fold(
            onSuccess = { body ->
                val domain = body.toDomain(location.id)

                alertsDao.insertAlerts(domain.map { it.toEntity(location.id) }, location.id)
                dao.updateAlertsLastFetchedAt(location.id, System.currentTimeMillis())

                AlertResult.Success(domain)
            },
            onFailure = { e ->
                AlertResult.Error(
                    exception = e as? Exception ?: Exception(e),
                    cacheAlerts = cache.map { it!!.toDomain() }
                )
            }
        )
    }
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