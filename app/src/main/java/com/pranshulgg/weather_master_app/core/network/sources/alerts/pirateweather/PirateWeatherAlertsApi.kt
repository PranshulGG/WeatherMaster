package com.pranshulgg.weather_master_app.core.network.sources.alerts.pirateweather

import com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.json.PirateWeatherJson
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface PirateWeatherAlertsApi {

    @GET("forecast/{apiKey}/{coordinates}")
    suspend fun fetchAlerts(
        @Path("apiKey") apiKey: String,
        @Path("coordinates") coordinates: String,
        @Query("units") units: String = "si",
        @Query("exclude") exclude: String = "currently,minutely,hourly,daily,flags"
    ): Response<PirateWeatherJson>

    companion object {
        const val BASE_URL = "https://api.pirateweather.net/"

        fun create(): PirateWeatherAlertsApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PirateWeatherAlertsApi::class.java)
        }
    }
}
