package com.pranshulgg.weather_master_app.core.network.sources.weather.ipma

import com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.json.IpmaForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.json.IpmaLocationsJson
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface IpmaApi {

    @GET("public-data/forecast/locations.json")
    suspend fun fetchLocations(): Response<List<IpmaLocationsJson>>

    @GET("public-data/forecast/aggregate/{id}.json")
    suspend fun fetchForecast(
        @Path("id") id: Long
    ): Response<List<IpmaForecastJson>>


    companion object {
        const val BASE_URL = "https://api.ipma.pt/"

        fun create(): IpmaApi {


            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(IpmaApi::class.java)
        }
    }

}