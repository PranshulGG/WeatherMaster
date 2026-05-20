package com.pranshulgg.weathermaster.feature.main

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.core.model.domain.airquality.AirQuality
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.location.Location
import com.pranshulgg.weathermaster.core.model.domain.weather.Weather
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weathermaster.core.model.sources.WeatherSource
import com.pranshulgg.weathermaster.feature.intro.IntroScreen
import com.pranshulgg.weathermaster.feature.locations.LocationsScreen
import com.pranshulgg.weathermaster.feature.main.ui.NavigationDrawer
import com.pranshulgg.weathermaster.feature.shared.WeatherViewModel
import com.pranshulgg.weathermaster.feature.shared.ui.SharedDialogs
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
    val isWeatherSourcesDialogOpen: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val uiState by weatherViewModel.uiState
    val location = uiState.weather?.location
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
                    viewModel.showWeatherSourceDialog(uiState.isLoading)
                },
                isTabletLike,
                context
            )
        }
    )


    // WEATHER SOURCES DIALOG
    SharedDialogs.WeatherProvidersForLocationDialog(
        countryCode = location?.countryCode,
        show = viewModel.uiState.value.isWeatherSourcesDialogOpen,
        isEditing = true,
        selectedSource = location?.source ?: WeatherSource.OPEN_METEO,
        onSave = {
            if (location != null) {
                weatherViewModel.updateSourceForLocation(location, it)
            }
        },
        onCancel = viewModel::hideWeatherSourceDialog
    )
}



