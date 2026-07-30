package com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi

import com.pranshulgg.weather_master_app.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi.json.AlertsWeatherApiJson
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response


interface AlertsWeatherApi {

    @GET("alerts.json")
    suspend fun fetchAlerts(
        @Query("q") query: String
    ): Response<AlertsWeatherApiJson>


    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"

        fun create(): AlertsWeatherApi {

            val auth = Interceptor { chain ->
                val original = chain.request()

                val newUrl =
                    original.url.newBuilder()
                        .addQueryParameter("key", BuildConfig.WEATHERAPI_KEY)
                        .build()
                val request = original.newBuilder().url(newUrl).build()

                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(auth)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AlertsWeatherApi::class.java)
        }
    }
}