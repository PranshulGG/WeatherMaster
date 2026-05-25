package com.pranshulgg.weather_master_app.core.network.github


import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit


data class GithubRepoDataJson(
    val prerelease: Boolean,
    @SerializedName("tag_name")
    val tagName: String,

    @SerializedName("html_url")
    val htmlUrl: String
)

interface GithubApi {


    @GET("PranshulGG/WeatherMaster/releases")
    suspend fun fetchGithubRepos(): Response<List<GithubRepoDataJson>>

    companion object {
        const val BASE_URL =
            "https://api.github.com/repos/"

        fun create(): GithubApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }


            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubApi::class.java)
        }
    }

}