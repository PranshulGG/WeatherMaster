package com.pranshulgg.weather_master_app.core.network.sources.search.geonames.json

import com.google.gson.annotations.SerializedName


data class GeoNamesSearchJson(
    val geonames: List<GeoNamesSearchItemJson>
)

data class GeoNamesSearchItemJson(
    @SerializedName("lat")
    val latitude: Double,

    @SerializedName("lng")
    val longitude: Double,

    val name: String,
    val countryName: String,

    @SerializedName("countryCode")
    val countryCode: String,

    @SerializedName("adminName1")
    val state: String?

)


data class GeoNamesTimezoneItemJson(
    val timezoneId: String
)

data class GeoNamesTimezoneItem(
    val timezone: String
)

fun GeoNamesTimezoneItemJson.toDomain(): GeoNamesTimezoneItem {
    return GeoNamesTimezoneItem(timezone = timezoneId)
}

