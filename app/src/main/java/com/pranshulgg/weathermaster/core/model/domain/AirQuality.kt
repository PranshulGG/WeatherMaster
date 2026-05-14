package com.pranshulgg.weathermaster.core.model.domain

import com.pranshulgg.weathermaster.core.model.weather.airquality.AirQualityLevel
import kotlin.math.roundToInt

// TODO: ADD HOURLY-DAILY FORECAST

/**
 * Keeping air quality separate from the main weather domain
 * We don't wanna delay weather from displaying because the API hasn't returned anything or is slow
 * We only display air quality if it's available
 */
data class AirQuality(
    val current: AirQualityCurrent
) {
    companion object {

        /**
         * Thresholds extracted from Breezy Weather
         * [https://github.com/breezy-weather/breezy-weather/blob/06c4f1bbd6f27f8ae04ebba550cf8981e362e777/app/src/main/kotlin/org/breezyweather/domain/weather/index/PollutantIndex.kt]
         */
        private val pm25Thresholds = listOf(0, 5, 15, 30, 60, 150)
        private val pm10Thresholds = listOf(0, 15, 45, 80, 160, 400)
        private val o3Thresholds = listOf(0, 50, 100, 160, 240, 480)

        private val so2Thresholds = listOf(0, 20, 40, 270, 500, 960)
        private val no2Thresholds = listOf(0, 10, 25, 200, 400, 1000)

        private val coThresholds = listOf(0, 2, 4, 35, 100, 230)

        private val aqiThreshold = listOf(0, 20, 50, 100, 150, 250)

        private fun getAqi(value: Double, thresholds: List<Int>): Int {
            val index = thresholds.indexOfLast { value >= it }

            if (index == -1) return 0
            if (index >= thresholds.lastIndex) return aqiThreshold.last()


            /**
             * Get threshold range
             * For example: thresholds [0, 5, 15, 30, 60, 150]
             * If value is 21, the index should return 2 because 21 is more than 15 but less than 30
             * So we get the threshold range 15 - 30
             */
            val threshold1 = thresholds[index]
            val threshold2 = thresholds[index + 1]

            /**
             * Do same for AQI
             * For example: aqi thresholds [0, 20, 50, 100, 150, 250]
             * If threshold range 15 - 30, we should get 50 - 100
             */
            val aqi1 = aqiThreshold[index]
            val aqi2 = aqiThreshold[index + 1]

            // How far into the pollutant range is this value? (0.0 = start, 1.0 = end)
            val pos = (value - threshold1) / (threshold2 - threshold1)

            // Apply that same position to the AQI range to get the score
            return (aqi1 + pos * (aqi2 - aqi1)).roundToInt()

        }
    }


    fun getAqi(): Int {

        // Open-Meteo provides "carbon_monoxide" in MicrogramsPerCubicMeter so we convert to Milligrams
        val coInMilligramsPerCubicMeter = (current.carbonMonoxide!! / 1000)


        return maxOf(
            getAqi(current.pm25!!, pm25Thresholds),
            getAqi(current.nitrogenDioxide!!, no2Thresholds),
            getAqi(current.ozone!!, o3Thresholds),
            getAqi(current.pm10!!, pm10Thresholds),
            getAqi(coInMilligramsPerCubicMeter, coThresholds),
            getAqi(current.sulphurDioxide!!, so2Thresholds)
        )
    }

    // For AQI bars
    fun getAqiBarValue(aqi: Int): Float {
        return (aqi / 250f).coerceIn(0f, 1f)
    }

    private fun getAqiIndex(value: Int): Int {
        val index = aqiThreshold.indexOfLast { value >= it }
        return if (index == -1) 0 else index
    }

    fun getAqiLevel(aqi: Int): AirQualityLevel {

        val level = getAqiIndex(aqi)

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

data class AirQualityCurrent(
    val usAqi: Int?,
    val pm10: Double?,
    val pm25: Double?,
    val carbonMonoxide: Double?,
    val nitrogenDioxide: Double?,
    val sulphurDioxide: Double?,
    val ozone: Double?,
    val lastUpdatedInMilli: Long
)
