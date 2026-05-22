package com.pranshulgg.weather_master_app.core.model.weather.airquality


/**
 * Thresholds extracted from Breezy Weather
 * [https://github.com/breezy-weather/breezy-weather/blob/06c4f1bbd6f27f8ae04ebba550cf8981e362e777/app/src/main/kotlin/org/breezyweather/domain/weather/index/PollutantIndex.kt]
 */
enum class Pollutant(
    val thresholds: List<Int>
) {
    PM25(listOf(0, 5, 15, 30, 60, 150)),
    PM10(listOf(0, 15, 45, 80, 160, 400)),
    O3(listOf(0, 50, 100, 160, 240, 480)),
    NO2(listOf(0, 10, 25, 200, 400, 1000)),
    SO2(listOf(0, 20, 40, 270, 500, 960)),
    CO(listOf(0, 2, 4, 35, 100, 230));

    companion object {


        private fun getLevelIndex(value: Double, thresholds: List<Int>): Int {
            val index = thresholds.indexOfLast { value >= it }
            return if (index == -1) 0 else index
        }


        fun getPollutantLevel(value: Double, pollutant: Pollutant): AirQualityLevel {

            // Open-Meteo provides "carbon_monoxide" in MicrogramsPerCubicMeter so we convert to Milligrams
            val value = if (pollutant == CO) {
                (value / 1000)
            } else value

            val level = getLevelIndex(value, pollutant.thresholds)

            return when (level) {
                0 -> AirQualityLevel.GOOD
                1 -> AirQualityLevel.FAIR
                2 -> AirQualityLevel.MODERATE
                3 -> AirQualityLevel.POOR
                4 -> AirQualityLevel.VERY_POOR
                else -> AirQualityLevel.HAZARDOUS
            }
        }
    }
}


