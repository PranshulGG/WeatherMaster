package com.pranshulgg.weather_master_app.core.network.sources.weather.eccc

import com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.json.EcccWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.json.SmhiForecastJson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit


interface EcccApi {


    @GET("v3/en/Location/{latitude},{longitude}")
    suspend fun fetchWeather(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double,
    ): Response<List<EcccWeatherJson>>


    companion object {
        const val BASE_URL = "https://app.weather.gc.ca/"

        fun create(): EcccApi {


            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header(
                            "User-Agent",
                            "WeatherMaster (com.pranshulgg.weathermaster; pranshul.devmain@gmail.com)"
                        )
                        .build()

                    chain.proceed(request)
                }
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EcccApi::class.java)
        }
    }

}