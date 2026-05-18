package com.pranshulgg.weathermaster.feature.shared.components.blocks

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pranshulgg.weathermaster.core.model.domain.airquality.AirQuality
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.weather.Weather
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherBlockType
import com.pranshulgg.weathermaster.core.model.weather.PrecipitationUnits
import com.pranshulgg.weathermaster.core.utils.weather.UnitConverter
import com.pranshulgg.weathermaster.core.utils.weather.cache.isCurrentAirQualitySafe
import com.pranshulgg.weathermaster.feature.shared.WeatherViewModel
import sh.calvin.reorderable.DragGestureDetector
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

private data class BlockRules(
    val isDaily: Boolean,
    val rainForTheDay: Double,
    val snowForTheDay: Double,
    val isAirQualityValid: Boolean
)

private fun shouldShow(block: WeatherBlock, rules: BlockRules): Boolean {
    return when {
        block.isHidden -> false
        rules.isDaily != block.isDaily -> false
        rules.rainForTheDay == 0.0 && block.type == WeatherBlockType.RAIN_BLOCK -> false
        rules.snowForTheDay == 0.0 && block.type == WeatherBlockType.SNOW_BLOCK -> false
        !rules.isAirQualityValid && block.type == WeatherBlockType.AIR_QUALITY_BLOCK -> false
        else -> true
    }
}

@Composable
fun WeatherBlocks(
    weather: Weather,
    airQuality: AirQuality?,
    units: WeatherUnits,
    context: Context,
    blocks: List<WeatherBlock>,
    isDaily: Boolean = false,

    // Need this so the daily screen can also update block order
    updatedBlockOrder: (List<WeatherBlock>) -> Unit = {},
    dailyIndex: Int = 0
) {

    val viewModel: WeatherViewModel = hiltViewModel()


    val rainForTheDay = UnitConverter.convertPrecipitation(
        weather.daily[dailyIndex].rainSum,
        PrecipitationUnits.MM, units.precipitationUnit
    )

    val snowForTheDay = UnitConverter.convertPrecipitation(
        weather.daily[dailyIndex].snowfallSum ?: 0.0,
        PrecipitationUnits.CM, units.precipitationUnit
    )

    val isAirQualityValid = airQuality != null && isCurrentAirQualitySafe(airQuality)


    val rules = BlockRules(isDaily, rainForTheDay, snowForTheDay, isAirQualityValid)


    /**
     * Only few blocks can be shown on the daily forecast screen
     * [Rain, Snow, Moon, Sun, UV index, Wind] probably more depending on the data
     */
    val items = blocks.filter { shouldShow(it, rules) }


    val lazyGridState = rememberLazyGridState()


    val reorderableState = rememberReorderableLazyGridState(
        lazyGridState = lazyGridState,
        onMove = { from, to ->
            val updated = items.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }

            updatedBlockOrder(updated)
            viewModel.saveBlocks(
                items = updated,
                isDaily = isDaily
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
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(
            items = items,
            key = { it.id }
        ) { item ->

            ReorderableItem(
                reorderableState,
                key = item.id
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

                        WeatherBlockType.UV_INDEX_BLOCK -> UvIndexBlock(
                            weather,
                            context,
                            isDaily,
                            dailyIndex
                        )

                        WeatherBlockType.PRESSURE_BLOCK -> PressureBlock(weather, units, context)

                        WeatherBlockType.SUN_BLOCK -> SunBlock(weather, dailyIndex)
                        WeatherBlockType.MOON_BLOCK -> MoonBlock(weather, dailyIndex)
                        WeatherBlockType.AIR_QUALITY_BLOCK -> AirQualityBlock(airQuality, context)


                        WeatherBlockType.RAIN_BLOCK -> RainBlock(
                            rainForTheDay,
                            context,
                            units
                        )

                        WeatherBlockType.SNOW_BLOCK -> SnowBlock(
                            snowForTheDay,
                            context,
                            units
                        )

                        WeatherBlockType.WIND_BLOCK -> WindBlock(
                            weather,
                            context,
                            isDaily,
                            dailyIndex,
                            units
                        )
                    }
                }

            }
        }
    }

}