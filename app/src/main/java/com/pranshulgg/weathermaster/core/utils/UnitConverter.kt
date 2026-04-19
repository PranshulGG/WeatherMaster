package com.pranshulgg.weathermaster.core.utils

import kotlin.math.pow
import kotlin.math.round

// --- categories ---
enum class UnitType {
    LENGTH,
    TEMPERATURE,
    WIND_SPEED,
    PRESSURE,
    PRECIPITATION
}

// --- supported units ---
enum class Unit {
    // LENGTH
    CM, MM, INCH, METER,

    // TEMPERATURE
    CELSIUS, FAHRENHEIT,

    // WIND SPEED
    MPS, MPH,

    // PRESSURE
    HPA, INHG
}

// --- conversion rule ---
data class ConversionUnit(
    val type: UnitType,
    val toBaseFactor: Double, // convert to base unit
    val fromBaseFactor: Double
)

object UnitRegistry {

    // everything converts through a "base unit" per category

    val units = mapOf(
        // LENGTH (base = meter)
        Unit.METER to ConversionUnit(UnitType.LENGTH, 1.0, 1.0),
        Unit.CM to ConversionUnit(UnitType.LENGTH, 0.01, 100.0),
        Unit.MM to ConversionUnit(UnitType.LENGTH, 0.001, 1000.0),
        Unit.INCH to ConversionUnit(UnitType.LENGTH, 0.0254, 39.3701),

        // TEMPERATURE (special handling, base = Celsius)
        Unit.CELSIUS to ConversionUnit(UnitType.TEMPERATURE, 1.0, 1.0),
        Unit.FAHRENHEIT to ConversionUnit(UnitType.TEMPERATURE, 0.0, 0.0),

        // WIND SPEED (base = m/s)
        Unit.MPS to ConversionUnit(UnitType.WIND_SPEED, 1.0, 1.0),
        Unit.MPH to ConversionUnit(UnitType.WIND_SPEED, 0.44704, 2.23694),

        // PRESSURE (base = hPa)
        Unit.HPA to ConversionUnit(UnitType.PRESSURE, 1.0, 1.0),
        Unit.INHG to ConversionUnit(UnitType.PRESSURE, 33.8639, 0.02953)
    )
}

// --- main converter ---
object UnitConverter {

    private fun Double.round(decimals: Int = 1): Double {
        val factor = 10.0.pow(decimals)
        return kotlin.math.round(this * factor) / factor
    }

    fun convert(value: Double, from: Unit, to: Unit): Double {

        if (from == to) return value

        return when {

            // temperature gets its own logic
            from == Unit.CELSIUS && to == Unit.FAHRENHEIT ->
                ((value * 9 / 5) + 32).round(1)

            from == Unit.FAHRENHEIT && to == Unit.CELSIUS ->
                ((value - 32) * 5 / 9).round(1)

            // everything else goes through base unit
            else -> {
                val fromUnit = UnitRegistry.units[from]
                    ?: error("Unsupported unit: $from")
                val toUnit = UnitRegistry.units[to]
                    ?: error("Unsupported unit: $to")

                if (fromUnit.type != toUnit.type) {
                    error("Cannot convert between different unit types: ${fromUnit.type} > ${toUnit.type}")
                }

                val baseValue = value * fromUnit.toBaseFactor
                val result = baseValue / toUnit.toBaseFactor

                result.round(1)
            }
        }
    }
}
