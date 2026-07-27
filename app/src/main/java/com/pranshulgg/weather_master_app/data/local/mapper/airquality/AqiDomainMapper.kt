package com.pranshulgg.weather_master_app.data.local.mapper.airquality

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityCurrent
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityHourly
import com.pranshulgg.weather_master_app.data.local.entity.airquality.CurrentAirQualityEntity
import com.pranshulgg.weather_master_app.data.local.entity.airquality.HourlyAirQualityEntity


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

fun AirQualityHourly.toEntity(locationId: String): HourlyAirQualityEntity =
    HourlyAirQualityEntity(
        locationId = locationId,
        pm10 = pm10,
        pm25 = pm25,
        carbonMonoxide = carbonMonoxide,
        nitrogenDioxide = nitrogenDioxide,
        sulphurDioxide = sulphurDioxide,
        ozone = ozone,
        time = time
    )