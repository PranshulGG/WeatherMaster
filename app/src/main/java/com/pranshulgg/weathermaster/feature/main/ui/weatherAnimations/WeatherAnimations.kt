package com.pranshulgg.weathermaster.feature.main.ui.weatherAnimations

import androidx.compose.runtime.Composable
import com.pranshulgg.weathermaster.core.model.weather.WeatherCondition
import com.pranshulgg.weathermaster.core.model.domain.Weather

@Composable
fun WeatherAnimations(weather: Weather) {
    val condition = weather.current.weatherCondition
    val day = weather.daily.firstOrNull()
    val isDay = day?.let { weather.current.time in day.sunrise..day.sunset } ?: true

    when (condition) {

        WeatherCondition.CLEAR_SKY -> if (isDay) SunCanvas() else StarsCanvas()


        WeatherCondition.MOSTLY_CLEAR, WeatherCondition.PARTLY_CLOUDY -> if (isDay)
            SunCanvas(showClouds = true) else StarsCanvas(showClouds = true)


        WeatherCondition.LIGHT_RAIN -> RainCanvas(rainDropCount = 30)

        WeatherCondition.RAIN -> RainCanvas(rainDropCount = 50)

        WeatherCondition.HEAVY_RAIN -> RainCanvas(rainDropCount = 80)


        WeatherCondition.OVERCAST -> OvercastCanvas()

        WeatherCondition.SNOW -> SnowCanvas()

        WeatherCondition.LIGHT_SNOW -> SnowCanvas(snowFlakeCount = 15)

        WeatherCondition.HEAVY_SNOW -> SnowCanvas(snowFlakeCount = 50)

        WeatherCondition.FOG_HAZE -> FogHazeCanvas()

        WeatherCondition.THUNDERSTORM -> RainCanvas(rainDropCount = 50, isStorming = true)


        else -> StarsCanvas()

    }
}