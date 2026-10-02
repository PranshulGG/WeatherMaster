package com.pranshulgg.weather_master_app.core.network.sources.airquality.moenv

import com.pranshulgg.weather_master_app.core.network.sources.airquality.moenv.json.MoenvStationJson
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface MoenvApi {

    @GET("api/v2/aqx_p_432")
    suspend fun fetchStations(
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
    ): Response<List<MoenvStationJson>>

    companion object {
        const val BASE_URL = "https://data.moenv.gov.tw/"

        fun create(): MoenvApi {

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MoenvApi::class.java)
        }
    }
}
