package com.pranshulgg.weathermaster.feature.main.ui

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.pranshulgg.weathermaster.core.model.WeatherConditions
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.utils.WeatherUtils
import java.time.Instant

fun backgroundGradients(weather: Weather?): List<Color> {

    if (!WeatherUtils.isWeatherDomainSafe(weather)) return listOf(
        Color(0xFF635003),
        Color(0xFF543000)
    )

    val condition = weather?.current?.weatherCondition
    val day = weather?.daily?.firstOrNull()
    val isDay = day?.let { Instant.now().epochSecond in day.sunrise..day.sunset } ?: true

    Log.d("backgroundGradients", "isDay: $isDay")


    val gradient = gradients(isDay, condition ?: WeatherConditions.CLEAR_SKY)


    return gradient
}

private fun gradients(isDay: Boolean = true, condition: WeatherConditions): List<Color> {
    return when (condition) {

        WeatherConditions.CLEAR_SKY -> if (isDay) Colors.CLEAR_SKY_DAY else Colors.CLEAR_SKY_NIGHT

        WeatherConditions.MOSTLY_CLEAR, WeatherConditions.PARTLY_CLOUDY -> Colors.MOSTLY_CLEAR_PARTLY_CLEAR

        WeatherConditions.OVERCAST -> Colors.OVERCAST


        else -> Colors.CLEAR_SKY_NIGHT

    }

}


private object Colors {

    val CLEAR_SKY_DAY = listOf(Color(0xFF08579C), Color(0xFF04008e))


    val CLEAR_SKY_NIGHT = listOf(Color(0xFF01023D), Color(0xFF162155))


    val MOSTLY_CLEAR_PARTLY_CLEAR = listOf(Color(0xFF404558), Color(0xFF262C3D))

    val OVERCAST = listOf(Color(0xFF2F2F34), Color(0xFF202537))


}