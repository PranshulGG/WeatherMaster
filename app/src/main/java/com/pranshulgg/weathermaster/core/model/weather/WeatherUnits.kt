package com.pranshulgg.weathermaster.core.model.weather

import android.content.Context
import com.pranshulgg.weathermaster.R

enum class TemperatureUnits {
    CELSIUS, FAHRENHEIT
}

enum class WindSpeedUnits {
    MPS, MPH, KPH
}

enum class PressureUnits {
    HPA, INHG
}

enum class DistanceUnits {
    KM, MI, M
}

enum class PrecipitationUnits {
    MM, INCH, CM
}


fun TemperatureUnits.toName(context: Context): String {
    return when (this) {
        TemperatureUnits.CELSIUS -> context.getString(R.string.unit_temperature_celsius)
        TemperatureUnits.FAHRENHEIT -> context.getString(R.string.unit_temperature_fahrenheit)
    }
}

fun WindSpeedUnits.toName(context: Context): String {
    return when (this) {
        WindSpeedUnits.MPS -> context.getString(R.string.unit_wind_mps)
        WindSpeedUnits.MPH -> context.getString(R.string.unit_wind_mph)
        WindSpeedUnits.KPH -> context.getString(R.string.unit_wind_kph)
    }
}

fun PressureUnits.toName(inShort: Boolean = false, context: Context): String {
    return when (this) {
        PressureUnits.HPA -> if (inShort) "hPa" else context.getString(R.string.unit_pressure_hpa)
        PressureUnits.INHG -> if (inShort) "inHG" else context.getString(R.string.unit_pressure_inhg)
    }
}

fun DistanceUnits.toName(inShort: Boolean = false, context: Context): String {
    return when (this) {
        DistanceUnits.KM -> if (inShort) "Km" else context.getString(R.string.unit_distance_km)
        DistanceUnits.MI -> if (inShort) "Mi" else context.getString(R.string.unit_distance_mi)
        DistanceUnits.M -> if (inShort) "M" else context.getString(R.string.unit_distance_m)
    }
}

fun PrecipitationUnits.toName(context: Context): String {
    return when (this) {
        PrecipitationUnits.MM -> context.getString(R.string.unit_precipitation_mm)
        PrecipitationUnits.INCH -> context.getString(R.string.unit_precipitation_inch)
        PrecipitationUnits.CM -> context.getString(R.string.unit_precipitation_cm)
    }
}