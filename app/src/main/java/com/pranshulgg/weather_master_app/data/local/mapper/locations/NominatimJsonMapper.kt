package com.pranshulgg.weather_master_app.data.local.mapper.locations

import com.pranshulgg.weather_master_app.core.model.domain.location.Address
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json.NominatimAddressJson


fun NominatimAddressJson.toDomain(): Address {
    val city =
        this.address.city ?: this.address.suburb ?: this.address.village ?: this.address.county
        ?: this.address.road ?: this.address.stateDistrict ?: this.address.state


    return Address(
        city = city,
        country = this.address.country,
        countryCode = this.address.countryCode
    )
}