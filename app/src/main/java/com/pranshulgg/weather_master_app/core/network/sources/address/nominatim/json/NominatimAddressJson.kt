package com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json

import com.google.gson.annotations.SerializedName


data class NominatimAddressJson(
    val address: NominatimAddressDataJson
)

data class NominatimAddressDataJson(
    val city: String?,

    @SerializedName("state_district")
    val stateDistrict: String?,

    val county: String?,
    val road: String?,
    val village: String?,
    val suburb: String?,

    val state: String?,
    val country: String,

    @SerializedName("country_code") val countryCode: String
)