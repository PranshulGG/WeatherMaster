package com.pranshulgg.weather_master_app.data.provider.devicelocation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.telephony.TelephonyManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

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

            val permanentlyDenied = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
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


// WE WILL TRY EVERYTHING TO MAKE SURE COUNTRY CODE IS AVAILABLE

suspend fun getCountryCode(
    context: Context,
    latitude: Double,
    longitude: Double
): String? = suspendCancellableCoroutine { cont ->

    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    var countryCode = tm.networkCountryIso

    if (countryCode.isNullOrBlank()) {
        countryCode = tm.simCountryIso
    }

    if (!countryCode.isNullOrBlank()) {
        cont.resume(countryCode.uppercase(Locale.ROOT))
        return@suspendCancellableCoroutine
    }

    val geocoder = Geocoder(context, Locale.getDefault())

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                if (!cont.isActive) return@getFromLocation
                val code = addresses.firstOrNull()?.countryCode?.uppercase(Locale.ROOT)
                cont.resume(code)
            }
        } else {
            @Suppress("DEPRECATION")
            val result = geocoder.getFromLocation(latitude, longitude, 1)
            val code = result?.firstOrNull()?.countryCode?.uppercase(Locale.ROOT)

            if (cont.isActive) {
                cont.resume(code)
            }
        }
    } catch (e: Exception) {
        if (cont.isActive) {
            cont.resume(null)
        }
    }
}