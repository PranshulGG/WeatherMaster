package com.pranshulgg.weather_master_app.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusBanner(
    icon: Int,
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Symbol(icon, color = contentColor)
            Gap(16.dp)
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            color = containerColor,
            tonalElevation = 4.dp,
            content = content
        )
    } else {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            color = containerColor,
            tonalElevation = 4.dp,
            content = content
        )
    }
}
