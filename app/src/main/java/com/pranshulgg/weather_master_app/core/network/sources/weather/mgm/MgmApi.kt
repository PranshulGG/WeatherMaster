package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm

import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmCurrentJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmDailyJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmHourlyResultJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.MgmLocationJson
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface MgmApi {

    @GET("web/merkezler/lokasyon")
    suspend fun fetchLocation(
        @Query("enlem") latitude: Double,
        @Query("boylam") longitude: Double,
    ): Response<MgmLocationJson>

    @GET("web/sondurumlar")
    suspend fun fetchCurrent(
        @Query("merkezid") stationId: Long,
    ): Response<List<MgmCurrentJson>>

    @GET("web/tahminler/gunluk")
    suspend fun fetchDaily(
        @Query("istno") stationId: Long,
    ): Response<List<MgmDailyJson>>

    @GET("web/tahminler/saatlik")
    suspend fun fetchHourly(
        @Query("istno") stationId: Long,
    ): Response<List<MgmHourlyResultJson>>

    companion object {
        const val BASE_URL = "https://servis.mgm.gov.tr/"
        private const val ORIGIN_URL = "https://www.mgm.gov.tr"

        fun create(): MgmApi {

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    // MGM rejects requests without a same-origin Referer/Origin with
                    // {"error":"ServerError","message":"Not allowed by MGM"} - confirmed live,
                    // same pattern as BMKG's WAF check (see BmkgApi).
                    val request = chain.request().newBuilder()
                        .header("Referer", "$ORIGIN_URL/")
                        .header("Origin", ORIGIN_URL)
                        .build()

                    chain.proceed(request)
                }
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MgmApi::class.java)
        }
    }
}
