package com.pranshulgg.weathermaster.feature.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.core.ui.components.WeatherIconBox
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius

@Composable
fun LocationItem(
    title: String,
    description: String,
    icon: Int,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {

    val containerColor =
        if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceBright
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick = onClick,
        shape = if (isSelected) RoundedCornerShape(ShapeRadius.ExtraLarge) else CircleShape,
        color = containerColor,
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = containerColor),
            leadingContent = {
                Box(
                    Modifier
                        .size(52.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer,
                            shape = CircleShape
                        ), contentAlignment = Alignment.Center
                ) {
                    WeatherIconBox(icon, size = 34.dp)
                }
            },
            headlineContent = { Text(title, color = contentColor) },
            supportingContent = {
                Text(
                    description,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }

}