package com.pranshulgg.weather_master_app

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.*
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.util.concurrent.TimeUnit
import android.os.Bundle
import android.net.ConnectivityManager

class MainActivity : FlutterActivity() {

    private val BATTERY_CHANNEL = "com.pranshulgg.battery_optimization"
    private val SERVICE_CHANNEL = "com.pranshulgg.weather_master_app/service"

    private val LOCATION_CHANNEL = "native_location"
    private val REQUEST_CODE_POST_NOTIFICATIONS = 1001

    private var permissionResultPending: MethodChannel.Result? = null

    private lateinit var locationHelper: LocationHelper

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationHelper = LocationHelper(this)
    }


    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

         WorkInfoPlugin(this).registerWith(flutterEngine)


        // Battery optimization channel
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, BATTERY_CHANNEL)
            .setMethodCallHandler(BatteryOptimizationHandler(this))


        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, LOCATION_CHANNEL).setMethodCallHandler { call, result ->
            
    

            when (call.method) {
                 "getCurrentPosition" -> {
                        locationHelper.getCurrentPosition(object : LocationHelper.LocationResult {
                            override fun onSuccess(latitude: Double, longitude: Double) {
                                result.success(mapOf(
                                    "latitude" to latitude.toString(),
                                    "longitude" to longitude.toString()
                                ))
                            }

                            override fun onFailure(error: String) {
                                result.error("LOCATION_ERROR", error, null)
                            }
                        })
                    }

                "reverseGeocode" -> {
                    val lat = call.argument<Double>("latitude") ?: 0.0
                    val lon = call.argument<Double>("longitude") ?: 0.0

                    // Use the new async callback version
                    GeocodeHelper.reverseGeocode(this, lat, lon) { geo ->
                        result.success(geo)
                    }
                }


                else -> result.notImplemented()
            }
        }

        
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, SERVICE_CHANNEL)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "checkNotificationPermission" -> {
                        result.success(isNotificationPermissionGranted())
                    }
                    "requestNotificationPermission" -> {
                        requestNotificationPermission(result)
                    }
                    "startService" -> {
                        val intervalMinutes = when (val value = call.argument<Any>("intervalMinutes")) {
                            is Int -> value.toLong()
                            is Long -> value
                            else -> 90L
                        }

                    scheduleWeatherUpdates(this, intervalMinutes)
                    result.success(true)
                    }

                    "stopService" -> {
                        cancelWeatherUpdates(this)
                        result.success(true)
                    }
                    "isOnline" ->  {
                        result.success(isOnline(this))
                    }
                    else -> result.notImplemented()
                }
            }
    }


    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            // Below Android 13, permission not needed
            true
        }
    }

    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null
    }

    private fun requestNotificationPermission(result: MethodChannel.Result) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isNotificationPermissionGranted()) {
                permissionResultPending = result
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            } else {
                result.success(true)
            }
        } else {
            result.success(true)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionResultPending?.success(true)
            } else {
                permissionResultPending?.success(false)
            }
            permissionResultPending = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleWeatherUpdates(context: Context, intervalMinutes: Long) {
        // Cancel existing work first to avoid duplicates
        WorkManager.getInstance(context).cancelUniqueWork("weather_update_work")

         // Define constraints - only run if network connected
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        

        // Create periodic work request
        val periodicWorkRequest = PeriodicWorkRequestBuilder<WeatherUpdateWorker>(
            intervalMinutes, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(5, TimeUnit.SECONDS) // initial quick start delay 
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "weather_update_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )

    }

    private fun cancelWeatherUpdates(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("weather_update_work")
    }
}
