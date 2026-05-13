package com.pranshulgg.weathermaster.feature.main.ui

import androidx.compose.animation.AnimatedContent
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
import com.pranshulgg.weathermaster.core.model.weather.WeatherConditions
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.ui.theme.isThemeDark
import com.pranshulgg.weathermaster.core.utils.weather.cache.isWeatherDomainSafe

data class Background(
    val gradient: List<Color>,
    val scrollColor: Color
)

@Composable
fun BackgroundGradient(weather: Weather?, isScrolled: Boolean = false) {

    val isDark = isThemeDark()

    AnimatedContent(
        targetState = isScrolled,
        transitionSpec = { fadeIn() togetherWith fadeOut() }) { isScrolled ->
        if (!isScrolled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            backgroundGradients(weather, isDark).gradient,
                            startY = 0f,
                            endY = 1000f
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = backgroundGradients(weather, isDark).scrollColor)
            )
        }
    }

}

private fun backgroundGradients(weather: Weather?, isDark: Boolean = true): Background {

    if (!isWeatherDomainSafe(weather)) return Colors.CLEAR_SKY_DAY

    val condition = weather?.current?.weatherCondition
    val day = weather?.daily?.firstOrNull()
    val isDay = day?.let { weather.current.time in day.sunrise..day.sunset } ?: true


    val gradient = gradients(isDay, condition ?: WeatherConditions.CLEAR_SKY, isDark)


    return gradient
}

private fun gradients(
    isDay: Boolean = true,
    condition: WeatherConditions,
    isDark: Boolean = true
): Background {

    val colors = if (isDark) Colors else ColorsLight

    return when (condition) {

        WeatherConditions.CLEAR_SKY -> if (isDay) colors.CLEAR_SKY_DAY else colors.CLEAR_SKY_NIGHT

        WeatherConditions.MOSTLY_CLEAR, WeatherConditions.PARTLY_CLOUDY -> colors.MOSTLY_CLEAR_PARTLY_CLOUDY

        WeatherConditions.OVERCAST -> colors.OVERCAST

        WeatherConditions.SNOW, WeatherConditions.HEAVY_SNOW, WeatherConditions.LIGHT_SNOW -> colors.SNOW

        WeatherConditions.RAIN, WeatherConditions.HEAVY_RAIN, WeatherConditions.LIGHT_RAIN -> colors.RAIN

        WeatherConditions.FOG_HAZE -> colors.FOG_HAZE

        WeatherConditions.THUNDERSTORM -> colors.THUNDERSTORM

        else -> colors.CLEAR_SKY_DAY

    }

}

interface ColorPalette {
    val CLEAR_SKY_DAY: Background
    val CLEAR_SKY_NIGHT: Background
    val MOSTLY_CLEAR_PARTLY_CLOUDY: Background
    val OVERCAST: Background
    val SNOW: Background
    val RAIN: Background
    val FOG_HAZE: Background
    val THUNDERSTORM: Background
}

object Colors : ColorPalette {

    override val CLEAR_SKY_DAY = Background(
        gradient = listOf(Color(0xFF08579C), Color(0xFF04008e)),
        scrollColor = Color(0xFF04008e)
    )

    override val CLEAR_SKY_NIGHT = Background(
        gradient = listOf(Color(0xFF01023D), Color(0xFF162155)),
        scrollColor = Color(0xFF162155)
    )

    override val MOSTLY_CLEAR_PARTLY_CLOUDY = Background(
        gradient = listOf(Color(0xFF404558), Color(0xFF262C3D)),
        scrollColor = Color(0xFF262C3D)
    )
    override val OVERCAST = Background(
        gradient = listOf(Color(0xFF2F2F34), Color(0xFF202537)),
        scrollColor = Color(0xFF202537)
    )

    override val SNOW = Background(
        gradient = listOf(Color(0xFF171717), Color(0xFF202537)),
        scrollColor = Color(0xFF202537)
    )

    override val RAIN = Background(
        gradient = listOf(Color(0xFF242429), Color(0xFF1e2c3a)),
        scrollColor = Color(0xFF1e2c3a)
    )

    override val FOG_HAZE = Background(
        gradient = listOf(Color(0xFF191209), Color(0xFF352603)),
        scrollColor = Color(0xFF352603)
    )

    override val THUNDERSTORM = Background(
        gradient = listOf(Color(0xFF15021a), Color(0xFF4C2858)),
        scrollColor = Color(0xFF4C2858)
    )

}

private object ColorsLight : ColorPalette {

    override val CLEAR_SKY_DAY = Background(
        gradient = listOf(Color(0xFF9dceff), Color(0xFFcee5ff)),
        scrollColor = Color(0xFFcee5ff)
    )

    override val CLEAR_SKY_NIGHT = Background(
        gradient = listOf(Color(0xFF969eeb), Color(0xFFE4DFFF)),
        scrollColor = Color(0xFFE4DFFF)
    )

    override val MOSTLY_CLEAR_PARTLY_CLOUDY = Background(
        gradient = listOf(Color(0xFFc6d3e4), Color(0xFFd5e4f7)),
        scrollColor = Color(0xFFd5e4f7)
    )
    override val OVERCAST = Background(
        gradient = listOf(Color(0xFFacacad), Color(0xFFd5e4f7)),
        scrollColor = Color(0xFFd5e4f7)
    )

    override val SNOW = Background(
        gradient = listOf(Color(0xFFacacad), Color(0xFFFFFFFF)),
        scrollColor = Color(0xFFFFFFFF)
    )

    override val RAIN = Background(
        gradient = listOf(Color(0xFFaab8ca), Color(0xFFc4d3e5)),
        scrollColor = Color(0xFFc4d3e5)
    )

    override val FOG_HAZE = Background(
        gradient = listOf(Color(0xFFfacb8e), Color(0xFFfff4db)),
        scrollColor = Color(0xFFfff4db)
    )

    override val THUNDERSTORM = Background(
        gradient = listOf(Color(0xFFaab8ca), Color(0xFFf2c9ff)),
        scrollColor = Color(0xFFf2c9ff)
    )

}
