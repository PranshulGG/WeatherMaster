package com.pranshulgg.weather_master_app

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.toColorInt
import androidx.navigation.compose.rememberNavController
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.navigation.AppNavHost
import com.pranshulgg.weather_master_app.core.ui.snackbar.LocalSnackbarHostState
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.core.ui.theme.WeatherMasterTheme
import com.pranshulgg.weather_master_app.core.ui.theme.isThemeDark
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherMasterApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        SnackbarManager.events.collectLatest { event ->
            snackbarHostState.currentSnackbarData?.dismiss()

            val result = snackbarHostState.showSnackbar(
                message = context.getString(
                    event.messageResource,
                    event.messageArgs
                ),
                actionLabel = if (event.actionLabel != null) context.getString(event.actionLabel) else null,
                withDismissAction = event.actionLabel == null,
                duration = event.duration
            )

            if (result == SnackbarResult.ActionPerformed) {
                event.onAction?.invoke()
            }
        }
    }

    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState,
        LocalAppPrefs provides AppPrefs.state()
    ) {
        val prefs = LocalAppPrefs.current


        WeatherMasterTheme(
            darkTheme = isThemeDark(),
            themeVariantType = prefs.themeVariantType,
            dynamicTheme = prefs.isDynamicTheme,
            seedColor = Color(prefs.customThemeColor.toColorInt())
        ) {

            AppNavHost(
                navController,
                snackbarHostState
            )

        }
    }


}