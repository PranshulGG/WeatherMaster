package com.pranshulgg.weather_master_app.feature.locations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.components.Tooltip
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.provider.devicelocation.GetDeviceLocation
import com.pranshulgg.weather_master_app.data.provider.devicelocation.rememberBackgroundLocationPermissionLauncher
import com.pranshulgg.weather_master_app.data.provider.devicelocation.rememberLocationPermissionLauncher
import com.pranshulgg.weather_master_app.feature.intro.toDomain
import com.pranshulgg.weather_master_app.feature.locations.ui.LocationScreenConfirmationDialog
import com.pranshulgg.weather_master_app.feature.locations.ui.LocationScreenSheet
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel
import com.pranshulgg.weather_master_app.feature.shared.ui.SharedDialogs

data class LocationsScreenUiState(
    val isConfirmationDialogOpen: Boolean = false,
    val longClickedLocation: Location? = null,
    val isBottomSheetOpen: Boolean = false,
    val isDeviceLocationLoading: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LocationsScreen(
    onBack: () -> Unit,
    navController: NavController,
    locations: List<Location>,
    activeLocation: Location?,
    onLocationSelect: (Location) -> Unit
) {

    val viewModel: LocationsScreenViewModel = hiltViewModel()
    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val uiState = viewModel.uiState

    val weatherForLocations by viewModel.allLocationsWeather.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    var backgroundLocationPermissionInfoDialogOpen by remember { mutableStateOf(false) }
    var locationPermissionInfoDialogOpen by remember { mutableStateOf(false) }

    val requestLocation = rememberLocationPermissionLauncher(
        onForegroundGranted = {
            backgroundLocationPermissionInfoDialogOpen = true
        },
        onDenied = {
            SnackbarManager.show(R.string.location_permission_required)
        }
    )

    val requestBackgroundLocation = rememberBackgroundLocationPermissionLauncher(
        onGranted = {
            viewModel.saveDeviceLocation()
        },
        onContinueWithoutBackground = {
            viewModel.saveDeviceLocation()
        },
        onDenied = {
            SnackbarManager.show(R.string.location_permission_required)
            backgroundLocationPermissionInfoDialogOpen = true
        }
    )



    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopBar(onBack)
        },
        floatingActionButton = {
            FloatingButton(navController)
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            LocationsScreenContent(
                locations,
                onLongClick = {
                    viewModel.showBottomSheet(it)
                },
                onLocationSelect = {
                    onLocationSelect(it)
                },
                activeLocation = activeLocation,
                weatherForLocations,
                onAddCurrentLocation = { locationPermissionInfoDialogOpen = true },
                uiState.value.isDeviceLocationLoading
            )
        }
    }


    LocationScreenConfirmationDialog(weatherViewModel, viewModel)
    LocationScreenSheet(viewModel, sheetState)

    SharedDialogs.DeviceBackgroundLocationPermissionInfoDialog(
        show = backgroundLocationPermissionInfoDialogOpen,
        onConfirm = {
            backgroundLocationPermissionInfoDialogOpen = false
            requestBackgroundLocation()
        },
        onDismiss = { backgroundLocationPermissionInfoDialogOpen = false }
    )

    SharedDialogs.DeviceLocationPermissionInfoDialog(
        show = locationPermissionInfoDialogOpen,
        onConfirm = {
            requestLocation()
        },
        onDismiss = { locationPermissionInfoDialogOpen = false }
    )
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        title = {
            Text(
                stringResource(R.string.locations),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            Tooltip(
                "Navigate up",
                preferredPosition = TooltipAnchorPosition.Below,
                spacing = 10.dp
            ) {
                IconButton(
                    onClick = { onBack() }, shapes = IconButtonDefaults.shapes()
                ) {
                    Symbol(
                        R.drawable.arrow_back_24px,
                        desc = "arrow back icon",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    )
}

@Composable
private fun FloatingButton(navController: NavController) {
    FloatingActionButton(
        onClick = {
            navController.navigate(NavRoutes.SEARCH)
        },
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier
            .size(96.dp),
        shape = CircleShape
    ) {
        Symbol(
            R.drawable.search_24px,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            size = 36.dp
        )
    }
}