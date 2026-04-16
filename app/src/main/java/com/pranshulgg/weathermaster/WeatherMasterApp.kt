package com.pranshulgg.weathermaster

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.navigation.compose.rememberNavController
import com.pranshulgg.weathermaster.core.prefs.AppPrefs
import com.pranshulgg.weathermaster.core.prefs.LocalAppPrefs
import com.pranshulgg.weathermaster.core.ui.navigation.AppNavHost
import com.pranshulgg.weathermaster.core.ui.snackbar.LocalSnackbarHostState
import com.pranshulgg.weathermaster.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weathermaster.core.ui.theme.WeatherMasterTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherMasterApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        SnackbarManager.events.collectLatest { event ->
            snackbarHostState.currentSnackbarData?.dismiss()

            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.actionLabel,
                withDismissAction = event.actionLabel == null,
                duration = SnackbarDuration.Short
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

        val appTheme = when (prefs.appTheme) {
            "Dark" -> true
            "Light" -> false
            "System" -> isSystemInDarkTheme()
            else -> isSystemInDarkTheme()
        }

        WeatherMasterTheme(
            darkTheme = true,
            dynamicColor = prefs.isDynamicTheme,
            seedColor = Color(prefs.customThemeColor.toColorInt())
        ) {

            AppNavHost(
                navController,
                snackbarHostState
            )

        }
    }

}