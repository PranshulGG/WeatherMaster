package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather

import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.onecall.OpenWeatherOneCallCurrentJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.onecall.OpenWeatherOneCallDailyJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.onecall.OpenWeatherOneCallHourlyJson
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * One Call API 4.0 - a separate paid endpoint family from the legacy free /data/2.5/
 * endpoints in OpenWeatherApi.kt. Requires a "One Call by Call" subscription (still free
 * up to 1,000 calls/day, but only available if the user's key is subscribed to it).
 *
 * Used opportunistically: OpenWeatherRepository probes fetchCurrent() on every refresh
 * unless a recent denial was cached, and falls back to OpenWeatherApi's legacy endpoints
 * on failure - see OpenWeatherRepository.getWeather().
 */
interface OpenWeatherOneCallApi {

    @GET("current")
    suspend fun fetchCurrent(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<OpenWeatherOneCallCurrentJson>

    @GET("timeline/1h")
    suspend fun fetchHourly(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("cnt") count: Int,
        @Query("start") startEpochSeconds: Long,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<OpenWeatherOneCallHourlyJson>

    @GET("timeline/1day")
    suspend fun fetchDaily(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("cnt") count: Int,
        @Query("start") startEpochSeconds: Long,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<OpenWeatherOneCallDailyJson>

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/4.0/onecall/"

        fun create(): OpenWeatherOneCallApi {

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherOneCallApi::class.java)
        }
    }

}
