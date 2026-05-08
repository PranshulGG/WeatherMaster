package com.pranshulgg.weathermaster

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.pranshulgg.weathermaster.core.prefs.AppPrefs.initPrefs
import com.pranshulgg.weathermaster.data.provider.GetDeviceLocation
import com.pranshulgg.weathermaster.feature.shared.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            val state = viewModel.uiState.value
            !state.isInitialized
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


