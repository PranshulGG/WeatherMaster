package com.pranshulgg.weather_master_app.data.repository.data

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.data.provider.SourceRepositoryProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SourceDataRepository @Inject constructor(
    private val sourceRepositoryProvider: SourceRepositoryProvider
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

        val weatherJob = async {


            weatherRepo.getWeather(
                location,
                isManualRefresh,
                isForceRefresh
            )
        }

        if (alertSource == weatherSource && weatherRepo.providesAlerts) {

            launch {
                weatherJob.await()

                val repo = sourceRepositoryProvider.getAlertRepository(alertSource)
                onAlerts(repo?.getAlerts(location = location))
            }

        } else {
            launch {
                val repo = sourceRepositoryProvider.getAlertRepository(alertSource)

                onAlerts(
                    repo?.getAlerts(
                        location = location,
                        isManualRefresh = isManualRefresh,
                        isForceRefresh = isForceRefreshForAlerts
                    )
                )
            }
        }

        if (airQualitySource == weatherSource && weatherRepo.providesAirQuality) {
            launch {
                weatherJob.await()

                val repo = sourceRepositoryProvider.getAirQualityRepository(airQualitySource)
                onAirQuality(repo?.getAirQuality(location = location))
            }

        } else {
            launch {
                val repo = sourceRepositoryProvider.getAirQualityRepository(airQualitySource)

                onAirQuality(
                    repo?.getAirQuality(
                        location = location,
                        isManualRefresh = isManualRefresh,
                        isForceRefresh = isForceRefreshForAirQuality
                    )
                )
            }
        }

        launch {
            onWeather(weatherJob.await())
        }


    }
}