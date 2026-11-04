package com.pranshulgg.weather_master_app.core.network.sources.alerts.metservicenz

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeUnit


interface MetserviceNzApi {

    @GET("cap/rss")
    suspend fun fetchAlertsFeed(): Response<ResponseBody>

    @GET
    suspend fun fetchAlertXml(@Url url: String): Response<ResponseBody>

    companion object {
        const val BASE_URL = "https://alerts.metservice.com/"

        fun create(): MetserviceNzApi {

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MetserviceNzApi::class.java)
        }
    }
}
