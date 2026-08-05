package com.pranshulgg.weather_master_app.feature.main

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.BuildConfig
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.feature.intro.IntroScreen
import com.pranshulgg.weather_master_app.feature.locations.LocationsScreen
import com.pranshulgg.weather_master_app.feature.main.ui.MainScreenBottomSheets
import com.pranshulgg.weather_master_app.feature.main.ui.MainScreenDialogs
import com.pranshulgg.weather_master_app.feature.main.ui.NavigationDrawer
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel
import com.pranshulgg.weather_master_app.feature.shared.ui.SharedBottomSheet
import com.pranshulgg.weather_master_app.feature.shared.ui.SharedDialogs
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
    val isAirQualityLoading: Boolean = false,
    val isUnsupportedSource: Boolean = false,
    val alerts: List<Alert> = emptyList()
)

data class MainScreenUiState(
    val isWeatherSourcesForLocationSheetOpen: Boolean = false,
    val isWeatherSourcesInfoForLocationSheetOpen: Boolean = false,
    val isNewVersionAvailable: Boolean = false,
    val lastestVersionUrl: String = "https://github.com/PranshulGG/WeatherMaster/releases/latest",
    val isUnsupportedSourceDialogOpen: Boolean = false,
    val isChangelogSheetOpen: Boolean = false,
    val isGooglePlayStoreRelease: Boolean = false // Set TRUE when building for play store
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, weatherViewModel: WeatherViewModel) {
    val uiState by weatherViewModel.uiState
    val viewModel: MainScreenViewModel = hiltViewModel()
    val mainScreenUiState = viewModel.uiState.value
    val uriHandler = LocalUriHandler.current
    val savedVersion = PreferencesHelper.getString("saved_version")

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

    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Expanded, SheetValue.Hidden)
    )


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

    LaunchedEffect(uiState.isUnsupportedSource) {
        if (uiState.isUnsupportedSource) {
            viewModel.showUnsupportedSelectedSourceDialog()
        }
    }

    LaunchedEffect(mainScreenUiState.isNewVersionAvailable) {
        if (mainScreenUiState.isNewVersionAvailable) {
            SnackbarManager.show(
                R.string.message_new_version_available,
                actionLabel = R.string.action_view,
                onAction = {
                    uriHandler.openUri(mainScreenUiState.lastestVersionUrl)
                },
                duration = SnackbarDuration.Indefinite
            )

            viewModel.dismissNewVersionSnackbar()
        }
    }

    LaunchedEffect(Unit) {

        if (BuildConfig.APP_VERSION != savedVersion) {
            viewModel.showChangelogSheet()
            PreferencesHelper.setString("saved_version", BuildConfig.APP_VERSION)
        }
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
                    scope.launch {
                        drawerState.close() // wait until drawer fully closes
                        weatherViewModel.setActiveLocation(it)
                    }
                },
                weatherViewModel
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
                    navController.navigate(NavRoutes.editLocation(activeLocation!!.id))
                },
                context,
                onWeatherSourceInfoClick = viewModel::showWeatherSourcesInfoForLocationSheet,
                isTabletLike
            )
        }
    )


    // WEATHER SOURCES INFO DIALOG
    MainScreenBottomSheets.WeatherSourcesInfoForLocationSheet(viewModel, activeLocation, sheetState)

    // SOURCE NOT AVAILABLE
    MainScreenDialogs.UnsupportedSelectedSourceDialog(
        mainScreenUiState.isUnsupportedSourceDialogOpen,
        onDismiss =
            viewModel::hideUnsupportedSelectedSourceDialog,
        onConfirm = { viewModel.showWeatherSourcesForLocationSheet(uiState.isLoading) })

    // CHANGELOG DIALOG
    SharedBottomSheet.ChangelogBottomSheet(
        sheetState,
        onDismiss = viewModel::hideChangelogSheet,
        show = mainScreenUiState.isChangelogSheetOpen
    )
}



