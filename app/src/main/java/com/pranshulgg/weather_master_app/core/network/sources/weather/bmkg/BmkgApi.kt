package com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg

import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.json.BmkgCurrentForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.json.BmkgForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.china.json.ChinaForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.china.json.ChinaLocationJson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface BmkgApi {

    @GET("api/presentwx/coord")
    suspend fun fetchCurrent(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): Response<BmkgCurrentForecastJson>

    @GET("api/df/v1/forecast/coord")
    suspend fun fetchForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): Response<BmkgForecastJson>


    companion object {
        const val BASE_URL = "https://cuaca.bmkg.go.id/"

        fun create(): BmkgApi {


            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BmkgApi::class.java)
        }
    }
}

