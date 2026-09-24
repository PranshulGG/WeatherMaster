package com.pranshulgg.weather_master_app.core.network.sources.weather.kmi

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.MgmApi
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import java.security.MessageDigest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

// Source - https://github.com/jdejaegh/irm-kmi-api

class KmiRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: KmiApi,
    val apiKeysDao: ApiKeysDao,
    val locationKeysDao: LocationKeysDao,
    val alertsDao: AlertsDao
) : BaseRepository() {
    override val weatherSource = Source.KMI
    override val alertSource = Source.KMI
    override val airQualitySource = Source.NONE

    override val providesAlerts = true


    override fun weatherCapability(): WeatherCapability? {
        return object : WeatherCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {
                val response = api.fetchWeather(
                    lat = location.latitude,
                    lon = location.longitude,
                    key = generateIrmKmiKey()
                )


            }

            override suspend fun saveToDb(data: WeatherDataPack, cacheModel: CacheModel) {
                TODO("Not yet implemented")
            }

            override fun finishedResult(data: Weather): FinishedWeatherResult {
                TODO("Not yet implemented")
            }
        }
    }

}

private fun generateIrmKmiKey(): String {
    val date = LocalDate.now().format(
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT)
    )

    val input = "r9EnW374jkJ9acc;getForecasts;$date"

    return MessageDigest
        .getInstance("MD5")
        .digest(input.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it) }
}
