package com.pranshulgg.weathermaster.feature.locations

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
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.WeatherConditions
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.toIcon
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.SettingsTileIcon
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius
import com.pranshulgg.weathermaster.core.utils.WeatherUtils
import com.pranshulgg.weathermaster.feature.shared.components.LocationItem
import java.time.Instant

@Composable
fun LocationsScreenContent(
    locations: List<Location>,
    onLongClick: (Location) -> Unit,
    onLocationSelect: (Location) -> Unit,
    activeLocation: Location? = null,
    weatherForLocations: List<Weather> = emptyList(),
    onAddCurrentLocation: () -> Unit
) {

    val weatherMap = weatherForLocations.associateBy { it.location.id }
    val context = LocalContext.current


    AnimatedContent(
        targetState = locations,
        transitionSpec = { fadeIn() togetherWith fadeOut() }) { locations ->
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // TODO: Still need to find a better way for user to add current location, but this works for now
            if (locations.none { it.isDeviceLocation }) {
                item {
                    UseDeviceLocationCard(
                        onClick = onAddCurrentLocation
                    )
                    Gap(vertical = 8.dp)
                }
            }
            itemsIndexed(locations, key = { _, item -> item.id }) { index, location ->
                val weather = weatherMap[location.id]
                val icon =
                    weather?.current?.weatherCondition ?: WeatherConditions.NO_CONDITION_FOUND
                val description =
                    if (weather != null && weather.current.lastUpdatedSecs != -1L) stringResource(
                        R.string.time_last_updated,
                        WeatherUtils.getLastUpdatedTimeString(
                            context,
                            weather.current.lastUpdatedSecs
                        )
                    ) else "No weather data available"




                LocationItem(
                    title = location.name,
                    description = description,
                    onClick = {
                        onLocationSelect(location)
                    },
                    icon = icon.toIcon(
                        targetTimeSecs = weather?.current?.time ?: Instant.now().epochSecond,
                        daily = weather?.daily?.firstOrNull()
                    ),
                    isSelected = location.id == activeLocation?.id,
                    onLongClick = { onLongClick(location) },
                    isDefault = location.isDefault,
                    isDeviceLocation = location.isDeviceLocation
                )


                // FLOATING ACTION BUTTON SPACING
                if (index == locations.size - 1) {
                    Gap(150.dp)
                }
            }
        }
    }

}

@Composable
private fun UseDeviceLocationCard(onClick: () -> Unit) {
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
                    SettingsTileIcon(R.drawable.location_searching_24px)
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