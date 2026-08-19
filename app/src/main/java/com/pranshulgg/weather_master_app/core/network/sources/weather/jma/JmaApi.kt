package com.pranshulgg.weather_master_app.core.network.sources.weather.jma

import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaAmedasCurrentJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaAmedasStationJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaAreaJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaForecastBlockJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaHourlyJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json.JmaWeekAreaEntryJson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

// All endpoints are free, public, no API key or auth of any kind - confirmed live for every
// call this source makes.
interface JmaApi {

    @GET("common/const/area.json")
    suspend fun getAreas(): JmaAreaJson

    @GET("forecast/const/week_area.json")
    suspend fun getWeekArea(): Map<String, List<JmaWeekAreaEntryJson>>

    @GET("amedas/const/amedastable.json")
    suspend fun getAmedasTable(): Map<String, JmaAmedasStationJson>

    // officeCode = a class10 region's "parent" field. Returns [nearTermBlock, weeklyBlock].
    @GET("forecast/data/forecast/{officeCode}.json")
    suspend fun getForecast(@Path("officeCode") officeCode: String): List<JmaForecastBlockJson>

    // class10Code = the resolved region code directly (real 3-hourly numeric data).
    @GET("jmatile/data/wdist/VPFD/{class10Code}.json")
    suspend fun getHourly(@Path("class10Code") class10Code: String): JmaHourlyJson

    // dateHour must be "yyyyMMdd_HH" in JST.
    @GET("amedas/data/point/{amedasId}/{dateHour}.json")
    suspend fun getAmedasCurrent(
        @Path("amedasId") amedasId: String,
        @Path("dateHour") dateHour: String
    ): Map<String, JmaAmedasCurrentJson>

    companion object {
        const val BASE_URL = "https://www.jma.go.jp/bosai/"

        fun create(): JmaApi {

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(JmaApi::class.java)
        }
    }
}
