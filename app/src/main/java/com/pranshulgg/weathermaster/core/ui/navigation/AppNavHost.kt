package com.pranshulgg.weathermaster.core.ui.navigation

import android.R.attr.defaultValue
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.pranshulgg.weathermaster.feature.daily.DailyScreen
import com.pranshulgg.weathermaster.feature.main.MainScreen
import com.pranshulgg.weathermaster.feature.search.SearchScreen
import com.pranshulgg.weathermaster.feature.settings.SettingsScreen
import com.pranshulgg.weathermaster.feature.settings.appearance.AppearanceScreen
import com.pranshulgg.weathermaster.feature.settings.language.LanguageScreen
import com.pranshulgg.weathermaster.feature.settings.units.UnitsScreen

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
                startDestination = NavRoutes.MAIN
//                startDestination = NavRoutes.daily(0, "1f733854-a3fa-485f-85c7-25d292b086e2")
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
                composable(
                    NavRoutes.UNITS
                ) {
                    UnitsScreen(navController)
                }
                composable(
                    route = "${NavRoutes.DAILY}/{index}/{locationId}",
                    arguments = listOf(
                        navArgument("index") {
                            type = NavType.IntType
                            defaultValue = 0
                        },
                        navArgument("locationId") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val index = backStackEntry.arguments?.getInt("index") ?: 0
                    val locationId = backStackEntry.arguments?.getString("locationId")

                    DailyScreen(navController, index, locationId!!)
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