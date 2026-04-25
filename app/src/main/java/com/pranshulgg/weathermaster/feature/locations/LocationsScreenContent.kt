package com.pranshulgg.weathermaster.feature.locations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.core.model.WeatherConditions
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.toIcon
import com.pranshulgg.weathermaster.core.utils.WeatherUtils
import com.pranshulgg.weathermaster.feature.shared.components.LocationItem
import java.time.Instant

@Composable
fun LocationsScreenContent(
    locations: List<Location>,
    onDelete: (id: String) -> Unit,
    onLocationSelect: (Location) -> Unit,
    activeLocation: Location? = null,
    weatherForLocations: List<Weather> = emptyList()
) {

    val weatherMap = weatherForLocations.associateBy { it.location.id }



    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(locations.size, key = { locations[it].id }) { location ->
            val item = locations[location]
            val weather = weatherMap[item.id]
            val icon = weather?.current?.weatherCondition ?: WeatherConditions.NO_CONDITION_FOUND
            val description =
                if (weather != null && weather.current.lastUpdatedSecs != -1L) "Last updated ${
                    WeatherUtils.getLastUpdatedTimeString(
                        weather.current.lastUpdatedSecs
                    )
                }" else "No weather data available"

            AnimatedVisibility(
                visible = activeLocation != null,
                enter = expandVertically() + fadeIn()
            ) {
                LocationItem(
                    title = item.name,
                    description = description,
                    onClick = { onLocationSelect(item) },
                    icon = icon.toIcon(
                        targetTimeSecs = weather?.current?.time ?: Instant.now().epochSecond,
                        daily = weather?.daily?.firstOrNull()
                    ),
                    isSelected = item.id == activeLocation?.id
                )
            }
        }
    }

}