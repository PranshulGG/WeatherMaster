package com.pranshulgg.weather_master_app.core.network.sources.weather.brightsky

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Radar point-extraction endpoint of Bright Sky (https://brightsky.dev).
 * Serves the DWD "Radarkomposit RV" product: 5-minute precipitation nowcast
 * frames (observed + extrapolated) covering all of Germany.
 *
 * Each frame contains a zlib-compressed grid of int16 values
 * (0.01 mm per 5 minutes per unit); the grid can be cropped to a small
 * window around the requested point with the [PARAM_*_LAT]/[PARAM_*_LON]
 * parameters, and [BrightSkyRadarJson.latLonPosition] gives the exact
 * pixel position of the requested coordinate inside that crop.
 */
interface BrightSkyRadarApi {

    @GET("radar")
    suspend fun fetchRadar(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("date") date: String,
        @Query("max_lat") maxLat: Double,
        @Query("min_lat") minLat: Double,
        @Query("max_lon") maxLon: Double,
        @Query("min_lon") minLon: Double
    ): Response<BrightSkyRadarJson>

    companion object {
        const val BASE_URL = "https://api.brightsky.dev/"

        /** Half-size of the crop window around the requested point, in degrees. */
        const val CROP_HALF_DEGREES = 0.02

        fun create(): BrightSkyRadarApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BrightSkyRadarApi::class.java)
        }
    }
}

data class BrightSkyRadarJson(
    val radar: List<BrightSkyRadarFrame>?,
    val bbox: List<Int>?,
    val latlon_position: BrightSkyLatLonPosition?
)

data class BrightSkyRadarFrame(
    val timestamp: String?,
    /** base64-encoded zlib-compressed int16 grid */
    val precipitation_5: String?
)

data class BrightSkyLatLonPosition(
    val x: Double?,
    val y: Double?
)