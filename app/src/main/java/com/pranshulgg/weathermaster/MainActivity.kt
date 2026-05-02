package com.pranshulgg.weathermaster

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.pranshulgg.weathermaster.core.prefs.AppPrefs.initPrefs
import com.pranshulgg.weathermaster.feature.settings.appearance.setLanguage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        initPrefs(this)
        val pref = this.getSharedPreferences("language_pref", MODE_PRIVATE)
        setLanguage(this, pref.getString("language", null) ?: "en")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WeatherMasterApp()
        }
    }
}
