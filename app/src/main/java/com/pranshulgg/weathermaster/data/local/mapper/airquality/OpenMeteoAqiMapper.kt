package com.pranshulgg.weathermaster.data.local.mapper.airquality

import com.pranshulgg.weathermaster.core.model.domain.airquality.AirQuality
import com.pranshulgg.weathermaster.core.model.domain.airquality.AirQualityCurrent
import com.pranshulgg.weathermaster.core.network.sources.airquality.openmeteo.OpenMeteoAqiDto
import com.pranshulgg.weathermaster.data.local.entity.airquality.CurrentAirQualityEntity

fun OpenMeteoAqiDto.toDomain(): AirQuality =
    AirQuality(
        current = AirQualityCurrent(
            usAqi = this.current.usAqi,
            pm10 = this.current.pm10,
            pm25 = this.current.pm25,
            carbonMonoxide = this.current.carbonMonoxide,
            nitrogenDioxide = this.current.nitrogenDioxide,
            sulphurDioxide = this.current.sulphurDioxide,
            ozone = this.current.ozone,
            lastUpdatedInMilli = System.currentTimeMillis()
        )
    )


fun AirQualityCurrent.toEntity(locationId: String): CurrentAirQualityEntity =
    CurrentAirQualityEntity(
        locationId = locationId,
        usAqi = usAqi,
        pm10 = pm10,
        pm25 = pm25,
        carbonMonoxide = carbonMonoxide,
        nitrogenDioxide = nitrogenDioxide,
        sulphurDioxide = sulphurDioxide,
        ozone = ozone,
        lastUpdatedInMilli = lastUpdatedInMilli
    )