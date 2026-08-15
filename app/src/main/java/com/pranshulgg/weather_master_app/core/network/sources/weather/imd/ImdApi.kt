package com.pranshulgg.weather_master_app.core.network.sources.weather.imd

import com.pranshulgg.weather_master_app.core.network.sources.weather.imd.json.ImdForecastJson
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface ImdApi {


    @GET("test4_mme.php")
    suspend fun fetchForecast(
        @Query("lat_gfs") latitude: Double,
        @Query("lon_gfs") longitude: Double,
        @Query("date") date: String,
    ): Response<ImdForecastJson>

    @GET("{path}")
    suspend fun fetchTimestamps(
        @Path("path") path: String,
    ): Response<ResponseBody>


    companion object {
        const val BASE_URL = "https://mausamgram.imd.gov.in/"

        fun create(): ImdApi {


            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImdApi::class.java)
        }
    }

}