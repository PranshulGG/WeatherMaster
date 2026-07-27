package com.pranshulgg.weather_master_app.core.network.sources.airquality.accu

import com.pranshulgg.weather_master_app.BuildConfig
import com.pranshulgg.weather_master_app.core.network.sources.airquality.accu.json.AccuAqiForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.airquality.accu.json.AccuAqiJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.AccuCurrentWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.AccuDailyWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.AccuHourlyWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.AccuLocationJson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface AccuAqiApi {

    @GET("airquality/v2/currentconditions/{locationKey}")
    suspend fun fetchCurrent(
        @Path("locationKey") locationKey: String,
        @Query("pollutants") details: Boolean = true
    ): Response<AccuAqiJson>

    @GET("airquality/v2/forecasts/hourly/24hour/{locationKey}")
    suspend fun fetchForecast(
        @Path("locationKey") locationKey: String,
        @Query("pollutants") details: Boolean = true
    ): Response<AccuAqiForecastJson>


    companion object {
        const val BASE_URL = "https://api.accuweather.com/"

        fun create(): AccuAqiApi {


            val auth = Interceptor { chain ->
                val original = chain.request()

                val newUrl =
                    original.url.newBuilder().addQueryParameter("apikey", BuildConfig.ACCU_KEY)
                        .build()
                val request = original.newBuilder().url(newUrl).build()

                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(auth)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AccuAqiApi::class.java)
        }
    }
}

