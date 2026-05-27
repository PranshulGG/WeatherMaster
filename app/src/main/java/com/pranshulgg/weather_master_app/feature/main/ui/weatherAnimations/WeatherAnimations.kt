package com.pranshulgg.weather_master_app.feature.main.ui.weatherAnimations

import androidx.compose.runtime.Composable
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather

@Composable
fun WeatherAnimations(weather: Weather, isFroggyLayout: Boolean) {
    val condition = weather.current.weatherCondition
    val day = weather.daily.firstOrNull()
    val isDay = day?.let { weather.current.time in day.sunrise..day.sunset } ?: true

    when (condition) {

        WeatherCondition.CLEAR_SKY -> if (isDay) SunCanvas(
            isFroggyLayout = isFroggyLayout
        ) else StarsCanvas()

        WeatherCondition.MOSTLY_CLEAR, WeatherCondition.PARTLY_CLOUDY -> if (isDay)
            SunCanvas(showClouds = true, isFroggyLayout) else StarsCanvas(showClouds = true)


        WeatherCondition.LIGHT_RAIN -> RainCanvas(
            rainDropCount = 30,
            isFroggyLayout = isFroggyLayout
        )

        WeatherCondition.RAIN -> RainCanvas(rainDropCount = 50, isFroggyLayout = isFroggyLayout)

        WeatherCondition.HEAVY_RAIN -> RainCanvas(
            rainDropCount = 80,
            isFroggyLayout = isFroggyLayout
        )


        WeatherCondition.OVERCAST -> OvercastCanvas()


        WeatherCondition.SNOW -> SnowCanvas(isFroggyLayout = isFroggyLayout)

        WeatherCondition.LIGHT_SNOW -> SnowCanvas(
            snowFlakeCount = 15,
            isFroggyLayout = isFroggyLayout
        )

        WeatherCondition.HEAVY_SNOW -> SnowCanvas(
            snowFlakeCount = 50,
            isFroggyLayout = isFroggyLayout
        )

        WeatherCondition.FOG_HAZE -> FogHazeCanvas(isFroggyLayout)

        WeatherCondition.THUNDERSTORM -> RainCanvas(rainDropCount = 50, isStorming = true)


        else -> StarsCanvas()

    }
}