package com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val CURRENT_FIELDS =
    "us_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone"
private const val DOMAINS = "cams_global"

interface OpenMeteoAqiApi {

    @GET("air-quality")
    suspend fun fetchAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") appendCurrent: String = CURRENT_FIELDS,
        @Query("domains") appendDomains: String = DOMAINS,
        @Query("timeformat") appendTimeFormat: String = "unixtime"
    ): Response<OpenMeteoAqiDto>

    companion object {
        const val BASE_URL = "https://air-quality-api.open-meteo.com/v1/"

        fun create(): OpenMeteoAqiApi {


            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenMeteoAqiApi::class.java)
        }
    }
}