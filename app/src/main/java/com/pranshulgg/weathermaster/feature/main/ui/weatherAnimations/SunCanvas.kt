package com.pranshulgg.weathermaster.feature.main.ui.weatherAnimations

import androidx.compose.animation.core.Animatable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.R
import kotlinx.coroutines.delay


@Composable
fun SunCanvas(showClouds: Boolean = false) {


    val infiniteTransition = rememberInfiniteTransition()
    val alphaSun by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val alphaRays = remember { List(3) { Animatable(0f) } }


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


    alphaRays.forEachIndexed { index, animatable ->
        LaunchedEffect(index) {
            delay((0..2000).random().toLong())

            while (true) {
                animatable.animateTo(
                    targetValue = 0.2f,
                    animationSpec = tween(
                        durationMillis = 1500,
                        easing = LinearEasing
                    )
                )
                delay((2000..3000).random().toLong())
                animatable.animateTo(
                    targetValue = 0.1f,
                    animationSpec = tween(
                        durationMillis = 1500,
                        easing = LinearEasing
                    )
                )
                delay((800..1200).random().toLong())

                animatable.animateTo(
                    targetValue = 0.2f,
                    animationSpec = tween(
                        durationMillis = 1500,
                        easing = LinearEasing
                    )
                )
                delay((1000..3000).random().toLong())

                animatable.animateTo(
                    0f, animationSpec = tween(
                        durationMillis = 2000,
                        easing = LinearEasing
                    )
                )
                delay((3000..4000).random().toLong())
            }
        }
    }


    Canvas(
        Modifier
            .fillMaxWidth()
            .height(290.dp)
    ) {

        val circleCenter = Offset(
            x = size.width / 1.2f,
            y = size.height / 3.1f
        )

        val brushSun = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFDB813).copy(alpha = alphaSun),
                Color.Transparent
            ),
            center = circleCenter,
            radius = 200f
        )

        // START POINT
        drawCircle(
            brush = brushSun,
            center = circleCenter,
            radius = 200f
        )

        // ----
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Yellow.copy(alpha = alphaRays[2].value),
                    Color.Transparent
                ),
                center = Offset(
                    x = size.width / 1.2f,
                    y = size.height / 1.7f
                ),
                radius = 70f
            ),
            radius = 70f,
            center = Offset(
                x = size.width / 1.3f,
                y = size.height / 1.8f
            )
        )

        // ----
        if(!showClouds) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = alphaRays[0].value),
                        Color.Transparent
                    ),
                    center = Offset(
                        x = size.width / 2.24f,
                        y = size.height / 1.35f
                    ),
                    radius = 70f
                ),
                radius = 70f,
                center = Offset(
                    x = size.width / 2.5f,
                    y = size.height / 1.5f
                ),
            )

            // ----
            rotate(20f) {
                drawRoundRect(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = alphaRays[1].value),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = size.width / 1.64f,
                            y = size.height / 2f
                        ),
                        radius = 50f
                    ), size = Size(50f, 50f), topLeft = Offset(
                        x = size.width / 1.8f,
                        y = size.height / 3f
                    ),
                    cornerRadius = CornerRadius(20f)
                )
            }
        }
        // ---- RAY 1
        rotate(40f, pivot = circleCenter) {
            drawRect(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = alphaRays[0].value), Color.Transparent
                    ),
                    startY = 300f,
                    endY = 600f
                ),
                size = Size(20f, 500f), topLeft = circleCenter / 1.04f
            )
        }

        // ---- RAY 2
        rotate(10f, pivot = circleCenter) {
            drawRect(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = alphaRays[1].value), Color.Transparent
                    ),
                    startY = 300f,
                    endY = 600f
                ),
                size = Size(20f, 500f), topLeft = circleCenter
            )
        }

        // ---- RAY 3
        rotate(70f, pivot = circleCenter) {
            drawRect(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = alphaRays[2].value), Color.Transparent
                    ),
                    startY = 300f,
                    endY = 600f
                ),
                size = Size(30f, 500f), topLeft = circleCenter / 1.07f
            )
        }


        // ---- CLOUD 1
        if(showClouds) {
            translate(left = cloud1X) {
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
            translate(left = cloud2X + -(cloud2.width / 6f), top = cloud2.height / 2.5f) {
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