package com.pranshulgg.weathermaster.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Symbol(
    icon: Int,
    modifier: Modifier = Modifier,
    desc: String? = null,
    color: Color = MaterialTheme.colorScheme.onSurface,
    size: Dp = 24.dp,
    paddingStart: Dp = 0.dp,
) {
    Box(modifier = Modifier.padding(start = paddingStart)) {
        Icon(
            painter = painterResource(icon),
            contentDescription = desc,
            tint = color,
            modifier = modifier.size(size)
        )
    }
}