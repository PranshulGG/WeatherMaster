package com.pranshulgg.weather_master_app.core.ui.navigation

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
import com.pranshulgg.weather_master_app.feature.daily.DailyScreen
import com.pranshulgg.weather_master_app.feature.main.MainScreen
import com.pranshulgg.weather_master_app.feature.search.SearchScreen
import com.pranshulgg.weather_master_app.feature.settings.SettingsScreen
import com.pranshulgg.weather_master_app.feature.settings.about.AboutScreen
import com.pranshulgg.weather_master_app.feature.settings.about.license.LicenseScreen
import com.pranshulgg.weather_master_app.feature.settings.about.privacy.PrivacyPolicyScreen
import com.pranshulgg.weather_master_app.feature.settings.about.terms.TermsConditionsScreen
import com.pranshulgg.weather_master_app.feature.settings.appearance.AppearanceScreen
import com.pranshulgg.weather_master_app.feature.settings.background.BackgroundUpdatesScreen
import com.pranshulgg.weather_master_app.feature.settings.language.LanguageScreen
import com.pranshulgg.weather_master_app.feature.settings.sources.WeatherSourcesScreen
import com.pranshulgg.weather_master_app.feature.settings.units.UnitsScreen

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
                    NavRoutes.BACKGROUND_UPDATES
                ) {
                    BackgroundUpdatesScreen(navController)
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
                composable(
                    NavRoutes.ABOUT
                ) {
                    AboutScreen(navController)
                }

                composable(
                    NavRoutes.TERMS_CONDITIONS
                ) {
                    TermsConditionsScreen(navController)
                }
                composable(
                    NavRoutes.PRIVACY_POLICY
                ) {
                    PrivacyPolicyScreen(navController)
                }
                composable(
                    NavRoutes.LICENSE
                ) {
                    LicenseScreen(navController)
                }
                composable(
                    NavRoutes.SOURCES
                ) {
                    WeatherSourcesScreen(navController)
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