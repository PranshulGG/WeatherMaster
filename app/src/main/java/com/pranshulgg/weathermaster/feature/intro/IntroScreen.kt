package com.pranshulgg.weathermaster.feature.intro

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
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
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.WeatherProviders
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.prefs.LocalAppPrefs
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.components.WeatherIconBox
import com.pranshulgg.weathermaster.core.ui.navigation.NavRoutes
import com.pranshulgg.weathermaster.core.utils.UuidGenerator
import com.pranshulgg.weathermaster.data.provider.DeviceLocation
import com.pranshulgg.weathermaster.data.provider.getDeviceLocation
import java.util.TimeZone

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IntroScreen(navController: NavController) {

    val context = LocalContext.current
    val viewModel: IntroScreenViewModel = hiltViewModel()
    val prefs = LocalAppPrefs.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fine || coarse) {
            val location = getDeviceLocation(context)

            if (location.latitude == null || location.longitude == null) {
                Toast.makeText(context, "Location was null", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }

            viewModel.saveDeviceLocation(location.toDomain())
            prefs.setFirstStart(false)
        } else {
            Toast.makeText(context, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }

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
                    onClick = {
                        launcher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    modifier = Modifier
                        .heightIn(btnSize)
                        .fillMaxWidth(0.7f),
                    contentPadding = ButtonDefaults.contentPaddingFor(btnSize),
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Enable location", style = ButtonDefaults.textStyleFor(btnSize))
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
                        "Search for a city",
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
}

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

fun DeviceLocation.toDomain(): Location {

    val formattedLatitude = "%.5f".format(latitude).toDouble()
    val formattedLongitude = "%.5f".format(longitude).toDouble()


    return Location(
        id = UuidGenerator().generateId(),
        name = "$formattedLatitude, $formattedLongitude",
        latitude = formattedLatitude,
        longitude = formattedLongitude,
        country = "",
        timezone = TimeZone.getDefault().id,
        countryCode = "",
        state = "",
        provider = WeatherProviders.OPEN_METEO,
        isFavorite = false,
        isPinned = false,
        isDefault = false, // Repository can handle it
        isDeviceLocation = true
    )

}