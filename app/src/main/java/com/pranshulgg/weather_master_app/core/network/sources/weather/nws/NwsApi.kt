package com.pranshulgg.weather_master_app.core.network.sources.weather.nws

import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsCurrentForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsGridPointDataJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsGridPointsJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsHourlyForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsStationsJson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit


interface NwsApi {


    @GET("points/{latitude},{longitude}")
    suspend fun fetchGridPoints(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): Response<NwsGridPointsJson>

    @GET("gridpoints/{officeId}/{gridX},{gridY}/forecast")
    suspend fun fetchForecast(
        @Path("officeId") officeId: String,
        @Path("gridX") gridX: Long,
        @Path("gridY") gridY: Long
    ): Response<NwsForecastJson>


    @GET("gridpoints/{officeId}/{gridX},{gridY}/stations")
    suspend fun fetchStations(
        @Path("officeId") officeId: String,
        @Path("gridX") gridX: Long,
        @Path("gridY") gridY: Long
    ): Response<NwsStationsJson>


    @GET("stations/{station}/observations/latest")
    suspend fun fetchCurrentForecast(
        @Path("station") station: String,
    ): Response<NwsCurrentForecastJson>

    @GET("gridpoints/{officeId}/{gridX},{gridY}/forecast/hourly")
    suspend fun fetchHourlyForecast(
        @Path("officeId") officeId: String,
        @Path("gridX") gridX: Long,
        @Path("gridY") gridY: Long
    ): Response<NwsHourlyForecastJson>

    @GET("gridpoints/{officeId}/{gridX},{gridY}")
    suspend fun fetchGridPointData(
        @Path("officeId") officeId: String,
        @Path("gridX") gridX: Long,
        @Path("gridY") gridY: Long
    ): Response<NwsGridPointDataJson>

    companion object {
        const val BASE_URL = "https://api.weather.gov/"

        fun create(): NwsApi {

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NwsApi::class.java)
        }
    }

}