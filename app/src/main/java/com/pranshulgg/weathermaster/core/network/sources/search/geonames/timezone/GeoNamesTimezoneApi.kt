package com.pranshulgg.weathermaster.core.network.sources.search.geonames.timezone

import com.pranshulgg.weathermaster.BuildConfig
import com.pranshulgg.weathermaster.core.network.sources.search.geonames.json.GeoNamesTimezoneItemJson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeoNamesTimezoneApi {

    @GET("timezoneJSON")
    suspend fun getTimezone(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("append_to_response") append: String = "maxRows=10"
    ): Response<GeoNamesTimezoneItemJson>

    companion object {

        private const val BASE_URL = "https://secure.geonames.org/"

        fun create(): GeoNamesTimezoneApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val auth = Interceptor { chain ->
                val original = chain.request()
                val newUrl = original.url.newBuilder()
                    .addQueryParameter("username", BuildConfig.GEO_NAMES_USERNAME)
                    .build()
                val request = original.newBuilder().url(newUrl).build()
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(auth)
                .addInterceptor(logging)
                .callTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(GeoNamesTimezoneApi::class.java)

        }

    }

}