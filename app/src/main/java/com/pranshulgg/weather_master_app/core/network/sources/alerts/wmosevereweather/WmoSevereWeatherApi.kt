package com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather

import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.json.WmoSevereWeatherJson
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface WmoSevereWeatherApi {

    @GET("f/wfs")
    suspend fun fetchAlerts(
        @Query("typeName") typeName: String = "local_postgis:postgis_geojsons",
        @Query("cql_filter") cqlFilter: String,
        @Query("version") version: String = "1.1.0",
        @Query("outputFormat") outputFormat: String = "json",
        @Query("request") request: String = "GetFeature",
    ): Response<WmoSevereWeatherJson>

    @GET("v2/cap-alerts/{url}")
    suspend fun fetchAlertsXml(
        @Path(value = "url", encoded = true) url: String
    ): Response<ResponseBody>


    companion object {
        const val BASE_URL = "https://severeweather.wmo.int/"

        fun create(): WmoSevereWeatherApi {


            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WmoSevereWeatherApi::class.java)
        }
    }
}