package com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway

import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.json.MetNorwayForecastJson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface MetNorwayApi {


    @GET("locationforecast/2.0/complete")
    suspend fun fetchWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): Response<MetNorwayForecastJson>


    companion object {
        const val BASE_URL = "https://api.met.no/weatherapi/"

        fun create(): MetNorwayApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }


            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header(
                            "User-Agent",
                            "WeatherMaster/3.0.0 (com.pranshulgg.weathermaster; pranshul.devmain@gmail.com)"
                        )
                        .build()

                    chain.proceed(request)
                }
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MetNorwayApi::class.java)
        }
    }

}