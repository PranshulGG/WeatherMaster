package com.pranshulgg.weathermaster.feature.main.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pranshulgg.weathermaster.core.model.domain.AirQuality
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.domain.WeatherBlock
import com.pranshulgg.weathermaster.core.model.domain.WeatherBlockType
import com.pranshulgg.weathermaster.feature.shared.WeatherViewModel
import com.pranshulgg.weathermaster.feature.shared.components.blocks.AirQualityBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.HumidityBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.MoonBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.PressureBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.SunBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.UvIndexBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.VisibilityBlock
import sh.calvin.reorderable.DragGestureDetector
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Composable
fun WeatherBlocks(
    weather: Weather,
    airQuality: AirQuality?,
    units: AppWeatherUnits,
    context: Context,
    blocks: List<WeatherBlock>
) {

    val viewModel: WeatherViewModel = hiltViewModel()

    val items = blocks.filter { !it.isHidden }

    val lazyGridState = rememberLazyGridState()

    val reorderableState = rememberReorderableLazyGridState(
        lazyGridState = lazyGridState,
        onMove = { from, to ->
            val updated = items.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }

            viewModel.saveBlocks(
                items = updated
            )
        }
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 140.dp),
        state = lazyGridState,
        userScrollEnabled = false,
        modifier = Modifier
            .fillMaxSize()
            .heightIn(max = 1500.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(
            items = items,
            key = { it.type }
        ) { item ->

            ReorderableItem(
                reorderableState,
                key = item.type
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .draggableHandle(
                            dragGestureDetector = DragGestureDetector.LongPress
                        )
                ) {
                    when (item.type) {
                        WeatherBlockType.HUMIDITY_BLOCK -> HumidityBlock(weather, units)
                        WeatherBlockType.VISIBILITY_BLOCK -> VisibilityBlock(
                            weather,
                            units,
                            context
                        )

                        WeatherBlockType.UV_INDEX_BLOCK -> UvIndexBlock(weather, context)
                        WeatherBlockType.PRESSURE_BLOCK -> PressureBlock(weather, units, context)
                        WeatherBlockType.SUN_BLOCK -> SunBlock(weather)
                        WeatherBlockType.MOON_BLOCK -> MoonBlock(weather)
                        WeatherBlockType.AIR_QUALITY -> if (airQuality != null) AirQualityBlock(
                            airQuality
                        ) else null
                    }
                }

            }
        }
    }

}