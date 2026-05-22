package com.pranshulgg.weather_master_app.data.local.mapper.airquality

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityCurrent
import com.pranshulgg.weather_master_app.data.local.entity.airquality.AirQualityWithRelations


fun AirQualityWithRelations.toDomain(): AirQuality {
    return AirQuality(
        current = AirQualityCurrent(
            usAqi = this.current?.usAqi,
            pm10 = this.current?.pm10,
            pm25 = this.current?.pm25,
            carbonMonoxide = this.current?.carbonMonoxide,
            nitrogenDioxide = this.current?.nitrogenDioxide,
            sulphurDioxide = this.current?.sulphurDioxide,
            ozone = this.current?.ozone,
            lastUpdatedInMilli = this.current?.lastUpdatedInMilli ?: System.currentTimeMillis()
        )
    )
}
