package com.pranshulgg.weathermaster.feature.shared.components.blocks

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.theme.ShadowElevation
import com.pranshulgg.weathermaster.core.utils.TimeFormatters
import java.time.Instant

@Composable
fun SunBlock(weather: Weather) {

    val daily = weather.daily


    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2
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


            val sunrise = daily[0].sunrise
            val sunset = daily[0].sunset
            val now = Instant.now().epochSecond

            val progress = ((now - sunrise).toFloat() / (sunset - sunrise))
                .coerceIn(0f, 1f)


            val sunBitmap = ImageBitmap.imageResource(id = R.drawable.sun_rise_set_icon)


            Canvas(modifier = Modifier.fillMaxSize()) {

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


            val sunriseFormatted = TimeFormatters().to12HourTimeString(
                (sunrise * 1000),
                weather.location.timezone,
                "hh:mm a"
            )
            val sunsetFormatted = TimeFormatters().to12HourTimeString(
                (sunset * 1000),
                weather.location.timezone,
                "hh:mm a"
            )


            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxHeight(0.4f)
                    .fillMaxWidth(),
                color = Color.Black.copy(alpha = 0.5f)
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
            "Sun",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)

        )
    }
}

