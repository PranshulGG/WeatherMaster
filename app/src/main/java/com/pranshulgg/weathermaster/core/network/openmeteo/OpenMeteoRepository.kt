package com.pranshulgg.weathermaster.core.network.openmeteo

import android.util.Log
import com.pranshulgg.weathermaster.core.model.WeatherResult
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.data.local.dao.WeatherDataDao
import com.pranshulgg.weathermaster.data.local.mapper.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.weatherProviders.toCurrentWeatherEntity
import com.pranshulgg.weathermaster.data.local.mapper.weatherProviders.toDailyWeatherEntity
import com.pranshulgg.weathermaster.data.local.mapper.weatherProviders.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.weatherProviders.toHourlyWeatherEntity
import com.pranshulgg.weathermaster.data.repository.WeatherRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OpenMeteoRepository @Inject constructor(
    val dao: WeatherDataDao,
    val api: OpenMeteoApi
) : WeatherRepository {

    override suspend fun getWeather(location: Location, isRefresh: Boolean): WeatherResult {

        val cache = dao.getAllWeatherDataForLocation(location.id)


        // User refresh: 15 min limit to prevent spamming
        // Auto-refresh: 45 mins cache window
        if (cache?.current != null) {
            val cacheMilli = cache.current.time * 1000L
            val ageMillis = System.currentTimeMillis() - cacheMilli
            val ageMinutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)

            if (isRefresh && ageMinutes < 15) {
                return WeatherResult.RefreshNotAvailable
            }

            val maxAge = if (isRefresh) 15 else 4500 // TODO: CHANGE TO 45

            if (ageMinutes < maxAge) {
                Log.d("WeatherRepository", "Returning cached data") // TODO: REMOVE THIS PROD
                return WeatherResult.Success(cache.toDomain())
            }

        }

        return try {

            val response =
                api.fetchWeather(location.latitude, location.longitude, location.timezone)

            val body = response.body() ?: return WeatherResult.Error("Empty response")

            val domain = body.toDomain(location)


            dao.insertWeather(
                domain.current.toCurrentWeatherEntity(location.id),
                domain.hourly.toHourlyWeatherEntity(location.id),
                domain.daily.toDailyWeatherEntity(location.id)
            )

            WeatherResult.Success(domain)
        } catch (e: Exception) {
            WeatherResult.Error(e.message ?: "Unknown error")

        }

    }


}