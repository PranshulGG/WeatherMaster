package com.pranshulgg.weather_master_app.core.model.weather.rain

/**
 * One high-resolution precipitation sample.
 *
 * @param time             UTC epoch milliseconds of the interval start
 * @param intensityMmPerHour  rain rate in mm/h (ALWAYS MM/H internally, convert for display)
 * @param intervalMinutes  length of the interval this sample covers (5 for radar nowcast, 15 for model tail)
 */
data class RainNowcastPoint(
    val time: Long,
    val intensityMmPerHour: Double,
    val intervalMinutes: Int
)

/**
 * High-resolution rain nowcast for the coming ~2 hours.
 *
 * Points come from the best available source:
 * - KNMI radar_forecast_2.0 5-minute radar extrapolation via WMS (NL)
 * - DWD Radarkomposit RV 5-minute radar extrapolation via Bright Sky (DE)
 */
data class RainNowcast(
    val points: List<RainNowcastPoint>,
    val providerName: String,
    val isRadarBased: Boolean
) {
    /** Total expected accumulation in mm over the covered window. */
    fun totalMillimeters(): Double {
        return points.sumOf { it.intensityMmPerHour * it.intervalMinutes / 60.0 }
    }

    fun maxIntensity(): Double {
        return points.maxOfOrNull { it.intensityMmPerHour } ?: 0.0
    }
}