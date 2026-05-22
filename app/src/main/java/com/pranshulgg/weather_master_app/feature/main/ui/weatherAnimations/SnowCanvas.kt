package com.pranshulgg.weather_master_app.feature.main.ui.weatherAnimations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private data class Snowflake(
    var x: Float,
    var y: MutableState<Float>,
    var speed: MutableState<Float>,
    val size: Float
)

@Composable
fun SnowCanvas(snowFlakeCount: Int = 30) {


    var canvasSize by remember { mutableStateOf(Size.Zero) }


    val snowFlakes = remember {
        List(snowFlakeCount) {
            Snowflake(
                x = (0..1000).random().toFloat(),
                y = mutableFloatStateOf((0..2000).random().toFloat()),
                speed = mutableFloatStateOf((1..5).random().toFloat()),
                size = (5..20).random().toFloat()
            )

        }.toMutableStateList()
    }

    LaunchedEffect(Unit) {

        if (canvasSize == Size.Zero) return@LaunchedEffect

        while (true) {
            withFrameNanos {
                snowFlakes.forEach {

                    it.y.value += it.speed.value
                    it.x += -1f + -Random.nextFloat() * -4f

                    if (it.y.value > canvasSize.height) {
                        it.y.value = 0f
                        it.x = Random.nextFloat() * canvasSize.width
                        it.speed.value = (1..5).random().toFloat()
                    }
                }
            }
        }
    }



    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
    ) {

        canvasSize = size

        snowFlakes.forEach {
            drawCircle(
                Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(it.x, it.y.value),
                    radius = it.size

                ),
                radius = it.size,
                center = Offset(it.x, it.y.value)
            )

        }

    }


}