package com.pranshulgg.weather_master_app.feature.blocks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun ChartBarItem(
    barBackgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    height: Int,
) {
    Box(
        modifier = Modifier
            .background(
                shape = CircleShape,
                color = barBackgroundColor
            )
            .height(height.dp)
            .fillMaxWidth()
    ) {
    }

}