package com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json

import com.google.gson.annotations.SerializedName

data class InmetStationJson(
    @SerializedName("CD_ESTACAO") val cdEstacao: String,
    @SerializedName("DC_NOME") val dcNome: String,
    @SerializedName("VL_LATITUDE") val vlLatitude: String,
    @SerializedName("VL_LONGITUDE") val vlLongitude: String,
    @SerializedName("SG_ESTADO") val sgEstado: String,
    @SerializedName("VL_ALTITUDE") val vlAltitude: String?,
    @SerializedName("CD_SITUACAO") val cdSituacao: String?,
    @SerializedName("TP_ESTACAO") val tpEstacao: String?,
    @SerializedName("FL_CAPITAL") val flCapital: String?
)

data class InmetHourlyEntryJson(
    @SerializedName("CD_ESTACAO") val cdEstacao: String? = null,
    @SerializedName("DT_MEDICAO") val dtMedicao: String? = null,
    @SerializedName("HR_MEDICAO") val hrMedicao: String? = null,
    @SerializedName("TEMP_AR") val tempAr: String? = null,
    @SerializedName("TEMP_MIN_HORA") val tempMinHora: String? = null,
    @SerializedName("TEMP_MAX_HORA") val tempMaxHora: String? = null,
    @SerializedName("UMID_REL_AR") val umidRelAr: String? = null,
    @SerializedName("UMID_MIN_HORA") val umidMinHora: String? = null,
    @SerializedName("VENT_VEL") val ventVel: String? = null,
    @SerializedName("VENT_DIR") val ventDir: String? = null,
    @SerializedName("VENT_RAJ_MAX") val ventRajMax: String? = null,
    @SerializedName("PRESS_ATM_EST") val pressAtmEst: String? = null,
    @SerializedName("PRECIPITACAO_TOTAL") val precipitacaoTotal: String? = null,
    @SerializedName("PREV_CHUVA") val prevChuva: String? = null
)
