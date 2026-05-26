package com.pranshulgg.weather_master_app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs.initPrefs
import com.pranshulgg.weather_master_app.data.provider.devicelocation.GetDeviceLocation
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        val startTime = System.currentTimeMillis()


        splashScreen.setKeepOnScreenCondition {
            val initialized = viewModel.uiState.value.isInitialized
            val timedOut = System.currentTimeMillis() - startTime > 5000

            !initialized && !timedOut
        }
        initPrefs(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherMasterApp()
        }
    }

    val locationHelper = GetDeviceLocation()
    override fun onPause() {
        super.onPause()
        locationHelper.stopUpdates()
    }

}


