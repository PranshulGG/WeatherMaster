package com.pranshulgg.weathermaster.core.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.pranshulgg.weathermaster.feature.main.MainScreen
import com.pranshulgg.weathermaster.feature.search.SearchScreen
import com.pranshulgg.weathermaster.feature.settings.SettingsScreen
import com.pranshulgg.weathermaster.feature.settings.appearance.AppearanceScreen
import com.pranshulgg.weathermaster.feature.settings.language.LanguageScreen

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {

    Box(
        Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = "root",
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer),
            enterTransition = { NavTransitions.enter() },
            exitTransition = { NavTransitions.exit() },
            popEnterTransition = { NavTransitions.popEnter() },
            popExitTransition = { NavTransitions.popExit() }
        ) {
            navigation(
                route = "root",
                startDestination = NavRoutes.MAIN // DEFAULT MAIN
            ) {
                composable(
                    NavRoutes.MAIN
                ) {
                    MainScreen(navController)
                }
                composable(
                    NavRoutes.SEARCH
                ) {
                    SearchScreen(navController)
                }
                composable(
                    NavRoutes.SETTINGS
                ) {
                    SettingsScreen(navController)
                }
                composable(
                    NavRoutes.APPEARANCE
                ) {
                    AppearanceScreen(navController)
                }
                composable(
                    NavRoutes.LANGUAGE
                ) {
                    LanguageScreen(navController)
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding()
                )
        )

    }

}