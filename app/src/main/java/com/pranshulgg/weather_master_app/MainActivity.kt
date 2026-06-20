package com.pranshulgg.weather_master_app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs.initPrefs
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.core.ui.theme.isThemeDark
import com.pranshulgg.weather_master_app.data.provider.devicelocation.GetDeviceLocation
import com.pranshulgg.weather_master_app.data.worker.WeatherWorker
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

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
        val theme = PreferencesHelper.getString("app_theme") ?: "Dark"

        val isDark = resolveThemeDark(
            theme,
            resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES
        )

        enableEdgeToEdge(
            navigationBarStyle = if (isDark) {
                SystemBarStyle.dark(Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(
                    Color.TRANSPARENT, Color.TRANSPARENT
                )
            }
        )

        setContent {
            WeatherMasterApp()
        }

    }

    val locationHelper = GetDeviceLocation()
    override fun onPause() {
        super.onPause()
        locationHelper.stopUpdates()
    }


    private fun resolveThemeDark(
        appTheme: String,
        systemDark: Boolean
    ): Boolean {
        return when (appTheme) {
            "Dark" -> true
            "Light" -> false
            "System" -> systemDark
            else -> systemDark
        }
    }


}


