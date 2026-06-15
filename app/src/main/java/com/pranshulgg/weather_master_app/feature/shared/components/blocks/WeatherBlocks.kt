package com.pranshulgg.weather_master_app.feature.shared.components.blocks

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
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlockType
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isCurrentAirQualitySafe
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel
import sh.calvin.reorderable.DragGestureDetector
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

private data class BlockRules(
    val isDaily: Boolean,
    val rainForTheDay: Double,
    val snowForTheDay: Double,
    val isAirQualityValid: Boolean,
    val isUvIndexValid: Boolean,
    val isPressureValid: Boolean,
    val isVisibilityValid: Boolean,
    val isWindValid: Boolean

)

private fun shouldShow(block: WeatherBlock, rules: BlockRules): Boolean {
    return when {
        block.isHidden -> false
        rules.isDaily != block.isDaily -> false
        rules.rainForTheDay == 0.0 && block.type == WeatherBlockType.RAIN_BLOCK -> false
        rules.snowForTheDay == 0.0 && block.type == WeatherBlockType.SNOW_BLOCK -> false
        !rules.isAirQualityValid && block.type == WeatherBlockType.AIR_QUALITY_BLOCK -> false
        !rules.isUvIndexValid && block.type == WeatherBlockType.UV_INDEX_BLOCK -> false
        !rules.isPressureValid && block.type == WeatherBlockType.PRESSURE_BLOCK -> false
        !rules.isVisibilityValid && block.type == WeatherBlockType.VISIBILITY_BLOCK -> false
        !rules.isWindValid && block.type == WeatherBlockType.WIND_BLOCK -> false
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
    dailyIndex: Int = 0,
    navController: NavController
) {


    val viewModel: WeatherViewModel = hiltViewModel()


    val rainForTheDay =
        PrecipitationUnit.MM.convert(weather.daily[dailyIndex].rainSum, units.precipitationUnit)
            ?: 0.0
    val snowForTheDay =
        PrecipitationUnit.CM.convert(weather.daily[dailyIndex].snowfallSum, units.precipitationUnit)
            ?: 0.0

    // Some sources do not provide precipitation separately rain/snow
    val isOnlyPrecipitationData = !weather.location.source.providesSnowFall()
    val isAirQualityValid = airQuality != null && isCurrentAirQualitySafe(airQuality)


    val isUvIndexValid = weather.daily[dailyIndex].isUvIndexMaxValid()
            && weather.current.isUvIndexValid()

    val isPressureValid = weather.current.isPressureValid()

    val isVisibilityValid = weather.current.isVisibilityValid()
    val isWindValid =
        if (isDaily) weather.daily[dailyIndex].isWindSpeedValid() else weather.current.isWindSpeedValid()

    val prefs = LocalAppPrefs.current

    val rules = BlockRules(
        isDaily,
        rainForTheDay,
        snowForTheDay,
        isAirQualityValid,
        isUvIndexValid,
        isPressureValid,
        isVisibilityValid,
        isWindValid
    )


    /**
     * Only few blocks can be shown on the daily forecast screen
     * [Rain, Snow, Moon, Sun, UV index, Wind] probably more depending on the data
     */
    val items = blocks.filter { shouldShow(it, rules) }


    val blocksHidden = blocks.filter { it !in items }


    val lazyGridState = rememberLazyGridState()

    val onClickBlock: (String) -> Unit = {
        navController.navigate(
            NavRoutes.blockScreen(it, dailyIndex, weather.location.id)
        )
    }


    val reorderableState = rememberReorderableLazyGridState(
        lazyGridState = lazyGridState,
        onMove = { from, to ->
            val updated = items.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }

            updatedBlockOrder(updated)
            viewModel.saveBlocks(
                items = updated.plus(blocksHidden),
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
            key = { "${it.id}_${it.type}" }
        ) { item ->

            ReorderableItem(
                reorderableState,
                key = "${item.id}_${item.type}"
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .draggableHandle(
                            dragGestureDetector = DragGestureDetector.LongPress
                        )
                ) {
                    when (item.type) {
                        WeatherBlockType.HUMIDITY_BLOCK -> HumidityBlock(
                            weather,
                            units,
                            onClickBlock = { onClickBlock(NavRoutes.HUMIDITY) })

                        WeatherBlockType.VISIBILITY_BLOCK -> VisibilityBlock(
                            weather,
                            units,
                            context,
                            onClickBlock = { onClickBlock(NavRoutes.VISIBILITY) }
                        )

                        WeatherBlockType.UV_INDEX_BLOCK -> UvIndexBlock(
                            weather,
                            context,
                            isDaily,
                            dailyIndex,
                            onClickBlock = { onClickBlock(NavRoutes.UV_INDEX) }
                        )

                        WeatherBlockType.PRESSURE_BLOCK -> PressureBlock(
                            weather,
                            units,
                            context,
                            onClickBlock = { onClickBlock(NavRoutes.PRESSURE) })

                        WeatherBlockType.SUN_BLOCK -> SunBlock(
                            weather,
                            dailyIndex,
                            prefs,
                            onClickBlock = { onClickBlock(NavRoutes.SUN_MOON) })

                        WeatherBlockType.MOON_BLOCK -> MoonBlock(
                            weather,
                            dailyIndex,
                            prefs,
                            onClickBlock = { onClickBlock(NavRoutes.SUN_MOON) })

                        WeatherBlockType.AIR_QUALITY_BLOCK -> AirQualityBlock(airQuality, context)


                        WeatherBlockType.RAIN_BLOCK -> RainBlock(
                            rainForTheDay,
                            context,
                            units,
                            isOnlyPrecipitation = isOnlyPrecipitationData,
                            onClickBlock = { onClickBlock(NavRoutes.RAIN) }
                        )

                        WeatherBlockType.SNOW_BLOCK -> SnowBlock(
                            snowForTheDay,
                            context,
                            units,
                            onClickBlock = { onClickBlock(NavRoutes.SNOW) }
                        )

                        WeatherBlockType.WIND_BLOCK -> WindBlock(
                            weather,
                            context,
                            isDaily,
                            dailyIndex,
                            units,
                            onClickBlock = { onClickBlock(NavRoutes.WIND) })
                    }
                }

            }
        }
    }

}