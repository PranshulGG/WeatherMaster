package com.pranshulgg.weather_master_app.feature.locations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.toIcon
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.core.utils.formatters.getLastUpdatedTimeString
import com.pranshulgg.weather_master_app.feature.shared.components.LocationItem

@Composable
fun LocationsScreenContent(
    locations: List<Location>,
    onLongClick: (Location) -> Unit,
    onLocationSelect: (Location) -> Unit,
    activeLocation: Location? = null,
    weatherForLocations: List<Weather> = emptyList(),
    onAddCurrentLocation: () -> Unit,
    isDeviceLocationLoading: Boolean
) {

    val weatherMap = weatherForLocations.associateBy { it.location.id }
    val context = LocalContext.current


    AnimatedContent(
        targetState = locations,
        transitionSpec = { fadeIn() togetherWith fadeOut() }) { locations ->

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val showDeviceLocationCard = locations.none { it.isDeviceLocation }

            // TODO: Still need to find a better way for user to add current location, but this works for now
            if (showDeviceLocationCard) {
                item {
                    UseDeviceLocationCard(
                        onClick = {
                            if (!isDeviceLocationLoading) {
                                onAddCurrentLocation()
                            }
                        },
                        isDeviceLocationLoading
                    )
                    Gap(vertical = 8.dp)
                }
            }
            itemsIndexed(locations, key = { _, item -> item.id }) { index, location ->
                val weather = weatherMap[location.id]

                val icon = weather?.current?.weatherCondition
                    ?: WeatherCondition.NO_CONDITION_FOUND

                val description = if (weather != null && weather.current.lastUpdatedInMilli != -1L)
                    stringResource(
                        R.string.time_last_updated, getLastUpdatedTimeString(
                            context,
                            weather.current.lastUpdatedInMilli
                        )
                    ) else stringResource(R.string.weather_no_data)

                val isFirst = index == 0
                val isLast = index == locations.lastIndex
                val isOnly = locations.size == 1


                val shape = when {
                    isOnly -> RoundedCornerShape(ShapeRadius.Large)
                    isFirst -> RoundedCornerShape(
                        topStart = ShapeRadius.Large,
                        topEnd = ShapeRadius.Large,
                        bottomStart = ShapeRadius.ExtraSmall,
                        bottomEnd = ShapeRadius.ExtraSmall
                    )

                    isLast -> RoundedCornerShape(
                        topStart = ShapeRadius.ExtraSmall,
                        topEnd = ShapeRadius.ExtraSmall,
                        bottomStart = ShapeRadius.Large,
                        bottomEnd = ShapeRadius.Large
                    )

                    else -> RoundedCornerShape(ShapeRadius.ExtraSmall)
                }

                LocationItem(
                    title = location.name,
                    description = description,
                    onClick = {
                        onLocationSelect(location)
                    },
                    icon = icon.toIcon(
                        targetTimeMilli = weather?.current?.time ?: System.currentTimeMillis(),
                        daily = weather?.daily?.firstOrNull()
                    ),
                    isSelected = location.id == activeLocation?.id,
                    onLongClick = { onLongClick(location) },
                    isDefault = location.isDefault,
                    isDeviceLocation = location.isDeviceLocation,
                    shape = shape
                )


                // FLOATING ACTION BUTTON SPACING
                if (index == locations.size - 1) {
                    Gap(150.dp)
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UseDeviceLocationCard(onClick: () -> Unit, isLoading: Boolean = false) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(ShapeRadius.ExtraLarge)),
        onClick = onClick,
        shape = RoundedCornerShape(ShapeRadius.ExtraLarge),
        color = MaterialTheme.colorScheme.surfaceBright,
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceBright),
            leadingContent = {
                Box(
                    Modifier
                        .size(52.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer,
                            shape = CircleShape
                        ), contentAlignment = Alignment.Center
                ) {
                    if (!isLoading) {
                        SettingsTileIcon(R.drawable.location_searching_24px)
                    } else {
                        LoadingIndicator()
                    }
                }
            },
            headlineContent = {
                Text(
                    "Use current location",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            supportingContent = {
                Text(
                    "Detect your device’s current location automatically and add it to your list",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
        )
    }
}