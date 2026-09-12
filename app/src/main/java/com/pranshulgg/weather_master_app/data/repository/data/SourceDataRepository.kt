package com.pranshulgg.weather_master_app.data.repository.data

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.data.provider.SourceRepositoryProvider
import com.pranshulgg.weather_master_app.data.repository.airquality.CacheResolverForAirQuality
import com.pranshulgg.weather_master_app.data.repository.alerts.CacheResolverForAlerts
import com.pranshulgg.weather_master_app.data.repository.weather.CacheResolverForWeather
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SourceDataRepository @Inject constructor(
    private val sourceRepositoryProvider: SourceRepositoryProvider,
    private val cacheResolverForWeather: CacheResolverForWeather,
    private val cacheResolverForAirQuality: CacheResolverForAirQuality,
    private val cacheResolverForAlerts: CacheResolverForAlerts
) {
    suspend fun getData(
        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean = false,
        isForceRefreshForAirQuality: Boolean = false,
        isForceRefreshForAlerts: Boolean = false,
        onWeather: suspend (WeatherResult) -> Unit,
        onAlerts: suspend (AlertResult?) -> Unit,
        onAirQuality: suspend (AirQualityResult?) -> Unit,
    ) = coroutineScope {

        val weatherSource = location.source
        val alertSource = location.alertSource
        val airQualitySource = location.airQualitySource

        val weatherRepo = sourceRepositoryProvider.getWeatherRepository(weatherSource)

        val cacheModel = cacheResolverForWeather.resolve(
            location,
            isManualRefresh,
            isForceRefresh
        )

        val airQualityCacheModel = cacheResolverForAirQuality.resolve(
            location,
            isManualRefresh,
            isForceRefreshForAirQuality
        )

        val alertCacheModel = cacheResolverForAlerts.resolve(
            location,
            isManualRefresh,
            isForceRefreshForAlerts
        )

        val weatherJob = async {

            weatherRepo.getWeather(
                location,
                isManualRefresh,
                isForceRefresh,
                cacheModel
            )
        }

        if (alertSource == weatherSource && weatherRepo.providesAlerts) {
            launch {
                weatherJob.await()

                val repo = sourceRepositoryProvider.getAlertRepository(alertSource)
                onAlerts(repo?.getAlerts(location = location, alertCacheModel = alertCacheModel))
            }

        } else {
            launch {
                val repo = sourceRepositoryProvider.getAlertRepository(alertSource)

                onAlerts(
                    repo?.getAlerts(
                        location = location,
                        isManualRefresh = isManualRefresh,
                        isForceRefresh = isForceRefreshForAlerts,
                        alertCacheModel = alertCacheModel
                    )
                )
            }
        }

        if (airQualitySource == weatherSource && weatherRepo.providesAirQuality) {
            launch {
                weatherJob.await()

                val repo = sourceRepositoryProvider.getAirQualityRepository(airQualitySource)
                onAirQuality(
                    repo?.getAirQuality(
                        location = location,
                        airQualityCacheModel = airQualityCacheModel
                    )
                )
            }

        } else {
            launch {
                val repo = sourceRepositoryProvider.getAirQualityRepository(airQualitySource)

                onAirQuality(
                    repo?.getAirQuality(
                        location = location,
                        isManualRefresh = isManualRefresh,
                        isForceRefresh = isForceRefreshForAirQuality,
                        airQualityCacheModel = airQualityCacheModel
                    )
                )
            }
        }

        launch {
            onWeather(weatherJob.await())
        }


    }
}