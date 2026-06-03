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
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit.BFT
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit.Companion.beaufortToMps
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit.KPH
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit.KT
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit.MPH
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit.MPS


enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT;

    fun convert(value: Double?, to: TemperatureUnit): Double? {
        if (value == null) return null
        if (this == to) return value

        val celsius = when (this) {
            CELSIUS -> value
            FAHRENHEIT -> (value - 32) * 5 / 9
        }

        return when (to) {
            CELSIUS -> celsius
            FAHRENHEIT -> (celsius * 9 / 5) + 32
        }
    }
}

enum class WindSpeedUnit {
    MPS,
    MPH,
    KPH,

    BFT,
    KT;

    fun convert(value: Double?, to: WindSpeedUnit): Double? {
        if (value == null) return null
        if (this == to) return value

        val mps = when (this) {
            MPS -> value
            KPH -> value / 3.6
            MPH -> value / 2.237
            KT -> value / 1.943844
            BFT -> beaufortToMps(value)
        }

        return when (to) {
            MPS -> mps
            KPH -> mps * 3.6
            MPH -> mps * 2.237
            KT -> mps * 1.943844
            BFT -> mpsToBeaufort(mps).toDouble()
        }

    }

    companion object {
        private fun beaufortToMps(bft: Double): Double = when (bft.toInt()) {
            0 -> 0.0
            1 -> 0.9
            2 -> 2.4
            3 -> 4.4
            4 -> 6.7
            5 -> 9.3
            6 -> 12.3
            7 -> 15.5
            8 -> 18.9
            9 -> 22.6
            10 -> 26.4
            11 -> 30.5
            else -> 32.7
        }

        private fun mpsToBeaufort(mps: Double): Int = when {
            mps < 0.3 -> 0
            mps < 1.6 -> 1
            mps < 3.4 -> 2
            mps < 5.5 -> 3
            mps < 8.0 -> 4
            mps < 10.8 -> 5
            mps < 13.9 -> 6
            mps < 17.2 -> 7
            mps < 20.8 -> 8
            mps < 24.5 -> 9
            mps < 28.5 -> 10
            mps < 32.7 -> 11
            else -> 12
        }
    }
}

enum class PressureUnit {
    HPA,

    INHG,
    MMHG;

    fun convert(value: Double?, to: PressureUnit): Double? {
        if (value == null) return null
        if (this == to) return value

        val hpa = when (this) {
            HPA -> value
            INHG -> value * 33.8639
            MMHG -> value * 1.33322
        }

        return when (to) {
            HPA -> hpa
            INHG -> hpa * 0.02953
            MMHG -> hpa * 0.75006
        }
    }
}

enum class DistanceUnit {
    KM,
    MI,
    M;


    fun convert(value: Double?, to: DistanceUnit): Double? {
        if (value == null) return null
        if (this == to) return value
        val meters = when (this) {
            M -> value
            KM -> value * 1000
            MI -> value * 1609
        }

        return when (to) {
            M -> meters
            KM -> meters / 1000
            MI -> meters / 1609
        }
    }
}

enum class PrecipitationUnit {
    MM,
    INCH,
    CM;

    fun convert(value: Double?, to: PrecipitationUnit): Double? {
        if (value == null) return null
        if (this == to) return value

        val mm = when (this) {
            MM -> value
            CM -> value * 10
            INCH -> value * 25.4
        }

        return when (to) {
            MM -> mm
            CM -> mm / 10
            INCH -> mm / 25.4
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
        KT -> if (inShort) "kt" else context.getString(R.string.unit_wind_kt)
        BFT -> if (inShort) "bft" else context.getString(R.string.unit_wind_bft)
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