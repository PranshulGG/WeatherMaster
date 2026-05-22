package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.nws

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.nws.NwsGridPoints
import com.pranshulgg.weather_master_app.data.local.entity.weather.nws.NwsGridPointsEntity

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