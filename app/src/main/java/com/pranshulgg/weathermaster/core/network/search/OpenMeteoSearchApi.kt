package com.pranshulgg.weathermaster.core.network.search

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

interface OpenMeteoSearchApi {

    @GET("search")
    suspend fun search(
        @Query("name") query: String,
        @Query("append_to_response") append: String = "count=10&language=en&format=json"
    ): Response<OpenMeteoSearchDto>

    companion object {

        private const val BASE_URL = "https://geocoding-api.open-meteo.com/v1/"

        fun create(): OpenMeteoSearchApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .callTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(OpenMeteoSearchApi::class.java)

        }

    }

}