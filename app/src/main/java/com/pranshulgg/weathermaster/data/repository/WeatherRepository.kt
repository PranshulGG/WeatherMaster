package com.pranshulgg.weathermaster.data.repository

import com.pranshulgg.weathermaster.core.model.Weather

interface WeatherRepository {
    suspend fun getWeather(latitude: Double, longitude: Double): Weather?
}