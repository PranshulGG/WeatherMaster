package com.pranshulgg.weather_master_app.core.network.sources.search.geonames

import com.pranshulgg.weather_master_app.BuildConfig
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.json.GeoNamesSearchJson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeoNamesSearchApi {

    @GET("searchJSON")
    suspend fun search(
        @Query("name_startsWith") query: String,
        @Query("append_to_response") append: String = "maxRows=10"
    ): Response<GeoNamesSearchJson>

    companion object {

        private const val BASE_URL = "https://secure.geonames.org/"

        fun create(): GeoNamesSearchApi {

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
                .callTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(GeoNamesSearchApi::class.java)

        }

    }

}