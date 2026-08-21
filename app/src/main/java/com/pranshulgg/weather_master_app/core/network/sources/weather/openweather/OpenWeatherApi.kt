package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather

import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.OpenWeatherAirQualityJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.OpenWeatherCurrentJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.OpenWeatherForecastJson
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface OpenWeatherApi {

    @GET("forecast")
    suspend fun fetchForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<OpenWeatherForecastJson>


    @GET("weather")
    suspend fun fetchCurrent(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<OpenWeatherCurrentJson>

    @GET("air_pollution/forecast")
    suspend fun fetchAirQuality(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
    ): Response<OpenWeatherAirQualityJson>

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        fun create(): OpenWeatherApi {

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherApi::class.java)
        }
    }

}