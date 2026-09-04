package com.pranshulgg.weather_master_app.feature.main

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.pranshulgg.weather_master_app.core.managers.WeatherBlocksManager
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
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
import kotlinx.coroutines.launch

data class MainScreenWeatherUiState(
    val weatherUnits: WeatherUnits = WeatherUnits.getDefault(),
    val blocks: List<WeatherBlock> = WeatherBlock.getDefault(),
)

data class MainScreenUiState(
    val isWeatherSourcesForLocationSheetOpen: Boolean = false,
    val isWeatherSourcesInfoForLocationSheetOpen: Boolean = false,
    val isNewVersionAvailable: Boolean = false,
    val lastestVersionUrl: String = "https://github.com/PranshulGG/WeatherMaster/releases/latest",
    val isUnsupportedSourceDialogOpen: Boolean = false,
    val isChangelogSheetOpen: Boolean = false,
    val isGooglePlayStoreRelease: Boolean = BuildConfig.IS_PLAYSTORE_BUILD
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, weatherViewModel: WeatherViewModel) {
    val viewModel: MainScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.value
    val uriHandler = LocalUriHandler.current
    val savedVersion = PreferencesHelper.getString("saved_version")
    val prefs = LocalAppPrefs.current

    val locationStore = viewModel.location.collectAsState().value
    val weatherStore = viewModel.weather.collectAsState().value
    val unitsStore = viewModel.units.collectAsState().value
    val weatherBlocks = viewModel.weatherBlocks.collectAsState().value


    if (locationStore.locations.isEmpty()) {
        IntroScreen(navController)
        return
    }

    LaunchedEffect(Unit) {
        weatherViewModel.errors.collect { exp ->
            SnackbarManager.show(messageResource = exp.toMessageRes())
        }
    }

    val context = LocalContext.current
    val activeLocation = locationStore.activeLocation

    val density = LocalDensity.current
    val widthDp = with(density) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    val isTabletLike = if (!prefs.isTabletLayoutEnabled) false else widthDp > 600.dp

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

    LaunchedEffect(weatherViewModel.isUnSupportedSource) {
        if (weatherViewModel.isUnSupportedSource) {
            viewModel.showUnsupportedSelectedSourceDialog()
        }
    }

    LaunchedEffect(uiState.isNewVersionAvailable) {
        if (uiState.isNewVersionAvailable) {
            SnackbarManager.show(
                R.string.message_new_version_available,
                actionLabel = R.string.action_view,
                onAction = {
                    uriHandler.openUri(uiState.lastestVersionUrl)
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
                onLocationSelect = {
                    if (activeLocation == it) return@LocationsScreen
                    weatherViewModel.setActiveLoading()
                    scope.launch {
                        drawerState.close() // wait until drawer fully closes
                        weatherViewModel.setActiveLocation(it, skipLoading = true)
                    }
                },
            )
        },
        drawerState = drawerState,
        content = {
            MainScreenScaffold(
                navController = navController,
                drawerState = drawerState,
                weatherStore = weatherStore,
                onRefresh = {
                    weatherViewModel.setActiveLoading()
                    weatherViewModel.refreshWeather(activeLocation)
                },
                onEditLocation = {
                    navController.navigate(NavRoutes.editLocation(activeLocation!!.id))
                },
                context = context,
                onWeatherSourceInfoClick = viewModel::showWeatherSourcesInfoForLocationSheet,
                isTabletLike = isTabletLike,
                prefs = prefs,
                units = unitsStore,
                isLoading = locationStore.isActiveLocationLoading,
                activeLocation = locationStore.activeLocation,
                weatherBlocks = weatherBlocks,
                onUpdateBlocks = {
                    viewModel.saveBlocks(it)
                }
            )
        }
    )


    // WEATHER SOURCES INFO DIALOG
    MainScreenBottomSheets.WeatherSourcesInfoForLocationSheet(viewModel, activeLocation, sheetState)


    // SOURCE NOT AVAILABLE
    MainScreenDialogs.UnsupportedSelectedSourceDialog(
        show = uiState.isUnsupportedSourceDialogOpen,
        onDismiss = viewModel::hideUnsupportedSelectedSourceDialog,
        onConfirm = {
            locationStore.activeLocation?.let {
                navController.navigate(NavRoutes.editLocation(it.id))
            }
        }
    )


    // CHANGELOG DIALOG
    SharedBottomSheet.ChangelogBottomSheet(
        sheetState,
        onDismiss = viewModel::hideChangelogSheet,
        show = uiState.isChangelogSheetOpen
    )
}



