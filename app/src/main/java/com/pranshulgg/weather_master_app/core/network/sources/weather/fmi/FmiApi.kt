package com.pranshulgg.weather_master_app.core.network.sources.weather.fmi

import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.xml.FmiWeatherForecastXml
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jaxb.JaxbConverterFactory
import java.util.concurrent.TimeUnit
import retrofit2.http.GET
import retrofit2.http.Query

interface FmiApi {
    @GET("wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::forecast::edited::weather::scandinavia::point::simple")
    suspend fun fetchForecast(
        @Query("latlon") latlon: String,
        @Query("endtime") endtime: String,
        @Query("starttime") starttime: String,
    ): Response<ResponseBody>

    @GET("wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::ef::stations")
    suspend fun fetchStations(): Response<ResponseBody>

    @GET("wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::simple")
    suspend fun fetchCurrent(
        @Query("fmisid") id: String,
        @Query("starttime") starttime: String,
        @Query("endtime") endtime: String,
    ): Response<ResponseBody>


    companion object {
        const val BASE_URL = "https://opendata.fmi.fi/"

        fun create(): FmiApi {


            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .build()
                .create(FmiApi::class.java)
        }
    }

}