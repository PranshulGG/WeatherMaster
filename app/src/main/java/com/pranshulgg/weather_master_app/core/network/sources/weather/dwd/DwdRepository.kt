package com.pranshulgg.weather_master_app.core.network.sources.weather.dwd

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.json.bundle.DwdWeatherJsonBundle
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.dwd.toDomain
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import java.time.LocalDate
import javax.inject.Inject

class DwdRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: DwdApi
) : BaseRepository() {

    override val weatherSource = Source.DWD
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
                val response = safeApiCall {
                    api.fetchCurrentWeather(
                        location.latitude,
                        location.longitude
                    )
                }.getOrThrow()
                val dates = getStartEndDate(location)

                val forecastResponse = safeApiCall {
                    api.fetchWeatherForecast(
                        location.latitude, location.longitude, dates.first, dates.second
                    )
                }.getOrThrow()


                val final = DwdWeatherJsonBundle(
                    current = response,
                    forecastJson = forecastResponse
                )

                val domain = final.toDomain(location)

                return WeatherDataPack(
                    weather = domain
                )
            }

            override suspend fun saveToDb(data: WeatherDataPack, cacheModel: CacheModel) {
                useGenericSaveImplementationForWeather(
                    cacheModel.cachedHourly,
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

private fun getStartEndDate(location: Location): Pair<String, String> {
    val zoneId = safeZoneId(location.timezone)

    val start = LocalDate.now(zoneId)
    val end = start.plusDays(5)

    return Pair(start.toString(), end.toString())
}

