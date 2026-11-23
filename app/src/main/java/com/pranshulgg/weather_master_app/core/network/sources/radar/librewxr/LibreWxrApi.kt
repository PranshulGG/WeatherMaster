package com.pranshulgg.weather_master_app.core.network.sources.radar.librewxr

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import java.util.concurrent.TimeUnit

/**
 * LibreWXR (https://librewxr.net, data CC BY 4.0): worldwide radar composites where radar exists,
 * satellite precipitation estimates and forecast models elsewhere. RainViewer v2 compatible API
 */
interface LibreWxrApi {

    @GET("public/weather-maps.json")
    suspend fun fetchWeatherMaps(): Response<LibreWxrWeatherMapsJson>

    // e.g. /v2/radar/1794757800/512/9/123/195/6/1_0.png
    @Streaming
    @GET("{path}/{size}/{zoom}/{x}/{y}/{color}/{options}.png")
    suspend fun fetchTile(
        @Path("path", encoded = true) path: String,
        @Path("size") size: Int,
        @Path("zoom") zoom: Int,
        @Path("x") x: Int,
        @Path("y") y: Int,
        @Path("color") color: Int,
        @Path("options") options: String
    ): Response<ResponseBody>

    companion object {
        const val BASE_URL = "https://api.librewxr.net/"

        fun create(): LibreWxrApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LibreWxrApi::class.java)
        }
    }
}

data class LibreWxrWeatherMapsJson(
    @SerializedName("radar") val radar: LibreWxrRadarJson? = null
)

data class LibreWxrRadarJson(
    @SerializedName("past") val past: List<LibreWxrFrameJson>? = null
)

data class LibreWxrFrameJson(
    @SerializedName("time") val time: Long? = null,
    @SerializedName("path") val path: String? = null
)
