package com.pranshulgg.weather_master_app.feature.blocks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun ChartBarItem(
    valueComposable: @Composable () -> Unit,
    barBackgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    shapeColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    height: Int,
) {
    Box() {
        Box(
            modifier = Modifier
                .background(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                )
                .align(Alignment.BottomCenter)
                .width(28.dp)
                .height(170.dp)
        ) {}
        Box(
            modifier = Modifier
                .background(
                    shape = CircleShape,
                    color = barBackgroundColor
                )
                .align(Alignment.BottomCenter)
                .width(48.dp)
                .height(height.dp)
        ) {
            Surface(
                shape = MaterialShapes.Cookie12Sided.toShape(),
                color = shapeColor,
                modifier = Modifier
                    .size(44.dp)
                    .offset(y = 2.dp)
                    .align(Alignment.TopCenter)
            ) {

                Box(
                    Modifier.matchParentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    valueComposable()
                }
            }

        }
    }

}