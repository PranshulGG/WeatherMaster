package com.pranshulgg.weather_master_app.core.network.sources.weather.aemet

import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json.AemetDailyForecastRootJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json.AemetHourlyForecastRootJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json.AemetMunicipioJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.model.AemetEnvelopeJson
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.concurrent.TimeUnit


interface AemetApi {

    @GET("prediccion/especifica/municipio/diaria/{municipio}")
    suspend fun fetchDailyForecastEnvelope(
        @Path("municipio") municipio: String,
        @Query("api_key") apiKey: String
    ): Response<AemetEnvelopeJson>

    @GET("prediccion/especifica/municipio/horaria/{municipio}")
    suspend fun fetchHourlyForecastEnvelope(
        @Path("municipio") municipio: String,
        @Query("api_key") apiKey: String
    ): Response<AemetEnvelopeJson>

    @GET("maestro/municipios")
    suspend fun fetchMunicipiosEnvelope(
        @Query("api_key") apiKey: String
    ): Response<AemetEnvelopeJson>

    // Step 2: the envelope's `datos` url is an absolute, unauthenticated, short-lived url.
    // Content-Type on these is "text/plain;charset=ISO-8859-15" - Retrofit's Gson converter
    // reads via OkHttp's ResponseBody.charStream(), which honors that declared charset, so no
    // manual decoding is needed as long as this Content-Type header survives (don't add an
    // interceptor that rewrites/strips it).
    @GET
    suspend fun fetchDailyForecastData(@Url url: String): Response<List<AemetDailyForecastRootJson>>

    @GET
    suspend fun fetchHourlyForecastData(@Url url: String): Response<List<AemetHourlyForecastRootJson>>

    @GET
    suspend fun fetchMunicipiosData(@Url url: String): Response<List<AemetMunicipioJson>>

    companion object {
        const val BASE_URL = "https://opendata.aemet.es/opendata/api/"

        fun create(): AemetApi {

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AemetApi::class.java)
        }
    }
}
