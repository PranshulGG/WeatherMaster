package com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json

import com.google.gson.annotations.SerializedName

data class InmetAvisoJson(
    val id: Long? = null,
    @SerializedName("id_aviso") val idAviso: Long? = null,
    @SerializedName("id_sequencia") val idSequencia: Int? = null,
    @SerializedName("id_severidade") val idSeveridade: Int? = null,
    val severidade: String? = null,
    val descricao: String? = null,
    val riscos: String? = null,
    val instrucoes: String? = null,
    val geocodes: String? = null,
    val municipios: String? = null,
    val estados: String? = null,
    @SerializedName("aviso_cor") val avisoCor: String? = null,
    val inicio: String? = null,
    val fim: String? = null,
    @SerializedName("data_inicio") val dataInicio: String? = null,
    @SerializedName("hora_inicio") val horaInicio: String? = null,
    @SerializedName("data_fim") val dataFim: String? = null,
    @SerializedName("hora_fim") val horaFim: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    val encerrado: Boolean? = null,
    val alterado: Boolean? = null
)
