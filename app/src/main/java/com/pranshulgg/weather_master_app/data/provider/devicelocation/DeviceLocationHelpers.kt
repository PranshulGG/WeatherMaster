package com.pranshulgg.weather_master_app.data.provider.devicelocation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.collections.isNullOrEmpty

fun Context.setLocationPermissionRequested() {
    getSharedPreferences("permissions", Context.MODE_PRIVATE)
        .edit {
            putBoolean("location_requested", true)
        }
}

fun Context.hasRequestedLocationPermission(): Boolean {
    return getSharedPreferences("permissions", Context.MODE_PRIVATE)
        .getBoolean("location_requested", false)
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun rememberLocationPermissionLauncher(
    onForegroundGranted: () -> Unit,
    onDenied: () -> Unit
): () -> Unit {


    val foregroundLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val fine =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

        val coarse =
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fine || coarse) {
            onForegroundGranted()
        } else {
            onDenied()
        }
    }

    return {
        foregroundLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun rememberBackgroundLocationPermissionLauncher(
    onGranted: () -> Unit,
    onContinueWithoutBackground: () -> Unit,
    onDenied: () -> Unit
): () -> Unit {

    val context = LocalContext.current
    val activity = context as Activity

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->

        if (granted) {
            onGranted()
        } else {

            val permanentlyDenied =
                context.hasRequestedLocationPermission() &&
                        !ActivityCompat.shouldShowRequestPermissionRationale(
                            activity,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )

            if (permanentlyDenied) {
                onContinueWithoutBackground()
            } else {
                onDenied()
            }
        }
    }

    return {
        val alreadyGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (alreadyGranted) {
            onGranted()
        } else {
            context.setLocationPermissionRequested()

            launcher.launch(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }
}


suspend fun getCountryCode(
    context: Context,
    latitude: Double,
    longitude: Double
): String? = suspendCancellableCoroutine { cont ->

    val geocoder = Geocoder(context, Locale.getDefault())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
            val code = addresses.firstOrNull()?.countryCode
            cont.resume(code) { cause, _, _ -> null?.let { it(cause) } }
        }
    } else {
        @Suppress("DEPRECATION")
        val result = geocoder.getFromLocation(latitude, longitude, 1)
        val code = result?.firstOrNull()?.countryCode
        cont.resume(code) { cause, _, _ -> null?.let { it(cause) } }
    }
}