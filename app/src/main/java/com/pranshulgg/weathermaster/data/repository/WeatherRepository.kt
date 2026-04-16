package com.pranshulgg.weathermaster.data.repository

import com.pranshulgg.weathermaster.core.model.Weather
import com.pranshulgg.weathermaster.data.local.dao.WeatherDataDao
import com.pranshulgg.weathermaster.data.local.entity.WeatherDataEntity

interface WeatherRepository {
    suspend fun getWeather(latitude: Double, longitude: Double): Weather?
}