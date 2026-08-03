package com.pranshulgg.weather_master_app.core.network.sources.search.accu.json

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


data class AccuSearchJson(
    @SerializedName("EnglishName") val name: String,
    @SerializedName("Country") val country: AccuSearchCountryJson,
    @SerializedName("AdministrativeArea") val administrativeArea: AccuSearchAdministrativeAreaJson,
    @SerializedName("TimeZone") val timezone: AccuSearchTimeZoneJson,
    @SerializedName("GeoPosition") val geoPosition: AccuSearchGeoPositionJson
)

data class AccuSearchCountryJson(
    @SerializedName("EnglishName") val name: String,
    @SerializedName("ID") val countryCode: String
)

data class AccuSearchAdministrativeAreaJson(
    @SerializedName("EnglishName") val name: String?
)

data class AccuSearchTimeZoneJson(
    @SerializedName("Name") val name: String
)

data class AccuSearchGeoPositionJson(
    @SerializedName("Latitude") val latitude: Double,
    @SerializedName("Longitude") val longitude: Double
)