package com.pranshulgg.weather_master_app.core.network.sources.weather.china

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

interface ChinaApi {

    @GET("location/city/geo")
    suspend fun getLocationKey(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("locale") locale: String = "en"
    ): Response<List<ChinaLocationJson>>

    @GET("weather/all")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("appKey") appKey: String,
        @Query("locationKey") locationKey: String,
        @Query("sign") sign: String,
        @Query("locale") locale: String = "en",
        @Query("isGlobal") isGlobal: Boolean = false
    ): Response<ChinaForecastJson>

    companion object {
        const val BASE_URL = "https://weatherapi.market.xiaomi.com/wtr-v3/"

        fun create(): ChinaApi {

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChinaApi::class.java)
        }
    }
}

