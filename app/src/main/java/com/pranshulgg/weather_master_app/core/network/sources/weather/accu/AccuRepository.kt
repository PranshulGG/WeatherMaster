package com.pranshulgg.weather_master_app.core.network.sources.weather.accu

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityDataPack
import com.pranshulgg.weather_master_app.core.model.weather.airquality.FinishedAirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertsDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.FinishedAlertsResult
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.airquality.json.bundle.AccuAqiJsonBundle
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.bundle.AccuWeatherBundle
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.accu.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.accu.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.accu.toDomain
import com.pranshulgg.weather_master_app.data.repository.airquality.AirQualityCacheModel
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertCacheModel
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.AirQualityAdditionalData
import com.pranshulgg.weather_master_app.data.repository.data.AlertsAdditionalData
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherAdditionalData
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import javax.inject.Inject

class AccuRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: AccuApi,
    val locationKeysDao: LocationKeysDao,
    val airQualityDao: AirQualityDao,
    val weatherContextDao: WeatherContextDao,
    val alertsDao: AlertsDao
) : BaseRepository() {

    override val weatherSource = Source.ACCU_WEATHER
    override val airQualitySource = Source.ACCU_WEATHER
    override val alertSource = Source.ACCU_WEATHER

    override fun weatherCapability(): WeatherCapability? {
        return object : WeatherCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {
                val locationKey = cacheModel.apiKey
                    ?: safeApiCall { api.getLocationKey("${location.latitude},${location.longitude}") }.getOrThrow().key

                val current = safeApiCall {
                    api.fetchCurrent(locationKey)
                }.getOrThrow()


                val hourly = safeApiCall {
                    api.fetchHourly(locationKey)
                }.getOrThrow()

                val daily = safeApiCall { api.fetchDaily(locationKey) }.getOrThrow()


                val final = AccuWeatherBundle(
                    current = current[0],
                    hourly = hourly,
                    daily = daily
                )

                val domain = final.toDomain(location)


                return WeatherDataPack(
                    domain,
                    additionalData = WeatherAdditionalData(locationKey = locationKey)
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
                val locationKey = alertCacheModel.apiKey
                    ?: safeApiCall {
                        api.getLocationKey("${location.latitude},${location.longitude}")
                    }.getOrThrow().key


                val response = safeApiCall {
                    api.fetchAlerts(locationKey, language = getCurrentAppLocale().language)
                }.getOrThrow()


                val domain = response.map { it.toDomain(location.id) }

                return AlertsDataPack(
                    alerts = domain,
                    location = location,
                    additionalData = AlertsAdditionalData(locationKey = locationKey)
                )
            }

            override suspend fun saveAdditionalDataToDb(pack: AlertsDataPack) {
                locationKeysDao.insertCityKey(
                    LocationKeyEntity(
                        locationId = pack.location.id,
                        cityKey = pack.additionalData?.locationKey!!
                    )
                )
            }

            override suspend fun saveToDb(data: AlertsDataPack, alertCacheModel: AlertCacheModel) {
                useGenericSaveImplementationForAlerts(data, alertsDao, dao)
            }

            override fun finishedResult(data: List<Alert>): FinishedAlertsResult {
                return FinishedAlertsResult(alerts = data)
            }
        }
    }

    override fun airQualityCapability(): AirQualityCapability? {
        return object : AirQualityCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                airQualityCacheModel: AirQualityCacheModel
            ): AirQualityDataPack {
                val locationKey = airQualityCacheModel.apiKey
                    ?: safeApiCall { api.getLocationKey("${location.latitude},${location.longitude}") }.getOrThrow().key


                val responseCurrent =
                    safeApiCall { api.fetchCurrentAirQuality(locationKey) }.getOrThrow()

                val responseForecast =
                    safeApiCall { api.fetchAirQualityForecast(locationKey) }.getOrThrow()


                val final = AccuAqiJsonBundle(
                    current = responseCurrent,
                    forecast = responseForecast
                )

                return AirQualityDataPack(
                    airQuality = final.toDomain(),
                    location = location,
                    additionalData = AirQualityAdditionalData(locationKey = locationKey)
                )
            }

            override suspend fun saveAdditionalDataToDb(pack: AirQualityDataPack?) {
                locationKeysDao.insertCityKey(
                    LocationKeyEntity(
                        locationId = pack?.location?.id!!,
                        cityKey = pack.additionalData?.locationKey!!
                    )
                )
            }

            override suspend fun saveToDb(data: AirQualityDataPack) {
                useGenericSaveImplementationForAirQuality(airQualityDao, data)
            }

            override fun finishedResult(data: AirQuality): FinishedAirQualityResult {
                return FinishedAirQualityResult(airQuality = data)
            }
        }
    }


}