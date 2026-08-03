package com.pranshulgg.weather_master_app.widgets.ui.colors

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.unit.ColorProvider
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

@Composable
fun getWidgetWeatherBackground(weatherCondition: WeatherCondition): ColorProvider {
    return when (weatherCondition) {
        WeatherCondition.CLEAR_SKY -> ColorProvider(Color(0xFF04008e))
        WeatherCondition.MOSTLY_CLEAR, WeatherCondition.PARTLY_CLOUDY -> ColorProvider(
            Color(
                0xFF404558
            )
        )

        WeatherCondition.OVERCAST -> ColorProvider(Color(0xFF2F2F34))
        WeatherCondition.SNOW, WeatherCondition.HEAVY_SNOW, WeatherCondition.LIGHT_SNOW -> ColorProvider(
            Color(
                0xFF171717
            )
        )

        WeatherCondition.RAIN, WeatherCondition.HEAVY_RAIN, WeatherCondition.LIGHT_RAIN -> ColorProvider(
            Color(0xFF1e2c3a)
        )

        WeatherCondition.FOG_HAZE -> ColorProvider(Color(0xFF352603))
        WeatherCondition.THUNDERSTORM -> ColorProvider(Color(0xFF4C2858))
        else -> ColorProvider(Color(0xFF04008e))
    }


}