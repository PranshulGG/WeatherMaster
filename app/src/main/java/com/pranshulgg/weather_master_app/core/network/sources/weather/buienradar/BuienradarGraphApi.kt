package com.pranshulgg.weather_master_app.core.network.sources.weather.buienradar

import com.pranshulgg.weather_master_app.core.network.sources.weather.buienradar.json.BuienradarGraphJson
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Client for Buienradar's graph-data backend, the same API that powers the
 * high-resolution rain graphs on buienradar.nl. Coverage: Netherlands, Belgium
 * and parts of western / northern Germany. Returns 5-minute rain rates (mm/h).
 */
interface BuienradarGraphApi {

    @GET("3.0/forecast/geo/{type}")
    suspend fun fetchGeoForecast(
        @Path("type") type: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("language") language: String = "en",
        @Query("startutc") startUtc: String,
        @Query("endutc") endUtc: String
    ): Response<BuienradarGraphJson>

    companion object {
        const val BASE_URL = "https://graphdata.buienradar.nl/"
        const val TYPE_MINUTE = "minute"

        fun create(): BuienradarGraphApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                // graphdata.buienradar.nl answers with a 302 that carries the
                // (lat/lon rounded) query, so redirects must be followed
                .followRedirects(true)
                .followSslRedirects(true)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BuienradarGraphApi::class.java)
        }
    }
}