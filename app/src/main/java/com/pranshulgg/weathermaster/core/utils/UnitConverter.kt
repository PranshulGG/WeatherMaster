package com.pranshulgg.weathermaster.core.utils

import com.pranshulgg.weathermaster.core.model.DistanceUnits
import com.pranshulgg.weathermaster.core.model.PrecipitationUnits
import com.pranshulgg.weathermaster.core.model.PressureUnits
import com.pranshulgg.weathermaster.core.model.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.WindSpeedUnits

object UnitConverter {


    fun convertTemp(value: Double, from: TemperatureUnits, to: TemperatureUnits): Double = when {
        from == to -> value
        from == TemperatureUnits.CELSIUS -> (value * 9.0 / 5.0) + 32.0   // to F
        else -> (value - 32.0) * 5.0 / 9.0                    // to C
    }

    // base = meters
    private val distanceToMeters = mapOf(
        DistanceUnits.M to 1.0,
        DistanceUnits.KM to 1000.0,
        DistanceUnits.MI to 1609.34
    )

    // base = mps
    private val windToMps = mapOf(
        WindSpeedUnits.MPS to 1.0,
        WindSpeedUnits.KPH to 0.27778,
        WindSpeedUnits.MPH to 0.44704
    )

    // base = hPa
    private val pressureToHpa = mapOf(
        PressureUnits.HPA to 1.0,
        PressureUnits.INHG to 33.8639
    )

    // base = mm
    private val precipitationToMm = mapOf(
        PrecipitationUnits.MM to 1.0,
        PrecipitationUnits.CM to 10.0,
        PrecipitationUnits.INCH to 25.4
    )



    fun convertDistance(value: Double, from: DistanceUnits, to: DistanceUnits) =
        convertViaBase(value, from, to, distanceToMeters)

    fun convertWindSpeed(value: Double, from: WindSpeedUnits, to: WindSpeedUnits) =
        convertViaBase(value, from, to, windToMps)

    fun convertPressure(value: Double, from: PressureUnits, to: PressureUnits) =
        convertViaBase(value, from, to, pressureToHpa)

    fun convertPrecipitation(value: Double, from: PrecipitationUnits, to: PrecipitationUnits) =
        convertViaBase(value, from, to, precipitationToMm)

    private fun <T : Enum<T>> convertViaBase(
        value: Double,
        from: T,
        to: T,
        factors: Map<T, Double>
    ): Double {
        if (from == to) return value
        val base = value * (factors[from] ?: error("Unsupported: $from"))
        return base / (factors[to] ?: error("Unsupported: $to"))
    }
}