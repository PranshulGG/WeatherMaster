package com.pranshulgg.weathermaster.feature.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.core.model.WeatherBlockType
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.feature.main.components.FroggyContainer
import com.pranshulgg.weathermaster.feature.main.components.MainSearchBar
import com.pranshulgg.weathermaster.feature.main.ui.CurrentWeatherCard
import com.pranshulgg.weathermaster.feature.main.ui.backgroundGradients
import com.pranshulgg.weathermaster.feature.main.ui.weatherAnimations.SnowCanvas
import com.pranshulgg.weathermaster.feature.main.ui.weatherAnimations.StarsCanvas
import com.pranshulgg.weathermaster.feature.main.ui.weatherAnimations.SunCanvas
import com.pranshulgg.weathermaster.feature.main.ui.weatherAnimations.WeatherAnimations
import com.pranshulgg.weathermaster.feature.shared.WeatherViewModel
import com.pranshulgg.weathermaster.feature.shared.components.blocks.HumidityBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.PressureBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.SunBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.UvIndexBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.VisibilityBlock
import com.pranshulgg.weathermaster.feature.shared.ui.DailyCard
import com.pranshulgg.weathermaster.feature.shared.ui.HourlyCard
import sh.calvin.reorderable.DragGestureDetector
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Composable
fun MainScreenScaffold(
    navController: NavController,
    drawerState: DrawerState,
    uiState: MainScreenUiState,
    onRefresh: () -> Unit,
    isTabletLike: Boolean = false
) {


    val pullToRefreshState = rememberPullToRefreshState()
    val weather = uiState.weather
    val units = uiState.weatherUnits
    val scrollState = rememberScrollState()

    val isAnimationVisible by remember {
        derivedStateOf {
            scrollState.value < 30
        }
    }

    Scaffold { paddingValues ->
        Box {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            backgroundGradients(weather),
                            startY = 0f,
                            endY = 1000f
                        )
                    )
            )

//            AnimatedVisibility(visible = isAnimationVisible, enter = fadeIn(), exit = fadeOut()) {
//                weather?.let { WeatherAnimations(weather) }
//            }

            SnowCanvas()

            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                state = pullToRefreshState,
                onRefresh = {
                    onRefresh()
                },
                indicator = {
                    LoadingIndicator(
                        pullToRefreshState,
                        uiState.isLoading,
                        modifier = Modifier
                            .zIndex(99999f)
                            .align(Alignment.TopCenter)
                    )
                },
            ) {
                AnimatedContent(
                    targetState = weather,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { weather ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        MainSearchBar(
                            isFroggyLayout = true,
                            paddingValues = paddingValues,
                            navController,
                            drawerState,
                            uiState.activeLocation,
                            isTabletLike
                        )
                        if (weather != null) {
                            CurrentWeatherCard(weather, units)
                            FroggyContainer(weather)
                            Column(
                                Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 24.dp,
                                    bottom = 24.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                HourlyCard(weather, units)
                                DailyCard(weather, units)
                                ReorderableGridDemo(weather, units)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ReorderableGridDemo(weather: Weather, units: AppWeatherUnits) {

    val context = LocalContext.current
    val viewModel: WeatherViewModel = hiltViewModel()

    var items by remember {
        mutableStateOf(
            viewModel.loadBlocksOrder(context)
        )
    }

    val lazyGridState = rememberLazyGridState()

    val reorderableState = rememberReorderableLazyGridState(
        lazyGridState = lazyGridState,
        onMove = { from, to ->
            val updated = items.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }

            items = updated

            viewModel.saveBlocksOrder(
                context = context,
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
            .heightIn(max = 1000.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(
            items = items,
            key = { it }
        ) { item ->

            ReorderableItem(
                reorderableState,
                key = item
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .draggableHandle(
                            dragGestureDetector = DragGestureDetector.LongPress
                        )
                ) {
                    when (item) {

                        WeatherBlockType.HUMIDITY_BLOCK ->
                            HumidityBlock(weather, units)

                        WeatherBlockType.VISIBILITY_BLOCK ->
                            VisibilityBlock(weather, units)

                        WeatherBlockType.UV_INDEX_BLOCK ->
                            UvIndexBlock(weather)

                        WeatherBlockType.PRESSURE_BLOCK ->
                            PressureBlock(weather, units)

                        WeatherBlockType.SUN_BLOCK ->
                            SunBlock(weather)
                    }
                }

            }
        }
    }
}
