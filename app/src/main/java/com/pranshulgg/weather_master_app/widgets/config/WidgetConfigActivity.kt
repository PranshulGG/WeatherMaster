package com.pranshulgg.weather_master_app.widgets.config

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.lifecycleScope
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs.initPrefs
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.snackbar.LocalSnackbarHostState
import com.pranshulgg.weather_master_app.core.ui.theme.ThemeVariantType
import com.pranshulgg.weather_master_app.core.ui.theme.WeatherMasterTheme
import com.pranshulgg.weather_master_app.core.ui.theme.isThemeDark
import com.pranshulgg.weather_master_app.data.worker.widgets.WeatherWidgetUpdater
import com.pranshulgg.weather_master_app.widgets.glance.GlanceWidgetReceiver
import com.pranshulgg.weather_master_app.widgets.glance.ui.GlanceWidgetConfig
import com.pranshulgg.weather_master_app.widgets.weatherclockdaily.ClockDailyWidgetReceiver
import com.pranshulgg.weather_master_app.widgets.weatherclockdaily.ui.ClockDailyWidgetConfig
import kotlinx.coroutines.launch

class WidgetConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPrefs(this)
        enableEdgeToEdge()

        val widgetId =
            intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        val provider =
            AppWidgetManager
                .getInstance(this)
                .getAppWidgetInfo(widgetId)
                ?.provider
        setResult(RESULT_CANCELED)

        val updater = WeatherWidgetUpdater(this)

        val onDone: (WidgetConfig) -> Unit = {
            lifecycleScope.launch {

                updater.saveWidgetConfig(
                    context = this@WidgetConfigActivity,
                    widgetId,
                    it
                )

                setResult(
                    RESULT_OK,
                    Intent().apply {
                        putExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            widgetId
                        )
                    }
                )

                finish()
            }
        }

        setContent {
            CompositionLocalProvider(
                LocalAppPrefs provides AppPrefs.state()
            ) {

                WeatherMasterTheme(
                    isThemeDark(),
                    dynamicTheme = true,
                    themeVariantType = ThemeVariantType.EXPRESSIVE,
                ) {
                    when (provider?.className) {
                        GlanceWidgetReceiver::class.java.name -> {
                            GlanceWidgetConfig(onDone = { onDone(it) })
                        }

                        ClockDailyWidgetReceiver::class.java.name -> {
                            ClockDailyWidgetConfig(onDone = { onDone(it) })
                        }
                    }
                }
            }
        }
    }
}

