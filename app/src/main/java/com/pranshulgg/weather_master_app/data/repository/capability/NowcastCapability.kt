package com.pranshulgg.weather_master_app.data.repository.capability

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcast
import java.time.Instant

/**
 * Capability for the short-term (~2 h) rain nowcast, following the same
 * pattern as [WeatherCapability], [AlertCapability] and [AirQualityCapability].
 *
 * Coverage knowledge lives with the source's own implementation: [covers]
 * decides whether the source can serve nowcast data for a location at all,
 * so call sites never hardcode providers or geographies.
 */
interface NowcastCapability {

    /** Whether this source has rain radar/nowcast coverage for [location]. */
    fun covers(location: Location): Boolean = true

    /**
     * The nowcast, or null when no data could be produced for [location]
     * (callers move on to the next candidate source).
     */
    suspend fun fetchNowcast(
        location: Location,
        now: Instant
    ): RainNowcast?
}