package com.pranshulgg.weathermaster.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WeatherIconBox(icon: Int, size: Dp = 40.dp) {

    Box(
        modifier = Modifier.size(size)
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = "",
            modifier = Modifier.matchParentSize()
        )
    }
}