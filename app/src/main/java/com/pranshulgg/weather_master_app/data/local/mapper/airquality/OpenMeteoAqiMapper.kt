package com.pranshulgg.weather_master_app.data.local.mapper.airquality

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityCurrent
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityHourly
import com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo.OpenMeteoAqiDto
import com.pranshulgg.weather_master_app.data.local.entity.airquality.CurrentAirQualityEntity
import com.pranshulgg.weather_master_app.data.local.entity.airquality.HourlyAirQualityEntity

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
        ),
        hourly = List(hourly.time.size) {
            AirQualityHourly(
                time = hourly.time[it],
                pm10 = hourly.pm10[it],
                pm25 = hourly.pm25[it],
                carbonMonoxide = hourly.carbonMonoxide[it],
                nitrogenDioxide = hourly.carbonMonoxide[it],
                sulphurDioxide = hourly.sulphurDioxide[it],
                ozone = hourly.ozone[it]
            )
        }
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