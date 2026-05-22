package com.pranshulgg.weather_master_app.core.model.weather

import android.content.Context
import com.pranshulgg.weather_master_app.R

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

fun WindSpeedUnits.toName(context: Context, inShort: Boolean = false): String {
    return when (this) {
        WindSpeedUnits.MPS -> if (inShort) "m/s" else context.getString(R.string.unit_wind_mps)
        WindSpeedUnits.MPH -> if (inShort) "mi/h" else context.getString(R.string.unit_wind_mph)
        WindSpeedUnits.KPH -> if (inShort) "km/h" else context.getString(R.string.unit_wind_kph)
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
        DistanceUnits.KM -> if (inShort) "km" else context.getString(R.string.unit_distance_km)
        DistanceUnits.MI -> if (inShort) "mi" else context.getString(R.string.unit_distance_mi)
        DistanceUnits.M -> if (inShort) "m" else context.getString(R.string.unit_distance_m)
    }
}

fun PrecipitationUnits.toName(context: Context, inShort: Boolean = false): String {
    return when (this) {
        PrecipitationUnits.MM -> if (inShort) "mm" else context.getString(R.string.unit_precipitation_mm)
        PrecipitationUnits.INCH -> if (inShort) "in" else context.getString(R.string.unit_precipitation_inch)
        PrecipitationUnits.CM -> if (inShort) "cm" else context.getString(R.string.unit_precipitation_cm)
    }
}