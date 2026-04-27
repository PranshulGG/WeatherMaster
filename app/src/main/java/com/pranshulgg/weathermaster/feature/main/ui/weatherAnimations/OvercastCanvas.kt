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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.R

@Preview
@Composable
fun OvercastCanvas() {

    val cloud1 = ImageBitmap.imageResource(id = R.drawable.animation_cloudy_1)
    val cloud2 = ImageBitmap.imageResource(id = R.drawable.animation_cloudy_2)
    val cloud3 = ImageBitmap.imageResource(id = R.drawable.animation_cloudy_9)
    val cloud4 = ImageBitmap.imageResource(id = R.drawable.animation_cloudy_6)
    val cloud5 = ImageBitmap.imageResource(id = R.drawable.animation_cloudy_8)

    val infiniteTransition = rememberInfiniteTransition()

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
    val cloud3X = -((cloudAnimation * 40f) + 30f)
    val cloud4X = cloudAnimation * 40f + 100f
    val cloud5X = cloudAnimation * 100f + 100f

    Canvas(
        Modifier
            .fillMaxWidth()
            .height(290.dp)
    ) {

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

        translate(left = cloud4X  + (cloud4.width / 6f), top = 90f) {
            scale(scale = 0.5f) {
                drawImage(
                    image = cloud4,
                    srcOffset = IntOffset.Zero,
                    alpha = 0.5f,
                    srcSize = IntSize(cloud4.width, cloud4.height),
                )
            }
        }


        translate(left = cloud2X + -(cloud2.width / 6f), top = cloud2.height / 1.8f) {
            scale(scale = 0.4f) {
                drawImage(
                    image = cloud2,
                    srcOffset = IntOffset.Zero,
                    srcSize = IntSize(cloud2.width, cloud2.height),
                )
            }
        }
        translate(left = cloud3X + -(cloud3.width / 3f), top  = -60f) {
            scale(scale = 0.4f) {
                drawImage(
                    image = cloud3,
                    srcOffset = IntOffset.Zero,
                    alpha = 0.6f,
                    srcSize = IntSize(cloud3.width, cloud3.height),
                )
            }
        }

        translate(left = cloud5X  + (cloud5.width / 6f), top = cloud5.height / 1.3f) {
            scale(scale = 0.5f) {
                drawImage(
                    image = cloud5,
                    srcOffset = IntOffset.Zero,
                    alpha = 0.5f,
                    srcSize = IntSize(cloud5.width, cloud5.height),
                )
            }
        }
    }
}