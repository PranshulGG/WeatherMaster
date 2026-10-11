package com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertsDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.FinishedAlertsResult
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.pirateweather.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.pirateweather.toDomain
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertCacheModel
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherAdditionalData
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import javax.inject.Inject

/**
 * Initial Pirate Weather integration implemented by https://github.com/altendorfme
 */

class PirateWeatherRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: PirateWeatherApi,
    val apiKeysDao: ApiKeysDao,
    val alertsDao: AlertsDao,
) : BaseRepository() {

    override val weatherSource = Source.PIRATE_WEATHER
    override val alertSource = Source.PIRATE_WEATHER
    override val airQualitySource = Source.NONE
    override val providesAlerts = true


    override fun weatherCapability(): WeatherCapability {
        return object : WeatherCapability {


            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {

                val response = safeApiCall {
                    api.fetchWeather(
                        cacheModel.apiKey!!,
                        "${location.latitude},${location.longitude}"
                    )
                }.getOrThrow()

                val domain = response.toDomain(location)


                val additionalData = WeatherAdditionalData(
                    alerts = AlertsDataPack(
                        response.alerts?.toDomain(location.id) ?: emptyList(),
                        location
                    )
                )

                return WeatherDataPack(weather = domain, additionalData = additionalData)
            }

            override suspend fun saveToDb(data: WeatherDataPack, cacheModel: CacheModel) {
                useGenericSaveImplementationForWeather(
                    existingHourly = cacheModel.cachedHourly,
                    data.weather,
                    weatherDao
                )
            }

            override suspend fun saveAdditionalDataToDb(pack: WeatherDataPack) {
                useGenericSaveImplementationForAlerts(
                    pack.additionalData?.alerts!!,
                    alertsDao,
                    dao
                )

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
                if (location.source == location.alertSource) {
                    return AlertsDataPack(alertCacheModel.cachedAlerts, location)
                } else {

                    val response = safeApiCall {
                        api.fetchWeather(
                            alertCacheModel.apiKey!!,
                            "${location.latitude},${location.longitude}"
                        )
                    }.getOrThrow()

                    return AlertsDataPack(
                        alerts = response.alerts?.toDomain(location.id) ?: emptyList(), location
                    )
                }
            }

            override suspend fun saveToDb(
                data: AlertsDataPack,
                alertCacheModel: AlertCacheModel
            ) {
                useGenericSaveImplementationForAlerts(data, alertsDao, dao)
            }

            override fun finishedResult(data: List<Alert>): FinishedAlertsResult {
                return FinishedAlertsResult(alerts = data)
            }
        }

    }

    override fun airQualityCapability(): AirQualityCapability? = null

}