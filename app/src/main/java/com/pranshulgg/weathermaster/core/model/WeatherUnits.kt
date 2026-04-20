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

fun PressureUnits.toName(): String {
    return when (this) {
        PressureUnits.HPA -> "Hectopascals"
        PressureUnits.INHG -> "Inches of Mercury"
    }
}

fun DistanceUnits.toName(): String {
    return when (this) {
        DistanceUnits.KM -> "Kilometers"
        DistanceUnits.MI -> "Miles"
        DistanceUnits.M -> "Meters"
    }
}

fun PrecipitationUnits.toName(): String {
    return when (this) {
        PrecipitationUnits.MM -> "Millimeters"
        PrecipitationUnits.INCH -> "Inches"
        PrecipitationUnits.CM -> "Centimeters"
    }
}