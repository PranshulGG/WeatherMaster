package com.pranshulgg.weather_master_app.feature.settings.appearance

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.tiles.DialogOption
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes
import com.pranshulgg.weather_master_app.feature.settings.appearance.components.ColorPickerBtn

@Composable
fun AppearanceScreen(navController: NavController) {

    val prefs = LocalAppPrefs.current
    val isAndroid12Plus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val appThemeOptions = listOf(
        DialogOption("Dark", stringResource(R.string.setting_dark_theme)),
        DialogOption("Light", stringResource(R.string.setting_light_theme)),
        DialogOption("System", stringResource(R.string.setting_system_theme))
    )

    LargeTopBarScaffold(
        title = stringResource(R.string.setting_appearance),
        navigationIcon = { NavigateUpBtn(navController) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingSection(
                title = stringResource(R.string.setting_app_looks),
                tiles = listOf(
                    SettingTile.DialogOptionTile(
                        leading = { SettingsTileIcon(R.drawable.palette_24px) },
                        title = stringResource(R.string.setting_app_theme),
                        options = appThemeOptions,
                        selectedOption = prefs.appTheme,
                        onOptionSelected = {
                            prefs.setAppTheme(it)
                        }
                    ),
                    SettingTile.SwitchTile(
                        leading = {
                            if (prefs.isCustomTheme) ColorPickerBtn() else SettingsTileIcon(
                                R.drawable.brush_24px
                            )
                        },
                        title = stringResource(R.string.setting_custom_color),
                        description = stringResource(R.string.setting_custom_color_secondary),
                        checked = prefs.isCustomTheme,
                        enabled = !prefs.isDynamicTheme,
                        onCheckedChange = { checked ->
                            prefs.setUseCustomTheme(checked)
                            if (!checked) {
                                prefs.setCustomThemeColor("#2196f3")
                            }
                        }
                    ),
                    SettingTile.SwitchTile(
                        leading = {
                            SettingsTileIcon(
                                R.drawable.photo_24px
                            )
                        },
                        title = stringResource(R.string.setting_dynamic_colors),
                        description = stringResource(R.string.setting_dynamic_colors_secondary),
                        checked = prefs.isDynamicTheme,
                        enabled = isAndroid12Plus && !prefs.isCustomTheme,
                        onCheckedChange = { checked ->
                            prefs.setDynamicColor(checked)
                        }
                    ),
                )
            )

            SettingSection(
                title = "Weather",
                tiles = listOf(
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.tune_24px) },
                        title = stringResource(R.string.setting_units),
                        description = stringResource(R.string.setting_units_secondary),
                        onClick = { navController.navigate(NavRoutes.UNITS) }
                    ),

                    SettingTile.SwitchTile(
                        leading = { SettingsTileIcon(R.drawable.animation_24px) },
                        title = stringResource(R.string.setting_weather_animations),
                        description = stringResource(R.string.setting_weather_animations_secondary),
                        checked = prefs.isShowWeatherAnimations,
                        onCheckedChange = { checked ->
                            prefs.setShowWeatherAnimations(checked)
                        }
                    ),
                    SettingTile.SwitchTile(
                        leading = { SettingsTileIcon(R.drawable.gradient_24px) },
                        title = stringResource(R.string.setting_weather_based_theme),
                        description = stringResource(R.string.setting_weather_based_theme_secondary),
                        checked = prefs.isWeatherBasedTheme,
                        onCheckedChange = { checked ->
                            prefs.setIsWeatherBasedTheme(checked)
                        }
                    ),
                    SettingTile.SwitchTile(
                        leading = { SettingsTileIcon(R.drawable.article_24px) },
                        title = stringResource(R.string.setting_day_summary),
                        description = stringResource(R.string.setting_day_summary_secondary),
                        checked = prefs.isShowSummary,
                        onCheckedChange = { checked ->
                            prefs.setShowSummary(checked)
                        }
                    ),
                )
            )

            SettingSection(
                title = stringResource(R.string.setting_layout),
                tiles = listOf(
                    SettingTile.SwitchTile(
                        leading = { SettingsTileIcon(R.drawable.froggy_head_48px) },
                        title = stringResource(R.string.setting_froggy_layout),
                        description = stringResource(R.string.setting_froggy_layout_secondary),
                        checked = prefs.isFroggyLayout,
                        onCheckedChange = { checked ->
                            prefs.setFroggyLayout(checked)
                        }
                    )
                )
            )

            SettingSection(
                title = "Time",
                tiles = listOf(
                    SettingTile.SwitchTile(
                        leading = { SettingsTileIcon(R.drawable.schedule_48px) },
                        title = "Use 24hr time format",
                        checked = prefs.is24HrTimeFormat,
                        onCheckedChange = { checked ->
                            prefs.set24HrTimeFormat(checked)
                        }
                    )
                )
            )

            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)
        }
    }

}

