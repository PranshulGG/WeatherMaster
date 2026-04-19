package com.pranshulgg.weathermaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pranshulgg.weathermaster.core.prefs.AppPrefs.initPrefs
import com.pranshulgg.weathermaster.core.ui.theme.WeatherMasterTheme
import com.pranshulgg.weathermaster.feature.locations.LocationsScreenViewModel
import com.pranshulgg.weathermaster.feature.shared.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        initPrefs(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WeatherMasterApp()
        }
    }
}
