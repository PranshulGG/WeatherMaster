package com.pranshulgg.weathermaster.core.network.openmeteo

import android.util.Log
import com.pranshulgg.weathermaster.core.model.Location
import com.pranshulgg.weathermaster.core.model.RefreshIds
import com.pranshulgg.weathermaster.core.model.Weather
import com.pranshulgg.weathermaster.data.local.dao.WeatherDataDao
import com.pranshulgg.weathermaster.data.local.mapper.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weathermaster.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weathermaster.data.local.mapper.weather.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weathermaster.data.repository.WeatherRepository
import javax.inject.Inject

class OpenMeteoRepository @Inject constructor(
    val dao: WeatherDataDao,
    val api: OpenMeteoApi
) : WeatherRepository {

    override suspend fun getWeather(location: Location, isRefresh: Boolean = false): Weather? {

        val cache = dao.getAllWeatherDataForLocation(location.id)

        if (cache != null && cache.current != null && !isRefresh) {
            return cache.toDomain()
        }


        val response = api.fetchWeather(location.latitude, location.longitude, location.timezone)

        val body = response.body() ?: return null

        val domain = body.toDomain(location)


        dao.insertWeather(
            domain.current.toCurrentWeatherEntity(location.id),
            domain.hourly.toHourlyWeatherEntity(location.id),
            domain.daily.toDailyWeatherEntity(location.id)
        )

        return domain

    }


}