package com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json

import com.google.gson.annotations.SerializedName

data class InmetDayJson(
    val uf: String? = null,
    val entidade: String? = null,
    val resumo: String? = null,
    val tempo: String? = null,
    @SerializedName("temp_max") val tempMax: Int? = null,
    @SerializedName("temp_min") val tempMin: Int? = null,
    @SerializedName("dir_vento") val dirVento: String? = null,
    @SerializedName("int_vento") val intVento: String? = null,
    @SerializedName("cod_icone") val codIcone: String? = null,
    val manha: InmetPeriodJson? = null,
    val tarde: InmetPeriodJson? = null,
    val noite: InmetPeriodJson? = null
)

data class InmetPeriodJson(
    val uf: String? = null,
    val entidade: String? = null,
    val resumo: String? = null,
    val tempo: String? = null,
    @SerializedName("temp_max") val tempMax: Int? = null,
    @SerializedName("temp_min") val tempMin: Int? = null,
    @SerializedName("dir_vento") val dirVento: String? = null,
    @SerializedName("int_vento") val intVento: String? = null,
    @SerializedName("cod_icone") val codIcone: String? = null
)
