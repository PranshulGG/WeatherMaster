package com.pranshulgg.weathermaster.data.local.mapper.weather.sources.nws

import com.pranshulgg.weathermaster.core.model.domain.location.Location
import com.pranshulgg.weathermaster.core.model.domain.weather.nws.NwsGridPoints
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsForecastJson
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsGridPointsJson

// ---------------------------- JSON TO DOMAIN ----------------------------

fun NwsGridPointsJson.toDomain(location: Location, stationIdentifier: String?): NwsGridPoints {
    return NwsGridPoints(
        locationId = location.id,
        officeId = this.points.officeId,
        gridX = this.points.gridX,
        gridY = this.points.gridY,
        stationIdentifier = stationIdentifier,
        lastUpdatedMilli = System.currentTimeMillis()
    )
}

fun NwsForecastJson.toDomain() {

}