package com.pranshulgg.weathermaster.core.network.search.geonames

import com.google.gson.annotations.SerializedName

data class GeoNamesSearchDto(
    val geonames: List<GeoNamesSearchItemDto>
)

data class GeoNamesSearchItemDto(
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


data class GeoNamesTimezoneItemDto(
    val timezoneId: String
)

data class GeoNamesTimezoneItem(
    val timezone: String
)

fun GeoNamesTimezoneItemDto.toDomain(): GeoNamesTimezoneItem {
    return GeoNamesTimezoneItem(timezone = timezoneId)
}

