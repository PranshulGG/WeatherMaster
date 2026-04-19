package com.pranshulgg.weathermaster.core.network.openmeteo

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val DAILY_FIELDS =
    "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,daylight_duration,uv_index_max,rain_sum,showers_sum,snowfall_sum,precipitation_hours,precipitation_probability_max,wind_speed_10m_max,wind_direction_10m_dominant"

private const val HOURLY_FIELDS =
    "temperature_2m,relative_humidity_2m,dew_point_2m,apparent_temperature,precipitation_probability,rain,showers,snowfall,weather_code,visibility,uv_index,wind_speed_10m,wind_direction_10m"

private const val CURRENT_FIELDS =
    "rain,showers,snowfall,weather_code,wind_speed_10m,wind_direction_10m,wind_gusts_10m,pressure_msl,relative_humidity_2m,temperature_2m,is_day,apparent_temperature,uv_index,cloud_cover"

interface OpenMeteoApi {


    @GET("forecast")
    suspend fun fetchWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("timezone") timezone: String,
        @Query("hourly") appendHourly: String = HOURLY_FIELDS,
        @Query("current") appendCurrent: String = CURRENT_FIELDS,
        @Query("daily") appendDaily: String = DAILY_FIELDS,
        @Query("timeformat") appendTimeFormat: String = "unixtime"
    ): Response<OpenMeteoWeatherDto>


    companion object {
        const val BASE_URL = "https://api.open-meteo.com/v1/"

        fun create(): OpenMeteoApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenMeteoApi::class.java)
        }
    }

}