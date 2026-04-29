package com.pranshulgg.weathermaster.feature.main.ui

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.pranshulgg.weathermaster.core.model.WeatherConditions
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.utils.DataSafe
import com.pranshulgg.weathermaster.core.utils.WeatherUtils
import com.pranshulgg.weathermaster.feature.main.ui.Background
import java.time.Instant

private data class Background(
    val gradient: List<Color>,
    val scrollColor: Color
)

@Composable
fun BackgroundGradient(weather: Weather?, isScrolled: Boolean = false) {
    AnimatedContent(
        targetState = isScrolled,
        transitionSpec = { fadeIn() togetherWith fadeOut() }) { isScrolled ->
        if (!isScrolled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            backgroundGradients(weather).gradient,
                            startY = 0f,
                            endY = 1000f
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = backgroundGradients(weather).scrollColor
                    )
            )
        }
    }

}

private fun backgroundGradients(weather: Weather?): Background {

    if (!DataSafe().isWeatherDomainSafe(weather)) return Colors.CLEAR_SKY_DAY

    val condition = weather?.current?.weatherCondition
    val day = weather?.daily?.firstOrNull()
    val isDay = day?.let { weather.current.time in day.sunrise..day.sunset } ?: true


    val gradient = gradients(isDay, condition ?: WeatherConditions.CLEAR_SKY)


    return gradient
}

private fun gradients(
    isDay: Boolean = true,
    condition: WeatherConditions
): Background {
    return when (condition) {

        WeatherConditions.CLEAR_SKY -> if (isDay) Colors.CLEAR_SKY_DAY else Colors.CLEAR_SKY_NIGHT

        WeatherConditions.MOSTLY_CLEAR, WeatherConditions.PARTLY_CLOUDY -> Colors.MOSTLY_CLEAR_PARTLY_CLOUDY

        WeatherConditions.OVERCAST -> Colors.OVERCAST

        WeatherConditions.SNOW, WeatherConditions.HEAVY_SNOW, WeatherConditions.LIGHT_SNOW -> Colors.SNOW

        WeatherConditions.RAIN, WeatherConditions.HEAVY_RAIN, WeatherConditions.LIGHT_RAIN -> Colors.RAIN

        WeatherConditions.FOG_HAZE -> Colors.FOG_HAZE

        WeatherConditions.THUNDERSTORM -> Colors.THUNDERSTORM

        else -> Colors.CLEAR_SKY_NIGHT

    }

}


private object Colors {

    val CLEAR_SKY_DAY = Background(
        gradient = listOf(Color(0xFF08579C), Color(0xFF04008e)),
        scrollColor = Color(0xFF04008e)
    )

    val CLEAR_SKY_NIGHT = Background(
        gradient = listOf(Color(0xFF01023D), Color(0xFF162155)),
        scrollColor = Color(0xFF162155)
    )

    val MOSTLY_CLEAR_PARTLY_CLOUDY = Background(
        gradient = listOf(Color(0xFF404558), Color(0xFF262C3D)),
        scrollColor = Color(0xFF262C3D)
    )
    val OVERCAST = Background(
        gradient = listOf(Color(0xFF2F2F34), Color(0xFF202537)),
        scrollColor = Color(0xFF202537)
    )

    val SNOW = Background(
        gradient = listOf(Color(0xFF171717), Color(0xFF202537)),
        scrollColor = Color(0xFF202537)
    )

    val RAIN = Background(
        gradient = listOf(Color(0xFF242429), Color(0xFF1e2c3a)),
        scrollColor = Color(0xFF1e2c3a)
    )

    val FOG_HAZE = Background(
        gradient = listOf(Color(0xFF191209), Color(0xFF352603)),
        scrollColor = Color(0xFF352603)
    )

    val THUNDERSTORM = Background(
        gradient = listOf(Color(0xFF15021a), Color(0xFF4C2858)),
        scrollColor = Color(0xFF4C2858)
    )

}

