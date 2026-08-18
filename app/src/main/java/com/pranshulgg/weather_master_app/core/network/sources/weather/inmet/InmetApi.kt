package com.pranshulgg.weather_master_app.core.network.sources.weather.inmet

import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json.IbgeMunicipioJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json.InmetDayJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json.InmetHourlyEntryJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json.InmetStationJson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit


interface InmetForecastApi {

    @GET("previsao/{ibgeCode}")
    suspend fun fetchForecast(
        @Path("ibgeCode") ibgeCode: String
    ): Response<Map<String, Map<String, InmetDayJson>>>

    companion object {
        const val BASE_URL = "https://apiprevmet3.inmet.gov.br/"

        fun create(): InmetForecastApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(InmetForecastApi::class.java)
        }
    }
}


interface InmetObservationApi {

    @GET("estacoes/T")
    suspend fun fetchStations(): Response<List<InmetStationJson>>

    @GET("estacao/{dataInicio}/{dataFim}/{stationCode}")
    suspend fun fetchHourlyData(
        @Path("dataInicio") dataInicio: String,
        @Path("dataFim") dataFim: String,
        @Path("stationCode") stationCode: String
    ): Response<List<InmetHourlyEntryJson>>

    companion object {
        const val BASE_URL = "https://apitempo.inmet.gov.br/"

        fun create(): InmetObservationApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(InmetObservationApi::class.java)
        }
    }
}


interface IbgeApi {

    @GET("localidades/estados/{uf}/municipios")
    suspend fun fetchMunicipiosByState(
        @Path("uf") uf: String
    ): Response<List<IbgeMunicipioJson>>

    companion object {
        const val BASE_URL = "https://servicodados.ibge.gov.br/api/v1/"

        fun create(): IbgeApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(IbgeApi::class.java)
        }
    }
}
