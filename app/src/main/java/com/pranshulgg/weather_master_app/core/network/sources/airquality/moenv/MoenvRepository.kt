package com.pranshulgg.weather_master_app.core.network.sources.airquality.moenv

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityDataPack
import com.pranshulgg.weather_master_app.core.model.weather.airquality.FinishedAirQualityResult
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.moenv.findClosestStation
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.moenv.toDomain
import com.pranshulgg.weather_master_app.data.repository.airquality.AirQualityCacheModel
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import javax.inject.Inject

/**
 * MOENV (Taiwan) air quality integration implemented by https://github.com/reveler-hub
 */
class MoenvRepository @Inject constructor(
    val api: MoenvApi,
    val airQualityDao: AirQualityDao
) : BaseRepository() {

    override val weatherSource = Source.NONE
    override val alertSource = Source.NONE
    override val airQualitySource = Source.MOENV

    override fun airQualityCapability(): AirQualityCapability {
        return object : AirQualityCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                airQualityCacheModel: AirQualityCacheModel
            ): AirQualityDataPack {
                val stations = safeApiCall {
                    api.fetchStations(apiKey = airQualityCacheModel.apiKey!!)
                }.getOrThrow()

                val closest = findClosestStation(location, stations)
                    ?: throw AppException.EmptyResponseBody()

                return AirQualityDataPack(airQuality = closest.toDomain(), location)
            }

            override suspend fun saveToDb(data: AirQualityDataPack) {
                useGenericSaveImplementationForAirQuality(airQualityDao, data)
            }

            override fun finishedResult(data: AirQuality): FinishedAirQualityResult {
                return FinishedAirQualityResult(airQuality = data)
            }
        }
    }

    override fun weatherCapability(): WeatherCapability? = null
    override fun alertCapability(): AlertCapability? = null
}
