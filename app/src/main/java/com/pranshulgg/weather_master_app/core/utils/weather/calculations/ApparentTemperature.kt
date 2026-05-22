package com.pranshulgg.weather_master_app.core.utils.weather.calculations

import kotlin.math.exp


/**
 * Used when a weather source doesn't provide ApparentTemperature
 * Source: https://www.vcalc.com/wiki/australian-apparent-temperature
 */
fun computeApparentTemperature(
    tempC: Double?,
    humidity: Double?,
    windMs: Double?
): Double? {

    if (tempC == null || humidity == null || windMs == null) {
        return null
    }
    // Estimate moisture in the air
    val vaporPressure =
        (humidity / 100.0) *
                6.105 *
                exp((17.27 * tempC) / (237.7 + tempC)) // hotter air can support WAY more moisture

    return tempC +
            0.33 * vaporPressure -
            0.70 * windMs -
            4.0
}