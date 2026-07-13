package com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam

import com.pranshulgg.weather_master_app.BuildConfig
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.AccuCurrentWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.AccuDailyWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.AccuHourlyWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.AccuLocationJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.json.MeteoamCurrentWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.json.MeteoamForecastWeatherJson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit


interface MeteoamApi {

    @GET("deda-ows/api/GetStationRadius/{latitude}/{longitude}")
    suspend fun fetchCurrent(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): Response<MeteoamCurrentWeatherJson>

    @GET("deda-meteograms/api/GetMeteogram/preset1/{latitude},{longitude}")
    suspend fun fetchForecast(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): Response<MeteoamForecastWeatherJson>

    companion object {
        const val BASE_URL = "https://api.meteoam.it/"

        fun create(): MeteoamApi {

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MeteoamApi::class.java)
        }
    }
}

