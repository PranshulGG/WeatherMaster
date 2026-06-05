package com.pranshulgg.weather_master_app.feature.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius

@Composable
fun LocationItem(
    title: String,
    description: String,
    icon: Int,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    isDefault: Boolean = false,
    onLongClick: () -> Unit,
    isDeviceLocation: Boolean = false,
    shape: RoundedCornerShape
) {

    val containerColor =
        if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceBright
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface

    val shape = if (isSelected) RoundedCornerShape(ShapeRadius.Large) else shape

    Surface(
        modifier = Modifier
            .clip(shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = shape,
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
            headlineContent = {
                if (isDefault) TitleForDefaultLocation(
                    contentColor,
                    title
                ) else Text(
                    title,
                    color = contentColor
                )
            },
            supportingContent = {
                Text(
                    description,
                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                        0.8f
                    ) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingContent = {
                if (isDeviceLocation) {
                    Symbol(R.drawable.circle_circle_24px, color = contentColor)
                }
            }
        )
    }
}

@Composable
private fun TitleForDefaultLocation(contentColor: Color, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = contentColor)
        Gap(horizontal = 3.dp)
        Symbol(R.drawable.home_pin_24px, color = contentColor, size = 18.dp)
    }
}

