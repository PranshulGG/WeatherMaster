package com.pranshulgg.weathermaster.feature.main.ui.weatherAnimations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class Drop(
    var x: Float,
    var y: MutableState<Float>,
    var speed: MutableState<Float>
)

@Composable
fun RainCanvas(
    modifier: Modifier,
    isStorming: Boolean = false,
    rainDropCount: Int = 80
) {

    var canvasSize by remember { mutableStateOf(Size.Zero) }


    var lightningAlpha by remember { mutableIntStateOf(0) }

    val drops = remember {
        List(rainDropCount) {
            Drop(
                x = (0..1000).random().toFloat(),
                y = mutableFloatStateOf((0..2000).random().toFloat()),
                speed = mutableFloatStateOf((10..50).random().toFloat())
            )

        }.toMutableStateList()
    }
    LaunchedEffect(Unit) {

        if (canvasSize == Size.Zero) return@LaunchedEffect

        val gravity = 0.5f

        while (true) {
            withFrameNanos {
                drops.forEach {
                    it.speed.value += gravity
                    it.y.value += it.speed.value

                    it.x += -1f + -Random.nextFloat() * -2f

                    if (it.y.value > canvasSize.height) {
                        it.y.value = 0f
                        it.x = Random.nextFloat() * canvasSize.width
                        it.speed.value = (10..30).random().toFloat()
                    }
                }
            }
        }
    }


    if (isStorming) {
        LaunchedEffect(Unit) {
            while (true) {

                delay((1000..10000).random().toLong())
                lightningAlpha = 124
                delay((50..100).random().toLong())

                lightningAlpha = 0
                delay((200..300).random().toLong())

                lightningAlpha = 100
                delay((50..100).random().toLong())

                lightningAlpha = 0

            }
        }
    }


    Canvas(
        modifier
            .fillMaxWidth()
    ) {

        canvasSize = size

        drops.forEach {
            drawLine(
                start = Offset(it.x, it.y.value),
                end = Offset(it.x, it.y.value + 20f),
                strokeWidth = 5f,
                cap = StrokeCap.Round,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Gray.copy(alpha = 0.5f),
                        Color.White.copy(alpha = 0.6f)
                    )
                )
            )
        }
        if (isStorming) {
            drawRect(
                size = size,
                brush = Brush.verticalGradient(
                    listOf(
                        Color(212, 191, 255, alpha = lightningAlpha),
                        Color.Transparent
                    )
                )

            )
        }
    }

}