package com.pranshulgg.weather_master_app.data.repository.rain

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcast
import java.time.Instant

/**
 * Rain nowcast feed of one source, resolved the same way as
 * [com.pranshulgg.weather_master_app.data.repository.weather.WeatherRepository],
 * [com.pranshulgg.weather_master_app.data.repository.alerts.AlertRepository]
 * and [com.pranshulgg.weather_master_app.data.repository.airquality.AirQualityRepository]:
 * a repository returns its [NowcastCapability] implementation, or not at all
 * when the source does not provide rain nowcast data.
 */
interface NowcastRepository {

    /** The source this repository serves nowcast data for (NONE when it doesn't). */
    val nowcastSource: Source

    /**
     * The nowcast for [location], or null when this source provides no
     * nowcast for it (no coverage, disabled capability or fetch failure).
     */
    suspend fun getNowcast(
        location: Location,
        now: Instant = Instant.now()
    ): RainNowcast?
}