package com.pranshulgg.weathermaster.core.network.openmeteo

import com.pranshulgg.weathermaster.core.model.Weather
import com.pranshulgg.weathermaster.data.local.dao.WeatherDataDao
import com.pranshulgg.weathermaster.data.local.dao.WeatherLocationDao
import com.pranshulgg.weathermaster.data.local.mapper.toDomain
import com.pranshulgg.weathermaster.data.repository.WeatherRepository
import javax.inject.Inject

class OpenMeteoRepository @Inject constructor(
    val daoLocation: WeatherLocationDao,
    val daoData: WeatherDataDao,
    val api: OpenMeteoApi
) : WeatherRepository {

    override suspend fun getWeather(latitude: Double, longitude: Double): Weather? {

        val response = api.fetchWeather(latitude, longitude)

        val body = response.body() ?: return null

//        dao.insertWeatherData(body.toEntity())

        return body.toDomain()

    }


}