package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json

import com.google.gson.annotations.SerializedName

// MGM returns "Gun0" as a duplicate of "Gun1" (same date, same values) rather than a distinct
// 6th day - confirmed live. The 5 distinct forecast days are Gun1..Gun5, so Gun0 is skipped here.
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
    // NOTE: these carry a 'Z' suffix but are actually Europe/Istanbul local time, not UTC -
    // confirmed live and matches a note in the breezy-weather MGM source. Parse as local time.
    @SerializedName("tarihGun1") val dateDay1: String?,
    @SerializedName("tarihGun2") val dateDay2: String?,
    @SerializedName("tarihGun3") val dateDay3: String?,
    @SerializedName("tarihGun4") val dateDay4: String?,
    @SerializedName("tarihGun5") val dateDay5: String?,
)
