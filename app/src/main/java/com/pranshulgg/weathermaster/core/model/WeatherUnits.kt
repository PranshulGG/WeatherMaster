package com.pranshulgg.weathermaster.core.model

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


fun TemperatureUnits.toName(): String {
    return when (this) {
        TemperatureUnits.CELSIUS -> "Celsius"
        TemperatureUnits.FAHRENHEIT -> "Fahrenheit"
    }
}

fun WindSpeedUnits.toName(): String {
    return when (this) {
        WindSpeedUnits.MPS -> "Meters per second"
        WindSpeedUnits.MPH -> "Miles per hour"
        WindSpeedUnits.KPH -> "Kilometers per hour"
    }
}

fun PressureUnits.toName(inShort: Boolean = false): String {
    return when (this) {
        PressureUnits.HPA -> if (inShort) "hPa" else "Hectopascals"
        PressureUnits.INHG -> if (inShort) "inHG" else "Inches of Mercury"
    }
}

fun DistanceUnits.toName(inShort: Boolean = false): String {
    return when (this) {
        DistanceUnits.KM -> if (inShort) "Km" else "Kilometers"
        DistanceUnits.MI -> if (inShort) "Mi" else "Miles"
        DistanceUnits.M -> if (inShort) "M" else "Meters"
    }
}

fun PrecipitationUnits.toName(): String {
    return when (this) {
        PrecipitationUnits.MM -> "Millimeters"
        PrecipitationUnits.INCH -> "Inches"
        PrecipitationUnits.CM -> "Centimeters"
    }
}