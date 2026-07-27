package com.pranshulgg.weather_master_app.data.local.mapper.airquality.sources.openmeteo

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityCurrent
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityHourly
import com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo.OpenMeteoAqiDto


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