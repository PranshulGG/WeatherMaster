package com.pranshulgg.weathermaster.feature.main.ui.weatherAnimations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.R
import kotlin.math.PI
import kotlin.math.sin

private data class Star(
    val x: Float,
    val y: Float,
    val alpha: Float,
    val radius: Float,
    val phase: Float
)

@Composable
fun StarsCanvas(starCount: Int = 200, showClouds: Boolean = false) {

    val stars = remember {
        List(starCount) {
            Star(
                x = (0..1000).random().toFloat(),
                y = (0..1000).random().toFloat(),
                alpha = (3..9).random() / 10f,
                radius = (5..25).random() / 10f,
                phase = (0..1000).random() / 1000f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition()

    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val cloud1 = ImageBitmap.imageResource(id = R.drawable.animation_cloudy_1)
    val cloud2 = ImageBitmap.imageResource(id = R.drawable.animation_cloudy_3)

    val cloudAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val cloud1X = cloudAnimation * 80f
    val cloud2X = -((cloudAnimation * 80f) + 50f)


    Canvas(Modifier.fillMaxWidth().height(290.dp)) {
        stars.forEach {
            val animatedAlpha =
                it.alpha * (0.5f + 0.5f * sin((twinkle + it.phase) * 2 * PI).toFloat())

            drawCircle(
                color = Color.White.copy(alpha = animatedAlpha.coerceIn(0f, 1f)),
                radius = it.radius,
                center = Offset(it.x / 1000f * size.width, it.y / 1000f * size.height)
            )
        }

        // ---- CLOUD 1
        if (showClouds) {
            translate(left = cloud1X, top = -60f) {
                scale(scale = 0.5f) {
                    drawImage(
                        image = cloud1,
                        srcOffset = IntOffset.Zero,
                        alpha = 0.8f,
                        srcSize = IntSize(cloud1.width, cloud1.height),
                    )
                }
            }

            // ---- CLOUD 2
            translate(left = cloud2X + -(cloud2.width / 6f), top = cloud2.height / 4f) {
                scale(scale = 0.4f) {
                    drawImage(
                        image = cloud2,
                        srcOffset = IntOffset.Zero,
                        srcSize = IntSize(cloud2.width, cloud2.height),
                    )
                }
            }
        }

    }

}