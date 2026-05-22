package com.pranshulgg.weather_master_app.data.local.entity.airquality

import androidx.room.Embedded

data class AirQualityWithRelations(
    @Embedded val current: CurrentAirQualityEntity?
)