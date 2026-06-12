package com.pranshulgg.weather_master_app.core.network.sources.weather.smhi

import com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.json.SmhiForecastJson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit


interface SmhiApi {


    @GET("geotype/point/lon/{lon}/lat/{lat}/data.json")
    suspend fun fetchWeather(
        @Path("lat") latitude: Double,
        @Path("lon") longitude: Double,
    ): Response<SmhiForecastJson>


    companion object {
        const val BASE_URL =
            "https://opendata-download-metfcst.smhi.se/api/category/snow1g/version/1/"

        fun create(): SmhiApi {


            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SmhiApi::class.java)
        }
    }

}