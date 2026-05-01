package com.pranshulgg.weathermaster

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.toColorInt
import androidx.navigation.compose.rememberNavController
import com.pranshulgg.weathermaster.core.prefs.AppPrefs
import com.pranshulgg.weathermaster.core.prefs.LocalAppPrefs
import com.pranshulgg.weathermaster.core.ui.navigation.AppNavHost
import com.pranshulgg.weathermaster.core.ui.snackbar.LocalSnackbarHostState
import com.pranshulgg.weathermaster.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weathermaster.core.ui.theme.WeatherMasterTheme
import com.pranshulgg.weathermaster.core.ui.theme.isThemeDark
import com.pranshulgg.weathermaster.feature.shared.WeatherViewModel
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
                message = context.getString(event.messageResource),
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


        WeatherMasterTheme(
            darkTheme = isThemeDark(),
            dynamicColor = true
        ) {

            AppNavHost(
                navController,
                snackbarHostState
            )

        }
    }

}