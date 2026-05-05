package com.pranshulgg.weathermaster

import android.Manifest
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.pranshulgg.weathermaster.core.prefs.AppPrefs.initPrefs
import com.pranshulgg.weathermaster.data.provider.getDeviceLocation
import com.pranshulgg.weathermaster.feature.shared.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.uiState.value.weather == null
        }
        initPrefs(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WeatherMasterApp()
        }
    }
}
