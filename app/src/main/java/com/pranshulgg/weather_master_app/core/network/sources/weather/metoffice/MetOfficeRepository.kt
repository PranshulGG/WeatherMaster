package com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.model.MetOfficeForecastJson
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.metoffice.toDomain
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import javax.inject.Inject


class MetOfficeRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: MetOfficeApi,
    val apiKeysDao: ApiKeysDao
) : BaseRepository() {

    override val weatherSource = Source.MET_OFFICE
    override val alertSource = Source.NONE
    override val airQualitySource = Source.NONE

    override fun weatherCapability(): WeatherCapability? {
        return object : WeatherCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {
                val hourly = safeApiCall {
                    api.fetchHourlyForecast(
                        cacheModel.apiKey!!, location.latitude, location.longitude
                    )
                }.getOrThrow()

                val daily = safeApiCall {
                    api.fetchDailyForecast(
                        cacheModel.apiKey!!, location.latitude, location.longitude
                    )
                }.getOrThrow()


                val final = MetOfficeForecastJson(
                    hourly = hourly,
                    daily = daily
                )

                return WeatherDataPack(
                    weather = final.toDomain(location)
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

    override fun airQualityCapability(): AirQualityCapability? = null
    override fun alertCapability(): AlertCapability? = null
}