package com.pranshulgg.weather_master_app.core.network.sources.weather.imd

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.imd.model.ImdForecastModel
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.imd.toDomain
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import javax.inject.Inject
import kotlin.math.roundToInt


class ImdRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: ImdApi
) : BaseRepository() {
    override val weatherSource = Source.IMD
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

                val imdTimeFrames = listOf("1hr", "3hr", "6hr")

                val timeStamps = imdTimeFrames.map {
                    safeApiCall {
                        api.fetchTimestamps("mmem_${it}.txt")
                    }.getOrThrow()
                }

                val timeStampsBody = timeStamps.map {
                    it.string().substringBefore(",")
                }
                val latitude = roundToEighth(location.latitude)
                val longitude = roundToEighth(location.longitude)

                val forecasts = timeStampsBody.mapIndexed { index, s ->
                    safeApiCall {
                        api.fetchForecast(
                            latitude = latitude,
                            longitude = longitude,
                            date = "${s}_${imdTimeFrames[index]}_0p125"
                        )
                    }.getOrThrow()
                }

                val final = ImdForecastModel(
                    forecast1hr = forecasts[0],
                    forecast3hr = forecasts[1],
                    forecast6hr = forecasts[2],
                    timeStamp1 = timeStampsBody[0],
                    timeStamp2 = timeStampsBody[1],
                    timeStamp3 = timeStampsBody[2]
                )

                val domain = final.toDomain(location)

                return WeatherDataPack(domain)

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

private fun roundToEighth(value: Double): Double =
    (value * 8).roundToInt() / 8.0