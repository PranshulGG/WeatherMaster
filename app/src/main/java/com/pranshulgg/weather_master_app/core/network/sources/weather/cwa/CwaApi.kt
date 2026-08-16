package com.pranshulgg.weather_master_app.core.network.sources.weather.cwa

import com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.json.CwaDatasetJson
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface CwaApi {

    // Covers all call shapes this source needs: the nationwide county-centroid list
    // (datasetId = CwaCountyEndpoints.NATIONWIDE_SHORT_RANGE, no locationName), an unfiltered
    // per-county call (returns every township in that county), and a filtered per-county call
    // (returns just one township) - short-range and weekly datasets share the same envelope shape.
    // An invalid/missing key returns HTTP 401 with a plain-text (non-JSON) body, so this must
    // return Response<T> and be checked for .code() == 401 before .body() is touched, or Gson
    // throws trying to parse the plain-text error body.
    @GET("{datasetId}")
    suspend fun fetchDataset(
        @Path("datasetId") datasetId: String,
        @Query("Authorization") apiKey: String,
        @Query("format") format: String = "JSON",
        @Query("LocationName") locationName: String? = null
    ): Response<CwaDatasetJson>

    companion object {
        const val BASE_URL = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/"

        fun create(): CwaApi {

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CwaApi::class.java)
        }
    }
}
