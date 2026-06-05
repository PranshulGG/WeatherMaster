package com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance

import com.pranshulgg.weather_master_app.BuildConfig
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.json.MeteoFranceForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway.json.MetNorwayForecastJson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface MeteoFranceApi {


    @GET("v2/forecast")
    suspend fun fetchWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("formatDate") formatDate: String = "unixtime",
        @Query("lang") lang: String = "en"
    ): Response<MeteoFranceForecastJson>


    companion object {
        const val BASE_URL = "https://webservice.meteofrance.com//"

        fun create(): MeteoFranceApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val auth = Interceptor { chain ->
                val original = chain.request()
                val newUrl = original.url.newBuilder()
                    .addQueryParameter("token", BuildConfig.MF_KEY).build()
                val request = original.newBuilder().url(newUrl).build()
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header(
                            "User-Agent",
                            "WeatherMaster (com.pranshulgg.weathermaster; pranshul.devmain@gmail.com)"
                        )
                        .build()

                    chain.proceed(request)
                }
                .addInterceptor(auth)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MeteoFranceApi::class.java)
        }
    }

}