package com.pranshulgg.weather_master_app.feature.main

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.feature.intro.IntroScreen
import com.pranshulgg.weather_master_app.feature.locations.LocationsScreen
import com.pranshulgg.weather_master_app.feature.main.ui.MainScreenBottomSheets
import com.pranshulgg.weather_master_app.feature.main.ui.NavigationDrawer
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel
import com.pranshulgg.weather_master_app.feature.shared.ui.SharedBottomSheet
import kotlinx.coroutines.launch

data class MainScreenWeatherUiState(
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val activeLocation: Location? = null,
    val locations: List<Location> = emptyList(),
    val weather: Weather? = null,
    val weatherUnits: WeatherUnits = WeatherUnits.getDefault(),
    val blocks: List<WeatherBlock> = WeatherBlock.getDefault(),
    val isInitialized: Boolean = false,
    val airQuality: AirQuality? = null,
)

data class MainScreenUiState(
    val isWeatherSourcesForLocationSheetOpen: Boolean = false,
    val isWeatherSourcesInfoForLocationSheetOpen: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val uiState by weatherViewModel.uiState
    val viewModel: MainScreenViewModel = hiltViewModel()



    if (uiState.locations.isEmpty()) {
        IntroScreen(navController)
        return
    }

    val context = LocalContext.current
    val activeLocation = uiState.activeLocation
    val density = LocalDensity.current
    val widthDp = with(density) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    val isTabletLike = widthDp > 600.dp
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val closeDrawer = {
        scope.launch { drawerState.close() }
    }


    BackHandler(
        enabled = drawerState.isOpen,
    ) {
        closeDrawer()
    }



    NavigationDrawer(
        drawerContent = {
            LocationsScreen(
                onBack = {
                    closeDrawer()
                },
                navController,
                uiState.locations,
                uiState.activeLocation,
                onLocationSelect = {
                    if (activeLocation == it) return@LocationsScreen
                    weatherViewModel.setLoading(true)
                    if (!isTabletLike) {
                        scope.launch {
                            drawerState.close() // wait until drawer fully closes
                            weatherViewModel.setActiveLocation(it)
                        }
                    } else {
                        weatherViewModel.setActiveLocation(it)
                    }
                },
                isTabletLike
            )
        },
        drawerState = drawerState,
        isTabletLike = isTabletLike,
        content = {
            MainScreenScaffold(
                navController,
                drawerState,
                uiState,
                onRefresh = {
                    if (activeLocation != null) {
                        weatherViewModel.getWeather(
                            activeLocation,
                            activeLocation.source,
                            isManualRefresh = true
                        )
                    }
                },
                onEditLocation = {
                    viewModel.showWeatherSourcesForLocationSheet(uiState.isLoading)
                },
                isTabletLike,
                context,
                onWeatherSourceInfoClick = viewModel::showWeatherSourcesInfoForLocationSheet
            )
        }
    )


    // WEATHER SOURCES DIALOG
    SharedBottomSheet.WeatherSourcesForLocationSheet(
        countryCode = activeLocation?.countryCode,
        show = viewModel.uiState.value.isWeatherSourcesForLocationSheetOpen,
        isEditing = true,
        selectedSource = activeLocation?.source ?: WeatherSource.OPEN_METEO,
        onSave = {
            if (activeLocation != null) {
                weatherViewModel.updateSourceForLocation(activeLocation, it)
            }
        },
        onDismiss = viewModel::hideWeatherSourcesForLocationSheet,
        sheetState = sheetState
    )

    // WEATHER SOURCES INFO DIALOG
    MainScreenBottomSheets.WeatherSourcesInfoForLocationSheet(viewModel, activeLocation, sheetState)
}



