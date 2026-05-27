package com.pranshulgg.weather_master_app.feature.main.ui.weatherAnimations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.core.ui.theme.isThemeDark

@Composable
fun FogHazeCanvas(isFroggyLayout: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val windowInfo = LocalWindowInfo.current
    val screenWidthPx = windowInfo.containerSize.width.toFloat()

    val isDark = isThemeDark()


    val itemWidth = 300f
    val speedPxPerSecond = 20f

    val distance = screenWidthPx + itemWidth * 2
    val durationMillis = ((distance / speedPxPerSecond) * 1000).toInt()

    val offsetX1 by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 6000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val offsetX2 by infiniteTransition.animateFloat(
        initialValue = 200f,
        targetValue = -150f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 8000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val offsetX3 by infiniteTransition.animateFloat(
        initialValue = screenWidthPx,
        targetValue = -screenWidthPx,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val modifier = Modifier
        .fillMaxWidth()
        .blur(30.dp)

    Canvas(
        modifier = if (!isFroggyLayout) modifier.fillMaxHeight() else modifier
            .height(290.dp)
    ) {
        val fog1 = Brush.radialGradient(
            colors = listOf(
                Color.Gray.copy(alpha = if (isDark) 0.3f else 0.7f),
                Color.Transparent
            ),
            center = Offset(
                x = size.width * 0.3f + offsetX1,
                y = size.height
            ),
            radius = size.width * 0.7f
        )

        val fog2 = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isDark) 0.2f else 0.7f),
                Color.Transparent
            ),
            center = Offset(
                x = size.width * 0.7f + offsetX2,
                y = size.height / 5f
            ),
            radius = size.width * 0.8f
        )


        val fog3 = Brush.radialGradient(
            colors = listOf(
                if (isDark) Color.Black.copy(alpha = 1f) else Color.White.copy(alpha = 1f),
                Color.Transparent
            ),
            center = Offset(
                x = size.width * 0.7f + offsetX3,
                y = size.height / 5f
            ),
            radius = 300f
        )

        drawRect(
            brush = fog1,
            size = size
        )

        drawRect(
            brush = fog2,
            size = size,
        )

        drawRect(
            brush = fog3,
            size = size,
        )
    }
}