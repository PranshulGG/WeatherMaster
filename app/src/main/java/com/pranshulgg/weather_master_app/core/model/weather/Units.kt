package com.pranshulgg.weather_master_app.core.model.weather

import android.content.Context
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit.KM
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit.M
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit.MI
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit.HPA
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit.INHG
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit.CELSIUS
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit.FAHRENHEIT
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit.KPH
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit.MPH
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit.MPS

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT;

    fun convert(value: Double?, to: TemperatureUnit): Double? {
        if (value == null) return null
        return when (this to to) {
            CELSIUS to FAHRENHEIT -> (value * 9 / 5) + 32
            FAHRENHEIT to CELSIUS -> (value - 32) * 5 / 9
            else -> value
        }
    }
}

enum class WindSpeedUnit {
    MPS,
    MPH,
    KPH;

    fun convert(value: Double?, to: WindSpeedUnit): Double? {
        if (value == null) return null
        return when (this to to) {
            KPH to MPH -> (value / 1.609)
            KPH to MPS -> (value / 3.6)
            MPH to KPH -> (value * 1.609)
            MPH to MPS -> (value / 2.237)
            MPS to KPH -> (value * 3.6)
            MPS to MPH -> (value * 2.237)
            else -> value
        }
    }
}

enum class PressureUnit {
    HPA,

    INHG,
    MMHG;

    fun convert(value: Double?, to: PressureUnit): Double? {
        if (value == null) return null
        return when (this to to) {
            HPA to INHG -> (value * 0.02953)
            HPA to MMHG -> (value * 0.75006)
            INHG to HPA -> (value * 33.8639)
            INHG to MMHG -> (value * 25.4)
            MMHG to INHG -> (value / 25.4)
            MMHG to HPA -> (value * 1.33322)

            else -> value
        }
    }
}

enum class DistanceUnit {
    KM,
    MI,
    M;


    fun convert(value: Double?, to: DistanceUnit): Double? {
        if (value == null) return null
        return when (this to to) {
            M to KM -> (value / 1000)
            M to MI -> (value / 1609)
            MI to M -> (value * 1609)
            MI to KM -> (value * 1.609)
            KM to MI -> (value / 1.609)
            KM to M -> (value * 1000)
            else -> value
        }
    }
}

enum class PrecipitationUnit {
    MM,
    INCH,
    CM;

    fun convert(value: Double?, to: PrecipitationUnit): Double? {
        if (value == null) return null
        return when (this to to) {
            MM to INCH -> (value / 25.4)
            MM to CM -> (value / 10)
            INCH to MM -> (value * 25.4)
            INCH to CM -> (value * 2.54)
            CM to INCH -> (value / 2.54)
            CM to MM -> (value * 10)
            else -> value
        }
    }
}


fun TemperatureUnit.toName(context: Context): String {
    return when (this) {
        CELSIUS -> context.getString(R.string.unit_temperature_celsius)
        FAHRENHEIT -> context.getString(R.string.unit_temperature_fahrenheit)
    }
}

fun WindSpeedUnit.toName(context: Context, inShort: Boolean = false): String {
    return when (this) {
        MPS -> if (inShort) "m/s" else context.getString(R.string.unit_wind_mps)
        MPH -> if (inShort) "mi/h" else context.getString(R.string.unit_wind_mph)
        KPH -> if (inShort) "km/h" else context.getString(R.string.unit_wind_kph)
    }
}

fun PressureUnit.toName(inShort: Boolean = false, context: Context): String {
    return when (this) {
        HPA -> if (inShort) "hPa" else context.getString(R.string.unit_pressure_hpa)
        INHG -> if (inShort) "inHG" else context.getString(R.string.unit_pressure_inhg)
        PressureUnit.MMHG -> if (inShort) "mmHG" else context.getString(R.string.unit_pressure_mmhg)
    }
}

fun DistanceUnit.toName(inShort: Boolean = false, context: Context): String {
    return when (this) {
        KM -> if (inShort) "km" else context.getString(R.string.unit_distance_km)
        MI -> if (inShort) "mi" else context.getString(R.string.unit_distance_mi)
        M -> if (inShort) "m" else context.getString(R.string.unit_distance_m)
    }
}

fun PrecipitationUnit.toName(context: Context, inShort: Boolean = false): String {
    return when (this) {
        PrecipitationUnit.MM -> if (inShort) "mm" else context.getString(R.string.unit_precipitation_mm)
        PrecipitationUnit.INCH -> if (inShort) "in" else context.getString(R.string.unit_precipitation_inch)
        PrecipitationUnit.CM -> if (inShort) "cm" else context.getString(R.string.unit_precipitation_cm)
    }
}