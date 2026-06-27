package com.pranshulgg.weather_master_app.core.network.sources.address.nominatim

import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json.NominatimAddressJson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface NominatimApi {


    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("format") format: String = "jsonv2",
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("layer") layer: String = "address",
        @Query("accept-language") lang: String = "en"
    ): Response<NominatimAddressJson>

    companion object {
        const val BASE_URL = "https://nominatim.openstreetmap.org/"

        fun create(): NominatimApi {


            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header(
                            "User-Agent",
                            "WeatherMaster (com.pranshulgg.weathermaster; pranshul.devmain@gmail.com)"
                        )
                        .build()

                    chain.proceed(request)
                }
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NominatimApi::class.java)
        }
    }
}