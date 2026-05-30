package com.pranshulgg.weather_master_app.feature.shared.components.blocks

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.prefs.AppPrefsState
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherDailyDomainSafe

@Composable
fun SunBlock(weather: Weather, dailyIndex: Int, prefs: AppPrefsState, onClickBlock: () -> Unit) {

    if (!isWeatherDailyDomainSafe(weather)) return

    val daily = weather.daily[dailyIndex]
    val is24hr = prefs.is24HrTimeFormat


    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2,
        onClick = onClickBlock,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        ) {

            Image(
                painter = painterResource(id = R.drawable.sun_moon_arc_block),
                contentDescription = "",
                modifier = Modifier
                    .matchParentSize()
                    .align(Alignment.BottomCenter),
                alignment = Alignment.BottomCenter,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiaryContainer)
            )


            val sunrise = daily.sunrise
            val sunset = daily.sunset
            val now = System.currentTimeMillis()

            val progress = ((now - sunrise).toFloat() / (sunset - sunrise))
                .coerceIn(0f, 1f)


            val sunBitmap = ImageBitmap.imageResource(id = R.drawable.sun_rise_set_icon)


            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (dailyIndex != 0) 0f else 1f)
            ) {

                val width = size.width
                val height = size.height

                val path = Path().apply {
                    moveTo(0f, height / 1.45f)
                    cubicTo(
                        width * 0.38f, height / 2f,
                        width * 0.38f, height / 15f,
                        width * 0.98f, height / 1.4f
                    )

                }

                drawPath(
                    path = path,
                    color = Color.Transparent
                )

                val pathMeasure = PathMeasure().apply { setPath(path, false) }
                pathMeasure.setPath(path, false)
                val pos = pathMeasure.getPosition(pathMeasure.length * progress)

                val size = 58

                drawImage(
                    image = sunBitmap,
                    srcOffset = IntOffset.Zero,
                    srcSize = IntSize(sunBitmap.width, sunBitmap.height),
                    dstOffset = IntOffset(
                        (pos.x - size / 2f).toInt(),
                        (pos.y - size / 2f).toInt()
                    ),
                    dstSize = IntSize(size, size)
                )


            }

            Box(Modifier.align(Alignment.TopStart)) {
                Header()
            }


            val sunriseFormatted = if (is24hr) to24HourTimeString(
                sunrise,
                weather.location.timezone
            ) else to12HourTimeString(
                sunrise,
                weather.location.timezone,
                "hh:mm a"
            )
            val sunsetFormatted = if (is24hr) to24HourTimeString(
                sunset,
                weather.location.timezone
            ) else to12HourTimeString(
                sunset,
                weather.location.timezone,
                "hh:mm a"
            )


            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxHeight(0.4f)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainer.copy(0.5f)
            ) {
                Box(Modifier.fillMaxSize()) {
                    HorizontalDivider(Modifier.align(Alignment.TopCenter))

                    Column(Modifier.align(Alignment.Center)) {
                        RiseSetTimeRow(sunriseFormatted, R.drawable.arrow_upward_24px) // RISE
                        RiseSetTimeRow(sunsetFormatted, R.drawable.arrow_downward_24px) // SET
                    }
                }
            }
        }
    }
}

@Composable
private fun RiseSetTimeRow(text: String, icon: Int) {
    Row() {
        Symbol(icon, color = MaterialTheme.colorScheme.onSurface, size = 18.dp)
        Gap(horizontal = 5.dp)
        Text(
            text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun Header() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            5.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 16.dp, start = 12.dp, end = 12.dp)
    ) {
        Symbol(
            R.drawable.sunny_24px,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
        )
        Text(
            stringResource(R.string.weather_sun),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)

        )
    }
}

