package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json

import com.google.gson.annotations.SerializedName

data class MgmCurrentJson(
    @SerializedName("hadiseKodu") val condition: String?,
    @SerializedName("sicaklik") val temperature: Double?,
    @SerializedName("hissedilenSicaklik") val feelsLike: Double?,
    @SerializedName("nem") val humidity: Double?,
    @SerializedName("ruzgarHiz") val windSpeedMs: Double?,
    @SerializedName("ruzgarYon") val windDirection: Double?,
    @SerializedName("denizeIndirgenmisBasinc") val pressureMsl: Double?,
    @SerializedName("gorus") val visibility: Double?,
    @SerializedName("veriZamani") val time: String?
)

data class MgmDailyJson(
    @SerializedName("enDusukGun1") val minTempDay1: Double?,
    @SerializedName("enDusukGun2") val minTempDay2: Double?,
    @SerializedName("enDusukGun3") val minTempDay3: Double?,
    @SerializedName("enDusukGun4") val minTempDay4: Double?,
    @SerializedName("enDusukGun5") val minTempDay5: Double?,
    @SerializedName("enYuksekGun1") val maxTempDay1: Double?,
    @SerializedName("enYuksekGun2") val maxTempDay2: Double?,
    @SerializedName("enYuksekGun3") val maxTempDay3: Double?,
    @SerializedName("enYuksekGun4") val maxTempDay4: Double?,
    @SerializedName("enYuksekGun5") val maxTempDay5: Double?,
    @SerializedName("enDusukNemGun1") val minHumidityDay1: Double?,
    @SerializedName("enDusukNemGun2") val minHumidityDay2: Double?,
    @SerializedName("enDusukNemGun3") val minHumidityDay3: Double?,
    @SerializedName("enDusukNemGun4") val minHumidityDay4: Double?,
    @SerializedName("enDusukNemGun5") val minHumidityDay5: Double?,
    @SerializedName("enYuksekNemGun1") val maxHumidityDay1: Double?,
    @SerializedName("enYuksekNemGun2") val maxHumidityDay2: Double?,
    @SerializedName("enYuksekNemGun3") val maxHumidityDay3: Double?,
    @SerializedName("enYuksekNemGun4") val maxHumidityDay4: Double?,
    @SerializedName("enYuksekNemGun5") val maxHumidityDay5: Double?,
    @SerializedName("hadiseGun1") val conditionDay1: String?,
    @SerializedName("hadiseGun2") val conditionDay2: String?,
    @SerializedName("hadiseGun3") val conditionDay3: String?,
    @SerializedName("hadiseGun4") val conditionDay4: String?,
    @SerializedName("hadiseGun5") val conditionDay5: String?,
    @SerializedName("ruzgarHizGun1") val windSpeedDay1: Double?,
    @SerializedName("ruzgarHizGun2") val windSpeedDay2: Double?,
    @SerializedName("ruzgarHizGun3") val windSpeedDay3: Double?,
    @SerializedName("ruzgarHizGun4") val windSpeedDay4: Double?,
    @SerializedName("ruzgarHizGun5") val windSpeedDay5: Double?,
    @SerializedName("ruzgarYonGun1") val windDirectionDay1: Double?,
    @SerializedName("ruzgarYonGun2") val windDirectionDay2: Double?,
    @SerializedName("ruzgarYonGun3") val windDirectionDay3: Double?,
    @SerializedName("ruzgarYonGun4") val windDirectionDay4: Double?,
    @SerializedName("ruzgarYonGun5") val windDirectionDay5: Double?,
    @SerializedName("tarihGun1") val dateDay1: String?,
    @SerializedName("tarihGun2") val dateDay2: String?,
    @SerializedName("tarihGun3") val dateDay3: String?,
    @SerializedName("tarihGun4") val dateDay4: String?,
    @SerializedName("tarihGun5") val dateDay5: String?,

    @SerializedName("enDusukGun0") val minTempDay0: Double?,
    @SerializedName("enYuksekGun0") val maxTempDay0: Double?,
    @SerializedName("enDusukNemGun0") val minHumidityDay0: Double?,
    @SerializedName("enYuksekNemGun0") val maxHumidityDay0: Double?,
    @SerializedName("hadiseGun0") val conditionDay0: String?,
    @SerializedName("ruzgarHizGun0") val windSpeedDay0: Double?,
    @SerializedName("ruzgarYonGun0") val windDirectionDay0: Double?,
    @SerializedName("tarihGun0") val dateDay0: String?
)

data class MgmHourlyResultJson(
    @SerializedName("tahmin") val forecast: List<MgmHourlyForecastJson>?,
)

data class MgmHourlyForecastJson(
    @SerializedName("tarih") val time: String?,
    @SerializedName("hadise") val condition: String?,
    @SerializedName("sicaklik") val temperature: Double?,
    @SerializedName("hissedilenSicaklik") val feelsLike: Double?,
    @SerializedName("nem") val humidity: Double?,
    @SerializedName("ruzgarYonu") val windDirection: Double?,
    @SerializedName("ruzgarHizi") val windSpeed: Double?,
)