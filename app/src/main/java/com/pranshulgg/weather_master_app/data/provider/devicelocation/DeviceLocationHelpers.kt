package com.pranshulgg.weather_master_app.data.provider.devicelocation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.location.LocationManagerCompat
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.io.geojson.GeoJsonReader
import java.io.InputStream
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
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
    longitude: Double,
    chinaOfflineGeocoder: ChinaOfflineGeocoder
): String? {

    if (chinaOfflineGeocoder.checkIsChina(latitude, longitude)) {
        return "CN"
    }

    return suspendCancellableCoroutine { cont ->


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
}


fun Context.isLocationEnabled(): Boolean {
    val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return LocationManagerCompat.isLocationEnabled(locationManager)
}

// FOR LOCATIONS IN CHINA
// NOMINATIM/ANDROID GEOCODER FAILS
@Singleton
class ChinaOfflineGeocoder @Inject constructor(
    @ApplicationContext context: Context
) {
    private val geometryFactory = GeometryFactory()
    private val geoJsonReader = GeoJsonReader(geometryFactory)
    private var chinaGeometry: Geometry? = null

    init {
        val inputStream: InputStream = context.assets.open("china.geo.json")

        val mapper = jacksonObjectMapper()
        val root: Map<String, Any> = mapper.readValue(inputStream)
        val features = root["features"] as List<Map<String, Any>>

        if (features.isNotEmpty()) {
            val firstFeature = features[0]
            val geometryMap = firstFeature["geometry"] as Map<String, Any>
            val geometryJsonString = mapper.writeValueAsString(geometryMap)

            chinaGeometry = geoJsonReader.read(geometryJsonString)
        }
        inputStream.close()
    }

    fun checkIsChina(latitude: Double, longitude: Double): Boolean {
        val targetPoint = geometryFactory.createPoint(Coordinate(longitude, latitude))

        val isInside = chinaGeometry?.covers(targetPoint) ?: false

        return isInside
    }
}