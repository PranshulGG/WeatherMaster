package com.pranshulgg.weather_master_app

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs.initPrefs
import com.pranshulgg.weather_master_app.data.provider.devicelocation.GetDeviceLocation
import com.pranshulgg.weather_master_app.data.worker.WeatherWorker
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

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


