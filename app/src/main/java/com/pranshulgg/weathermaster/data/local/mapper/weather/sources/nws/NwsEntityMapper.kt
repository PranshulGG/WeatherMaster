package com.pranshulgg.weathermaster.data.local.mapper.weather.sources.nws

import com.pranshulgg.weathermaster.core.model.domain.location.Location
import com.pranshulgg.weathermaster.core.model.domain.weather.nws.NwsGridPoints
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsGridPointsJson
import com.pranshulgg.weathermaster.data.local.entity.weather.nws.NwsGridPointsEntity

// ---------------------------- DOMAIN TO ENTITY ----------------------------

fun NwsGridPoints.toEntity(location: Location): NwsGridPointsEntity {
    return NwsGridPointsEntity(
        locationId = location.id,
        officeId = this.officeId,
        gridX = this.gridX,
        gridY = this.gridY,
        stationIdentifier = stationIdentifier!!, // CAN'T BE NULL, WE NEED THIS :P
        lastUpdatedMilli = lastUpdatedMilli
    )
}

// ---------------------------- ENTITY TO DOMAIN ----------------------------

fun NwsGridPointsEntity.toDomain(): NwsGridPoints {
    return NwsGridPoints(
        locationId = this.locationId,
        officeId = this.officeId,
        gridX = this.gridX,
        gridY = this.gridY,
        stationIdentifier = this.stationIdentifier,
        lastUpdatedMilli = this.lastUpdatedMilli
    )
}