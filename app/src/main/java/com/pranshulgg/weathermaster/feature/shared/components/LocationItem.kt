package com.pranshulgg.weathermaster.feature.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.components.WeatherIconBox
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius

@Composable
fun LocationItem(
    title: String,
    description: String,
    icon: Int,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    isDefault: Boolean = false,
    onLongClick: () -> Unit,
    isDeviceLocation: Boolean = false
) {

    val containerColor =
        if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceBright
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    val shape = if (isSelected) RoundedCornerShape(ShapeRadius.ExtraLarge) else CircleShape


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
                if (isDefault) DefaultLocationTitle(
                    contentColor,
                    title,
                    isDeviceLocation
                ) else Text(
                    title,
                    color = contentColor
                )
            },
            supportingContent = {
                Text(
                    description,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                        0.8f
                    ) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}

@Composable
private fun DefaultLocationTitle(contentColor: Color, title: String, isDeviceLocation: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = contentColor)
        Gap(horizontal = 3.dp)
        Symbol(R.drawable.home_pin_24px, color = contentColor, size = 18.dp)
    }
}