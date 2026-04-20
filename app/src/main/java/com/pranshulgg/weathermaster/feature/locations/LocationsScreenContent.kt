package com.pranshulgg.weathermaster.feature.locations

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.feature.shared.components.LocationItem

@Composable
fun LocationsScreenContent(
    locations: List<Location>,
    onDelete: (id: String) -> Unit,
    onLocationSelect: (Location) -> Unit,
    activeLocation: Location? = null
) {

    if (activeLocation != null)

        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(locations.size, key = { locations[it].id }) { location ->
                val item = locations[location]

                LocationItem(
                    title = item.name,
                    description = "Description 1",
                    onClick = { onLocationSelect(item) },
                    icon = R.drawable.weather_mostly_clear_day,
                    isSelected = item.id == activeLocation.id
                )
            }
        }

}