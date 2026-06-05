package com.pranshulgg.weather_master_app.core.model.weather.wind

enum class WindDirection {
    N,
    NE,
    SE,
    S,
    SW,
    W,
    NW,
    E;

    companion object {


        // For sources that return values in degrees
        fun toWindDirectionFromDegrees(value: Int?): WindDirection? {
            return when (value) {
                in 0..22, in 337..360 -> N
                in 22..67 -> NE
                in 67..112 -> E
                in 122..157 -> SE
                in 157..202 -> S
                in 202..247 -> SW
                in 247..292 -> W
                in 292..337 -> NW
                else -> null
            }
        }

        // For rotating the arrow
        fun toDegrees(windDirection: WindDirection?): Int? {
            return when (windDirection) {
                N -> 0
                NE -> 45
                E -> 90
                SE -> 135
                S -> 180
                SW -> 225
                W -> 270
                NW -> 315
                else -> null
            }
        }

        // For sources that return values in cardinal directions (N, NE, etc.)
        fun toWindDirectionFromString(value: String?): WindDirection? {
            return when (value?.uppercase()) {
                "N", "NNE", "NNW" -> N
                "NE", "ENE" -> NE
                "E", "ESE" -> E
                "SE", "SSE" -> SE
                "S", "SSW" -> S
                "SW", "WSW" -> SW
                "W", "WNW" -> W
                "NW" -> NW
                else -> null
            }
        }
    }
}




