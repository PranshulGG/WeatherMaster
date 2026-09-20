package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


interface MgmApi {


    companion object {
        const val BASE_URL = "https://servis.mgm.gov.tr/"
        private const val ORIGIN_URL = "https://www.mgm.gov.tr"

        val auth = Interceptor { chain ->
            val original = chain.request()
            val new = original.newBuilder().header("Referer", "$ORIGIN_URL/")
                .header("Origin", ORIGIN_URL).build()

            chain.proceed(new)
        }

        fun create(): MgmApi {


            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(auth)
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