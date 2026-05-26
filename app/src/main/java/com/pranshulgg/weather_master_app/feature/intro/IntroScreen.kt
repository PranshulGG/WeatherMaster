package com.pranshulgg.weather_master_app.feature.intro

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.core.utils.ids.UuidGenerator
import com.pranshulgg.weather_master_app.data.provider.devicelocation.DeviceLocation
import com.pranshulgg.weather_master_app.data.provider.devicelocation.GetDeviceLocation
import com.pranshulgg.weather_master_app.data.provider.devicelocation.getCountryCode
import com.pranshulgg.weather_master_app.data.provider.devicelocation.rememberBackgroundLocationPermissionLauncher
import com.pranshulgg.weather_master_app.data.provider.devicelocation.rememberLocationPermissionLauncher
import com.pranshulgg.weather_master_app.feature.shared.ui.SharedDialogs
import java.util.TimeZone

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IntroScreen(navController: NavController) {

    val context = LocalContext.current
    val viewModel: IntroScreenViewModel = hiltViewModel()
    var isLoading by remember { mutableStateOf(false) }
    var backgroundLocationPermissionInfoDialogOpen by remember { mutableStateOf(false) }
    var locationPermissionInfoDialogOpen by remember { mutableStateOf(false) }

    val continueWithLocation = {
        isLoading = true
        GetDeviceLocation().getDeviceLocation(
            context,
            onTimeout = {
                SnackbarManager.show(R.string.current_location_not_found)
                isLoading = false
            }) { location ->
            if (location.latitude == null || location.longitude == null) {
                SnackbarManager.show(R.string.current_location_not_found)
                isLoading = false
                return@getDeviceLocation
            }

            viewModel.saveDeviceLocation(location)
        }
    }

    val requestLocation = rememberLocationPermissionLauncher(
        onForegroundGranted = {
            backgroundLocationPermissionInfoDialogOpen = true
        },
        onDenied = {
            SnackbarManager.show(R.string.location_permission_required)
            isLoading = false
        }
    )

    val requestBackgroundLocation = rememberBackgroundLocationPermissionLauncher(
        onGranted = {
            continueWithLocation()
        },
        onContinueWithoutBackground = {
            continueWithLocation()
        },
        onDenied = {
            SnackbarManager.show(R.string.location_permission_required)
            backgroundLocationPermissionInfoDialogOpen = true
            isLoading = false
        }
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) { paddingValues ->

        val btnSize = ButtonDefaults.MediumContainerHeight


        Box(
            Modifier.fillMaxSize()
        ) {

            Column(
                Modifier
                    .align(Alignment.Center)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = paddingValues.calculateTopPadding()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon()
                Gap(24.dp)
                Text(
                    "Let’s Get Your Forecast",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.W700
                )
                Gap(8.dp)
                Text(
                    "Allow location access to see your local forecast",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Gap(28.dp)
                Button(
                    enabled = !isLoading,
                    onClick = {
                        locationPermissionInfoDialogOpen = true
                    },
                    modifier = Modifier
                        .heightIn(btnSize)
                        .fillMaxWidth(0.7f),
                    contentPadding = ButtonDefaults.contentPaddingFor(btnSize),
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Enable Location", style = ButtonDefaults.textStyleFor(btnSize))
                }
                Gap(12.dp)
                OutlinedButton(
                    onClick = {
                        navController.navigate(NavRoutes.SEARCH)
                    },
                    modifier = Modifier
                        .heightIn(btnSize)
                        .fillMaxWidth(0.7f),
                    contentPadding = ButtonDefaults.contentPaddingFor(btnSize),
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(
                        "Search for a City",
                        style = ButtonDefaults.textStyleFor(btnSize),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .rotate(60f)
                    .alpha(0.2f)
            ) {
                WeatherIconBox(R.drawable.weather_very_hot, size = 150.dp)
            }

            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = 100.dp)
                    .rotate(60f)
                    .alpha(0.1f)
            ) {
                WeatherIconBox(R.drawable.weather_clear_night, size = 150.dp)
            }

            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .offset(y = 500.dp)
                    .rotate(60f)
                    .alpha(0.1f)
            ) {
                WeatherIconBox(R.drawable.weather_clear_day, size = 150.dp)
            }

            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .rotate(60f)
                    .alpha(0.1f)
            ) {
                WeatherIconBox(R.drawable.weather_very_cold, size = 150.dp)
            }
        }
    }

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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Icon() {
    Surface(
        shape = MaterialShapes.Cookie9Sided.toShape(),
        modifier = Modifier
            .height(100.dp)
            .width(100.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            Symbol(
                R.drawable.location_on_24px,
                size = 56.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }

}

suspend fun DeviceLocation.toDomain(context: Context): Location {


    val formattedLatitude = kotlin.math.round(latitude!! * 100000) / 100000
    val formattedLongitude = kotlin.math.round(longitude!! * 100000) / 100000

    val countryCode = getCountryCode(context, formattedLatitude, formattedLongitude)


    return Location(
        id = UuidGenerator.generateId(),
        name = "$formattedLatitude, $formattedLongitude", // TODO: Reverse geocoding support
        latitude = formattedLatitude,
        longitude = formattedLongitude,
        country = "",
        timezone = TimeZone.getDefault().id,
        countryCode = countryCode,
        state = "",
        source = WeatherSource.OPEN_METEO,
        isFavorite = false,
        isPinned = false,
        isDefault = false, // Repository can handle it
        isDeviceLocation = true
    )

}