package com.pranshulgg.weathermaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.pranshulgg.weathermaster.core.prefs.AppPrefs.initPrefs
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
