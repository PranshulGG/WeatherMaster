package com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo

import com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.json.IpmaForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.json.IpmaLocationsJson
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface GismeteoApi {


    @GET("inf_android/forecast/")
    suspend fun fetchForecast(
        @Query("lang") lang: String = "en",
        @Query("city") id: Long
    ): Response<ResponseBody>

    @GET("inf_android/cities/")
    suspend fun fetchLocations(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lat_lng") latLng: Int = 1,
        @Query("count") count: Int = 10,

        ): Response<ResponseBody>

    companion object {
        const val BASE_URL = "https://services.gismeteo.net/inform-service/"

        fun create(): GismeteoApi {


            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request()
                        .newBuilder()
                        .header(
                            "User-Agent",
                            "Gismeteo Android v2, Asus Nexus 7, 1.1.10"
                        )
                        .build()

                    chain.proceed(request)
                }
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .build()
                .create(GismeteoApi::class.java)
        }
    }

}