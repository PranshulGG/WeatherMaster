package com.pranshulgg.weathermaster.feature.daily.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.core.model.domain.WeatherDaily
import com.pranshulgg.weathermaster.core.model.weather.WeatherConditions
import com.pranshulgg.weathermaster.core.model.weather.toIcon
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.WeatherIconBox
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius

@Composable
fun SelectableDayItem(
    weekday: String,
    minTemp: String,
    maxTemp: String,
    conditions: WeatherConditions,
    onSelect: () -> Unit,
    isSelected: Boolean,
    motionScheme: MotionScheme
) {


    val animatedShape by animateDpAsState(
        targetValue = if (isSelected) 24.dp else 50.dp,
        animationSpec = motionScheme.defaultSpatialSpec(),
        label = "shape"
    )

    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
        animationSpec = motionScheme.defaultEffectsSpec(),
        label = "color"
    )

    val selectedOnSurface =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val selectedOnSurfaceVariant =
        if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(0.8f) else MaterialTheme.colorScheme.onSurfaceVariant


    Surface(
        color = animatedColor,
        shape = RoundedCornerShape(animatedShape),
        onClick = onSelect
    ) {
        Column(
            Modifier
                .height(160.dp)
                .width(65.dp)
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${maxTemp}°",
                    style = MaterialTheme.typography.bodyLarge,
                    color = selectedOnSurface
                )
                Text(
                    "${minTemp}°",
                    style = MaterialTheme.typography.bodyLarge,
                    color = selectedOnSurfaceVariant
                )
            }
            Gap(5.dp)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherIconBox(
                    conditions.toIcon(targetTimeMilli = System.currentTimeMillis()),
                    size = 34.dp
                )
                Gap(8.dp)
                Text(
                    weekday,
                    color = selectedOnSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

    }
}