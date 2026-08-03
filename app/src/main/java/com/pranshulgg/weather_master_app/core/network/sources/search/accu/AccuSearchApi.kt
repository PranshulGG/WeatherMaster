package com.pranshulgg.weather_master_app.core.network.sources.search.accu

import com.pranshulgg.weather_master_app.BuildConfig
import com.pranshulgg.weather_master_app.core.network.sources.search.accu.json.AccuSearchJson
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.json.GeoNamesSearchJson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface AccuSearchApi {

    @GET("locations/v1/cities/search.json")
    suspend fun search(
        @Query("q") query: String,
    ): Response<List<AccuSearchJson>>

    companion object {

        private const val BASE_URL = "https://api.accuweather.com/"

        fun create(): AccuSearchApi {

            val auth = Interceptor { chain ->
                val original = chain.request()

                val newUrl =
                    original.url.newBuilder().addQueryParameter("apikey", BuildConfig.ACCU_KEY)
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
                .create(AccuSearchApi::class.java)

        }

    }

}