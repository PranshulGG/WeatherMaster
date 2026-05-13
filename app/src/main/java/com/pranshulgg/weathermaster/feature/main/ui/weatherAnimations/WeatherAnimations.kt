package com.pranshulgg.weathermaster.feature.main.ui.weatherAnimations

import androidx.compose.runtime.Composable
import com.pranshulgg.weathermaster.core.model.weather.WeatherConditions
import com.pranshulgg.weathermaster.core.model.domain.Weather

@Composable
fun WeatherAnimations(weather: Weather) {
    val condition = weather.current.weatherCondition
    val day = weather.daily.firstOrNull()
    val isDay = day?.let { weather.current.time in day.sunrise..day.sunset } ?: true

    when (condition) {

        WeatherConditions.CLEAR_SKY -> if (isDay) SunCanvas() else StarsCanvas()


        WeatherConditions.MOSTLY_CLEAR, WeatherConditions.PARTLY_CLOUDY -> if (isDay)
            SunCanvas(showClouds = true) else StarsCanvas(showClouds = true)


        WeatherConditions.LIGHT_RAIN -> RainCanvas(rainDropCount = 30)

        WeatherConditions.RAIN -> RainCanvas(rainDropCount = 50)

        WeatherConditions.HEAVY_RAIN -> RainCanvas(rainDropCount = 80)


        WeatherConditions.OVERCAST -> OvercastCanvas()

        WeatherConditions.SNOW -> SnowCanvas()

        WeatherConditions.LIGHT_SNOW -> SnowCanvas(snowFlakeCount = 15)

        WeatherConditions.HEAVY_SNOW -> SnowCanvas(snowFlakeCount = 50)

        WeatherConditions.FOG_HAZE -> FogHazeCanvas()

        WeatherConditions.THUNDERSTORM -> RainCanvas(rainDropCount = 50, isStorming = true)


        else -> StarsCanvas()

    }
}