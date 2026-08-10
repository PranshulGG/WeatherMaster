package com.pranshulgg.weather_master_app.core.network.sources.alerts.fpas

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface FpasApi {

    @GET("alert/area")
    suspend fun fetchAlerts(
        @Query("min_lat") minLat: Double,
        @Query("max_lat") maxLat: Double,
        @Query("min_lon") minLon: Double,
        @Query("max_lon") maxLon: Double,
    ): Response<List<String>>

    @GET("alert/{id}")
    suspend fun fetchAlertsCap(
        @Path("id") id: String,
    ): Response<ResponseBody>

    companion object {
        const val BASE_URL = "https://alerts.kde.org/"

        fun create(): FpasApi {


            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FpasApi::class.java)
        }
    }
}