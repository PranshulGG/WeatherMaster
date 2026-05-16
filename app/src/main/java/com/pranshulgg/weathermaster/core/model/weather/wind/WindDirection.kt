package com.pranshulgg.weathermaster.core.model.weather.wind

enum class WindDirection {
    N,
    NE,
    SE,
    S,
    SW,
    W,
    NW,
    E
}


fun getWindDirectionValue(direction: Int): WindDirection? {
    return when (direction) {
        in 0..22, in 337..360 -> WindDirection.N
        in 22..67 -> WindDirection.NE
        in 67..112 -> WindDirection.E
        in 122..157 -> WindDirection.SE
        in 157..202 -> WindDirection.S
        in 202..247 -> WindDirection.SW
        in 247..292 -> WindDirection.W
        in 292..337 -> WindDirection.NW
        else -> null
    }
}

