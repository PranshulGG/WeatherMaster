package com.pranshulgg.weather_master_app.core.network.sources.weather.kmi

import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.JmaApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.alerts.json.JmaWarningJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaAmedasCurrentJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaAmedasStationJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaAreaJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaForecastBlockJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaHourlyJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaWeekAreaEntryJson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface KmiApi {

    @GET("appv4/?s=getForecasts")
    suspend fun fetchWeather(
        @Query("lat") lat: Double,
        @Query("long") lon: Double,
        @Query("k") key: String,
    )


    companion object {
        const val BASE_URL = "https://app.meteo.be/services/"

        fun create(): KmiApi {

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KmiApi::class.java)
        }
    }
}
