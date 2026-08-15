package com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice

import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.json.MetOfficeDailyForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.json.MetOfficeHourlyForecastJson
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface MetOfficeApi {


    @GET("point/hourly")
    suspend fun fetchHourlyForecast(
        @Header("apikey") apiKey: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("dataSource") dataSource: String = "BD1"
    ): Response<MetOfficeHourlyForecastJson>

    @GET("point/daily")
    suspend fun fetchDailyForecast(
        @Header("apikey") apiKey: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("dataSource") dataSource: String = "BD1"
    ): Response<MetOfficeDailyForecastJson>


    companion object {
        const val BASE_URL = "https://data.hub.api.metoffice.gov.uk/sitespecific/v0/"

        fun create(): MetOfficeApi {


            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MetOfficeApi::class.java)
        }
    }

}