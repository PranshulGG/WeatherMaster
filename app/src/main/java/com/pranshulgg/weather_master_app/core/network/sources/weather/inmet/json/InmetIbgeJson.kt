package com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json

import com.google.gson.annotations.SerializedName

data class IbgeMunicipioJson(
    val id: Int,
    val nome: String,
    val microrregiao: IbgeMicrorregiaoJson? = null
)

data class IbgeMicrorregiaoJson(
    val nome: String? = null,
    val uf: IbgeUfJson? = null
)

data class IbgeUfJson(
    val id: Int? = null,
    val sigla: String? = null,
    val nome: String? = null
)
